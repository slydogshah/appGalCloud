import 'package:app/src/model/location.dart';

import '../model/profile.dart';
import '../rest/profileRestClient.dart';

class ActiveSession
{
  static final ActiveSession singleton = new ActiveSession();

  Profile profile;
  Location location;

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

  void activate()
  {
      ProfileRestClient profileRestClient = new ProfileRestClient();
      profileRestClient.setProfile(this);
      //print(this.profile.getLatitude());
  }


  static ActiveSession getInstance()
  {
    return ActiveSession.singleton;
  }
}