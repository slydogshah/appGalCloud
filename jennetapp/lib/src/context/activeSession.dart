import 'package:app/src/context/securityToken.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunner.dart';
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
  FoodRunner foodRunner;

  SecurityToken securityToken;

  ActiveSession();

  Profile getProfile()
  {
    return this.profile;
  }

  void setProfile(Profile profile)
  {
    this.profile = profile;
    this.foodRunner = new FoodRunner(this.profile);
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
      box.write('bearerToken', profile.bearerToken);
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
    final String bearerToken = box.read<String>('bearerToken');
    final String password = box.read<String>('password');
    final double latitude = box.read<double>('latitude');
    final double longitude = box.read<double>('longitude');

    storedCredentials['email'] = email;
    storedCredentials['bearerToken'] = bearerToken;
    storedCredentials['password'] = password;
    storedCredentials['latitude'] = latitude;
    storedCredentials['longitude'] = longitude;

    return storedCredentials;
  }
}