import 'package:app/src/model/location.dart';

import '../model/profile.dart';

class ActiveSession
{
  static final ActiveSession singleton = new ActiveSession();

  Profile profile;
  Location location;
  String environment;

  ActiveSession();

  Profile getProfile()
  {
    return this.profile;
  }

  void setProfile(Profile profile)
  {
    this.profile = profile;
  }

  Location getLocation()
  {
    return this.location;
  }

  void setLocation(Location location)
  {
    this.location = location;
  }

  void setEnvironment(String environment)
  {
    this.environment = environment;
  }

  String getEnvironment()
  {
    return this.environment;
  }

  static ActiveSession getInstance()
  {
    return ActiveSession.singleton;
  }
}