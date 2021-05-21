import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:okito/okito.dart';

import '../model/profile.dart';

class ActiveSession
{
  static final ActiveSession singleton = new ActiveSession();

  Profile profile;
  FoodRunnerLocation location;
  FoodRecoveryTransaction current;

  ActiveSession();

  Profile getProfile()
  {
    return this.profile;
  }

  void setProfile(Profile profile)
  {
    this.profile = profile;
    this.storeCredentials(profile);
  }

  FoodRunnerLocation getLocation()
  {
    return this.location;
  }

  void setLocation(FoodRunnerLocation location)
  {
    this.location = location;
    this.storeLocation(location);
  }

  static ActiveSession getInstance()
  {
    return ActiveSession.singleton;
  }

  void storeCredentials(Profile profile) async
  {
      // Usage
      final box = OkitoStorage; // For easier reference.

      box.write('email', profile.email);
      box.write('password', profile.password);
  }

  void storeLocation(FoodRunnerLocation location) async
  {
    // Usage
    final box = OkitoStorage; // For easier reference.

    box.write('latitude', location.latitude);
    box.write('longitude', location.longitude);
  }

  Future<Map<String,dynamic>> readCredentials() async
  {
    Map<String,dynamic> storedCredentials = new Map();
    await OkitoStorage.init();

    // Usage
    final box = OkitoStorage; // For easier reference.


    final String email = box.read<String>('email');
    final String password = box.read<String>('password');
    final double latitude = box.read<double>('latitude');
    final double longitude = box.read<double>('longitude');

    storedCredentials['email'] = email;
    storedCredentials['password'] = password;
    storedCredentials['latitude'] = latitude;
    storedCredentials['longitude'] = longitude;

    return storedCredentials;
  }
}