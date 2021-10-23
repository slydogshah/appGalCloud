import 'dart:async';
import 'package:app/src/context/activeSession.dart';
import 'package:location/location.dart';

import 'package:app/src/model/profile.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

class LocationUpdater
{
  Location location;
  bool isBackgroundModeEnabled;
  ActiveNetworkRestClient activeNetworkClient= new ActiveNetworkRestClient();

  static LocationUpdater singleton;

  static void start() async{
    if(singleton == null){
      singleton = new LocationUpdater();
    }
    singleton.location = new Location();
    singleton.isBackgroundModeEnabled =
        await singleton.location.enableBackgroundMode(enable: true);
  }

  static Future<FoodRunnerLocation> getLocation() async
  {
    LocationData data = await singleton.location.getLocation();
    FoodRunnerLocation foodRunnerLocation = new FoodRunnerLocation(data.latitude,data.longitude);
    ActiveSession.getInstance().setLocation(foodRunnerLocation);
    FoodRunnerLocation active = ActiveSession.getInstance().getLocation();
    //print("FOOD_LOCATION_RETURNED: $active");
    return active;
  }

  static void startPolling(Profile profile) async
  {
    //print("***********LOCATION_BACKGROUND_MODE_ENABLED_DURING_POLLING**********");
    //print(singleton.isBackgroundModeEnabled);

    singleton.location.onLocationChanged.listen((LocationData currentLocation) {
      //print("LOCATION_UPDATE_RECEIVED: $currentLocation.latitude,$currentLocation.longitude");

      // Use current location
      Map<String,double> map = new Map();

      map['latitude'] = currentLocation.latitude;
      map['longitude'] = currentLocation.longitude;
      LocationData location = LocationData.fromMap(map);
      Future<String> future = singleton.activeNetworkClient.sendLocationUpdate(location);
      future.then((result){}).catchError((error){});
    });
  }
}