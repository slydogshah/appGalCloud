import 'dart:async';

import 'package:app/src/model/location.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/ui/map_icons.dart';
import 'package:app/src/ui/uiFunctions.dart';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class LandingScene extends StatefulWidget {
  Profile profile;
  LandingSceneState landingSceneState;

  LandingScene(Profile profile)
  {
    this.profile = profile;
    this.landingSceneState = new LandingSceneState(this.profile);
  }

  @override
  LandingSceneState createState() => this.landingSceneState;

  LandingSceneState getLandingSceneState()
  {
    return this.landingSceneState;
  }
}

class LandingSceneState extends State<LandingScene> {
  Completer<GoogleMapController> mapController = Completer();

  static final CameraPosition _kLake = CameraPosition(
      bearing: 192.8334901395799,
      target: LatLng(37.43296265331129, -122.08832357078792),
      tilt: 59.440717697143555,
      zoom: 19.151926040649414);

  LatLng _center;

  LandingSceneState(Profile profile)
  {
    Location location = profile.getLocation();
    this._center = LatLng(location.latitude, location.longitude);
  }

  void _onMapCreated(GoogleMapController controller) {
    //mapController = controller as Completer<GoogleMapController>;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('Maps Sample App'),
          backgroundColor: Colors.green[700],
        ),
        body: GoogleMap(
          onMapCreated: _onMapCreated,
          initialCameraPosition: CameraPosition(
            target: _center,
            zoom: 11.0,
          ),
        ),
        bottomNavigationBar: UiFunctions.bottomNavigationBar(context)
      ),
    );
  }

  void map()
  {
    Navigator.push(context,MaterialPageRoute(builder: (context) => MarkerIconsPage()));
  }

  Future<void> goToTheLake() async {

    print("*******");
    print("GO_TO_THE_LAKE");
    print("*******");

    //final GoogleMapController controller = await mapController.future;
    //controller.animateCamera(CameraUpdate.newCameraPosition(_kLake));
  }
}