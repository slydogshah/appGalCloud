import '../model/profile.dart';
import '../rest/profileRestClient.dart';

class ActiveSession
{
  static final ActiveSession singleton = new ActiveSession();

  Profile profile;

  ActiveSession()
  {
    this.load();
  }

  Profile getProfile()
  {
    return this.profile;
  }

  void load()
  {
    ProfileRestClient restClient = new ProfileRestClient();
    Future<Profile> profileFuture = restClient.getProfile();
    profileFuture.then((value)=> this.profile = value);
  }

  static ActiveSession getInstance()
  {
    return ActiveSession.singleton;
  }
}