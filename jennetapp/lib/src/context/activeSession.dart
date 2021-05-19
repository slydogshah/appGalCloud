import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/rest/urlFunctions.dart';

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
}