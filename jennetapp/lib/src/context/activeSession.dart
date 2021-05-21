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

  Future<Map<String,String>> readCredentials() async
  {
    Map<String,String> storedCredentials = new Map();
    await OkitoStorage.init();

    // Usage
    final box = OkitoStorage; // For easier reference.


    final String email = box.read<String>('email');
    final String password = box.read<String>('password');

    storedCredentials['email'] = email;
    storedCredentials['password'] = password;

    return storedCredentials;
  }
}