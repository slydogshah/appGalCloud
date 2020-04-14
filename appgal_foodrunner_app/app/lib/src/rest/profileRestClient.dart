import 'package:http/http.dart' as http;
import 'package:http/http.dart';
import 'dart:convert';

import '../model/profile.dart';
import '../context/activeSession.dart';

class ProfileRestClient
{
  void setProfile(ActiveSession activeSession)
  {
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/";
    http.get(remoteUrl).then((response) {
      String profileJson = response.body;
      Map<String, dynamic> map = jsonDecode(profileJson);
      Profile profile = Profile.fromJson(map);
      print(profile);
      activeSession.setProfile(profile);
    });
  }

  void register(Profile profile)
  {
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/";
    http.post(remoteUrl, body: profile.toString());
  }

  Future<Profile> getProfile(String email) async
  {
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/?email="+email;
    var response = await http.get(remoteUrl);
    String profileJson = response.body;
    Profile profile = Profile.fromJson(jsonDecode(profileJson));
    return profile;
  }

  Future<String> getActiveView() async
  {
    //String remoteUrl = "http://10.0.2.2:8080/activeNetwork/activeView/";
    String remoteUrl = "http://localhost:8080/activeNetwork/activeView/";
    var response = await http.get(remoteUrl);
    String activeViewJson = response.body;
    return activeViewJson;
  }

}