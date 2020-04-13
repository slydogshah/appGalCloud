import 'package:http/http.dart' as http;
import 'dart:convert';

import '../model/profile.dart';
import '../context/activeSession.dart';

class ProfileRestClient
{
  void setProfile(ActiveSession activeSession)
  {
    /*Profile profile = new Profile("CLOUD_ID","blah@blah.com","8675309","photu");
    activeSession.setProfile(profile);
    print("****HERE***");
    print(activeSession.getProfile());
    print("****HERE***");*/

    print("START");
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/";
    http.get(remoteUrl).then((response) {
      print("BACK");
      String profileJson = response.body;
      Map<String, dynamic> map = jsonDecode(profileJson);
      Profile profile = Profile.fromJson(map);
      print(profile);
      activeSession.setProfile(profile);
    });
    print("LEAVING");
  }

  void register(Profile profile)
  {
    /*Profile profile = new Profile("CLOUD_ID","blah@blah.com","8675309","photu");
    activeSession.setProfile(profile);
    print("****HERE***");
    print(activeSession.getProfile());
    print("****HERE***");*/

    print("START");
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/";
    http.post(remoteUrl, body: profile.toString()).then((response) {
      print("BACK");
      String body = response.body;
      print(body);
    });
    print("LEAVING");
  }

  Profile getProfile(String email)
  {
    /*Profile profile = new Profile("CLOUD_ID","blah@blah.com","8675309","photu");
    activeSession.setProfile(profile);
    print("****HERE***");
    print(activeSession.getProfile());
    print("****HERE***");*/

    print("START");
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/?email="+email;
    http.get(remoteUrl).then((response) {
      print("BACK");
      String profileJson = response.body;
      print("***GET_PROFILE***");
      print(profileJson);
      print("******");
      Map<String, dynamic> map = jsonDecode(profileJson);
      Profile profile = Profile.fromJson(map);
      return profile;
    });
    print("LEAVING");
  }
}