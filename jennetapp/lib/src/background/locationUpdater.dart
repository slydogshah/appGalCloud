import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

import 'package:background_fetch/background_fetch.dart';

import 'package:shared_preferences/shared_preferences.dart';

import 'package:location/location.dart';

class LocationUpdater
{
  static String scheduledTaskId = "com.transistersoft.io.appgallabs.jen.network";
  static String standardTaskId = "com.transistorsoft.fetch";

  static Location location = new Location();
  static bool _serviceEnabled;
  static PermissionStatus _permissionGranted;
  static LocationData _locationData;

  static void startPolling(Profile profile) async
  {
      if(Platform.isIOS)
      {
        startIOSPolling(profile);
      }
      else if(Platform.isAndroid)
      {
        startAndroidPolling(profile);
      }
  }
  //--------ios--------------------------------------------
  static void startIOSPolling(Profile profile) async
  {
    print("********START**********");
    print("LOCATION_DATA: $_locationData");

    _serviceEnabled = await location.serviceEnabled();
    if (!_serviceEnabled) {
      _serviceEnabled = await location.requestService();
      if (!_serviceEnabled) {
        print("******************");
        print("SERVICE_DISABLED");
        return;
      }
    }

    _permissionGranted = await location.hasPermission();
    if (_permissionGranted == PermissionStatus.denied) {
      _permissionGranted = await location.requestPermission();
      if (_permissionGranted != PermissionStatus.granted) {
        print("******************");
        print("PERMISSION_DENIED");
        return;
      }
    }

    print("********POLL**********");
    Future<LocationData> locationData = location.getLocation();
    locationData.then((data){
      print("******************");
      print(data.latitude);
      print(data.longitude);
    });
  }
  //--------android----------------------------------------
  static void startAndroidPolling(Profile profile) async
  {
    print("********START**********");
    print("LOCATION_DATA: $_locationData");

    _serviceEnabled = await location.serviceEnabled();
    if (!_serviceEnabled) {
      _serviceEnabled = await location.requestService();
      if (!_serviceEnabled) {
        print("******************");
        print("SERVICE_DISABLED");
        return;
      }
    }

    _permissionGranted = await location.hasPermission();
    if (_permissionGranted == PermissionStatus.denied) {
      _permissionGranted = await location.requestPermission();
      if (_permissionGranted != PermissionStatus.granted) {
        print("******************");
        print("PERMISSION_DENIED");
        return;
      }
    }

    print("********POLL**********");
    Future<LocationData> locationData = location.getLocation();
    locationData.then((data){
      print("******************");
      print(data.latitude);
      print(data.longitude);
    });
  }
}