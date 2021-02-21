/*import 'package:app/src/ui/login.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

void main()
{
  //Launch the App
  runApp(new JenNetworkApp());
}*/

import 'package:flutter/material.dart';
import 'package:location/location.dart';

import 'package:app/src/ui/login.dart';

Future<void> main() async {
  Location location = new Location();

  bool _serviceEnabled;
  PermissionStatus _permissionGranted;
  LocationData _locationData;

  WidgetsFlutterBinding.ensureInitialized();

  runApp(new JenNetworkApp());

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
