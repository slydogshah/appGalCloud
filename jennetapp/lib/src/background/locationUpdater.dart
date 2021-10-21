import 'dart:async';
import 'dart:io';
import 'package:app/src/context/activeSession.dart';
import 'package:location/location.dart';

import 'package:app/src/model/profile.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

class LocationUpdater
{
  static String scheduledTaskId = "com.transistersoft.io.appgallabs.jen.network";
  static String standardTaskId = "com.transistorsoft.fetch";

  static Location location = new Location();
  static bool _serviceEnabled;
  static bool isBackgroundModeEnabled;
  static PermissionStatus _permissionGranted;
  static LocationData _locationData;
  static ActiveNetworkRestClient activeNetworkClient= new ActiveNetworkRestClient();

  static void start() async{
    //_permissionGranted = await location.requestPermission();
    isBackgroundModeEnabled = await location.isBackgroundModeEnabled();
    print("***********LOCATION_BACKGROUND_MODE_ENABLED**********");
    print(isBackgroundModeEnabled);
  }

  static Future<FoodRunnerLocation> getLocation() async
  {
    _serviceEnabled = await location.serviceEnabled();
    if (!_serviceEnabled) {
      _serviceEnabled = await location.requestService();
    }

    LocationData data = await location.getLocation();
    FoodRunnerLocation foodRunnerLocation = new FoodRunnerLocation(data.latitude,data.longitude);
    ActiveSession.getInstance().setLocation(foodRunnerLocation);
    FoodRunnerLocation active = ActiveSession.getInstance().getLocation();
    print("FOOD_LOCATION_RETURNED: $active");
    return active;
  }

  static void startPolling(Profile profile) async
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

    if(!isBackgroundModeEnabled) {
      location.enableBackgroundMode(enable: true);
    }

    //Start receiving location updates
    /*Future<LocationData> locationData = location.getLocation();
      locationData.then((data){
        Map<String,double> map = new Map();

        map['latitude'] = data.latitude;
        map['longitude'] = data.longitude;
        LocationData location = LocationData.fromMap(map);
        Future<String> future = activeNetworkClient.sendLocationUpdate(location);
        future.then((result){}).catchError((error){});
      });*/
  }
}