import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/applicableSources.dart';
import 'package:app/src/ui/pickupSource.dart';
import 'package:app/src/ui/uiFunctions.dart';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class LandingScene extends StatefulWidget {
  Profile profile;

  LandingScene(Profile profile)
  {
    this.profile = profile;
  }

  @override
  LandingSceneState createState() => LandingSceneState(this.profile);
}

class LandingSceneState extends State<LandingScene> {
  GoogleMapController mapController;
  LatLng _center;

  LandingSceneState(Profile profile)
  {
    Location location = profile.getLocation();
    this._center = LatLng(location.latitude, location.longitude);
  }

  void _onMapCreated(GoogleMapController controller) {
    mapController = controller;
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
}