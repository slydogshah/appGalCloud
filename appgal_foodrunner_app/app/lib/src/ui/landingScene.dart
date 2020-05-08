import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/profile.dart';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

class LandingScene extends StatefulWidget {
  @override
  LandingSceneState createState() => LandingSceneState();
}

class LandingSceneState extends State<LandingScene> {
  GoogleMapController mapController;
  LatLng _center;

  LandingSceneState()
  {
    Profile profile = ActiveSession.getInstance().getProfile();
    //print("LANDING"+profile.toString());
    this._center = LatLng(profile.getLatitude(), profile.getLongitude());
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
        bottomNavigationBar: BottomNavigationBar(
          currentIndex: 0, // this will be set when a new tab is tapped
          items: [
            BottomNavigationBarItem(
              icon: new Icon(Icons.home),
              title: new Text('Home'),
            ),
            BottomNavigationBarItem(
              icon: new Icon(Icons.mail),
              title: new Text('Messages'),
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.person),
              title: Text('Profile')
            )
          ],
        )
      ),
    );
  }
}