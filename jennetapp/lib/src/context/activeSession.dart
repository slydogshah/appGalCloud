import 'package:app/src/model/location.dart';
import 'package:app/src/rest/urlFunctions.dart';

import '../model/profile.dart';

class ActiveSession
{
  static final ActiveSession singleton = new ActiveSession();

  Profile profile;
  Location location;
  String environment;
  String apiUrl;

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
    Future<String> config = UrlFunctions.getConfig();
    config.then((url) {
      this.apiUrl = url;
    });
  }

  String getEnvironment()
  {
    return this.environment;
  }

  String getApiUrl()
  {
    return this.apiUrl;
  }

  static ActiveSession getInstance()
  {
    return ActiveSession.singleton;
  }
}