import 'dart:async';
import 'dart:io';
import 'package:location/location.dart';

import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

class LocationUpdater
{
  static String scheduledTaskId = "com.transistersoft.io.appgallabs.jen.network";
  static String standardTaskId = "com.transistorsoft.fetch";

  static Location location = new Location();
  static bool _serviceEnabled;
  static PermissionStatus _permissionGranted;
  static LocationData _locationData;
  static ActiveNetworkRestClient activeNetworkClient= new ActiveNetworkRestClient();

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

    Future<LocationData> locationData = location.getLocation();
    locationData.then((data){
      Map<String,double> map = new Map();
      map['latitude'] = 0.0;
      map['longitude'] = 0.0;
      LocationData location = LocationData.fromMap(map);
      activeNetworkClient.sendLocationUpdate(location);
    });
  }
  //--------android----------------------------------------
  static void startAndroidPolling(Profile profile) async
  {
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

    Future<LocationData> locationData = location.getLocation();
    locationData.then((data){
      Map<String,double> map = new Map();
      map['latitude'] = 0.0;
      map['longitude'] = 0.0;
      LocationData location = LocationData.fromMap(map);
      activeNetworkClient.sendLocationUpdate(location);
    });
  }
}