import '../model/profile.dart';
import '../rest/profileRestClient.dart';

class ActiveSession
{
  static final ActiveSession singleton = new ActiveSession();

  Profile profile;

  ActiveSession();

  Profile getProfile()
  {
    return this.profile;
  }

  void setProfile(Profile profile)
  {
    this.profile = profile;
  }

  void activate()
  {
      ProfileRestClient profileRestClient = new ProfileRestClient();
      profileRestClient.setProfile(this);
  }


  static ActiveSession getInstance()
  {
    return ActiveSession.singleton;
  }
}