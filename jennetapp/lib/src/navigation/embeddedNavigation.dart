import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/foodRunner.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_mapbox_navigation/library.dart';

import 'dart:io' show Platform;

class EmbeddedNavigation
{
  String _platformVersion = 'Unknown';
  String _instruction = "";
  FoodRunnerLocation foodRunnerLocation;
  WayPoint _origin;
  WayPoint _stop;

  MapBoxNavigation _directions;
  MapBoxOptions _options;

  bool _arrived = false;
  bool _isMultipleStop = false;
  double _distanceRemaining, _durationRemaining;
  MapBoxNavigationViewController _controller;
  bool _routeBuilt = false;
  bool _isNavigating = false;
  BuildContext context;

  EmbeddedNavigation(BuildContext buildContext,SourceOrg sourceOrg)
  {
      this.context = buildContext;
      foodRunnerLocation = ActiveSession.getInstance().getLocation();

      //print(foodRunnerLocation.getLatitude());
      //print(foodRunnerLocation.getLongitude());
      //print(sourceOrg.location.getLatitude());
      //print(sourceOrg.location.getLongitude());


      if(Platform.isIOS) {
        _origin = WayPoint(
            name: "Start",
            latitude: 30.2698104,
            longitude: -97.75115579999999);
      }
      else
      {
        _origin = WayPoint(
            name: "Start",
            latitude: foodRunnerLocation.getLatitude(),
            longitude: foodRunnerLocation.getLongitude());
      }

      _stop = WayPoint(
          name: "Stop",
          latitude: sourceOrg.location.getLatitude(),
          longitude: sourceOrg.location.getLongitude());
  }


  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> start() async {
    _directions = MapBoxNavigation(onRouteEvent: _onEmbeddedRouteEvent);
    _options = MapBoxOptions(
        //initialLatitude: 36.1175275,
        //initialLongitude: -115.1839524,
        zoom: 15.0,
        tilt: 0.0,
        bearing: 0.0,
        enableRefresh: false,
        alternatives: true,
        voiceInstructionsEnabled: true,
        bannerInstructionsEnabled: true,
        allowsUTurnAtWayPoints: true,
        mode: MapBoxNavigationMode.drivingWithTraffic,
        units: VoiceUnits.imperial,
        simulateRoute: false,
        animateBuildRoute: true,
        longPressDestinationEnabled: true,
        language: "en");

    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await _directions.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    var wayPoints = List<WayPoint>();
                            wayPoints.add(_origin);
                            wayPoints.add(_stop);

                            print(wayPoints.isEmpty);

                            await _directions.startNavigation(
                                wayPoints: wayPoints,
                                options: MapBoxOptions(
                                    mode:
                                        MapBoxNavigationMode.drivingWithTraffic,
                                    simulateRoute: true,
                                    language: "en",
                                    units: VoiceUnits.metric));
  }


  Future<void> _onEmbeddedRouteEvent(e) async {
    _distanceRemaining = await _directions.distanceRemaining;
    _durationRemaining = await _directions.durationRemaining;

    switch (e.eventType) {
      case MapBoxEvent.progress_change:
        var progressEvent = e.data as RouteProgressEvent;
        _arrived = progressEvent.arrived;
        if (progressEvent.currentStepInstruction != null)
          _instruction = progressEvent.currentStepInstruction;
        break;
      case MapBoxEvent.route_building:
      case MapBoxEvent.route_built:
        break;
      case MapBoxEvent.route_build_failed:
        break;
      case MapBoxEvent.navigation_running:
        break;
      case MapBoxEvent.on_arrival:
        _arrived = true;
        if (!_isMultipleStop) {
          await Future.delayed(Duration(seconds: 3));
          //await _controller.finishNavigation();

          Profile foodRunner = ActiveSession.getInstance().getProfile();
          ActiveNetworkRestClient client = new ActiveNetworkRestClient();
          Future<List<FoodRecoveryTransaction>> future = client
              .getFoodRecoveryTransaction(foodRunner.email);
          future.then((txs) {
            Navigator.of(context, rootNavigator: true).pop();

            Navigator.push(context, MaterialPageRoute(
                builder: (context) => FoodRunnerMainScene(txs)));
          });
        } else {}
        break;
      case MapBoxEvent.navigation_finished:
        {
          Profile foodRunner = ActiveSession.getInstance().getProfile();
          ActiveNetworkRestClient client = new ActiveNetworkRestClient();
          Future<List<FoodRecoveryTransaction>> future = client
              .getFoodRecoveryTransaction(foodRunner.email);
          future.then((txs) {
            //Navigator.of(context, rootNavigator: true).pop();

            Navigator.push(context, MaterialPageRoute(
                builder: (context) => FoodRunnerMainScene(txs)));
          });
        }
        break;
      case MapBoxEvent.navigation_cancelled:
        {
          Profile foodRunner = ActiveSession.getInstance().getProfile();
          ActiveNetworkRestClient client = new ActiveNetworkRestClient();
          Future<List<FoodRecoveryTransaction>> future = client
              .getFoodRecoveryTransaction(foodRunner.email);
          future.then((txs) {
            //Navigator.of(context, rootNavigator: true).pop();

            Navigator.push(context, MaterialPageRoute(
                builder: (context) => FoodRunnerMainScene(txs)));
          });
        }
        break;
      default:
        break;
    }
  }
}