import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/pickupSource.dart';
import 'package:app/src/ui/uiFunctions.dart';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import 'applicableSources.dart';

class DriveToDestinationScene extends StatefulWidget {
  DriveToDestinationScene();

  @override
  DriveToDestinationSceneState createState() => DriveToDestinationSceneState();
}

class DriveToDestinationSceneState extends State<DriveToDestinationScene> {
  GoogleMapController mapController;
  final LatLng _center = const LatLng(45.521563, -122.677433);

  DriveToDestinationSceneState();

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