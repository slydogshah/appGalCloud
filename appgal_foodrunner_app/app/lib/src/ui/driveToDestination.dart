import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class DriveToDestinationScene extends StatefulWidget {
  AuthCredentials authCredentials;

  DriveToDestinationScene(DropOffNotification)
  {
    this.authCredentials = authCredentials;
  }

  @override
  DriveToDestinationSceneState createState() => DriveToDestinationSceneState(this.authCredentials);
}

class DriveToDestinationSceneState extends State<DriveToDestinationScene> {
  GoogleMapController mapController;
  final LatLng _center = const LatLng(45.521563, -122.677433);
  AuthCredentials authCredentials;

  DriveToDestinationSceneState(AuthCredentials authCredentials)
  {
    this.authCredentials = authCredentials;
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
      ),
    );
  }
}