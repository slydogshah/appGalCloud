import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:app/src/model/authCredentials.dart';
import '../model/profile.dart';
import '../context/activeSession.dart';

class ProfileRestClient
{
  Future<Profile> getProfile(String email) async
  {
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/";
    var response = await http.get(remoteUrl);
    String profileJson = response.body;
    Profile profile = Profile.fromJson(jsonDecode(profileJson));
    return profile;
  }

  void setProfile(ActiveSession activeSession) async
  {
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/";
    http.get(remoteUrl).then((response) {
      String profileJson = response.body;
      Map<String, dynamic> map = jsonDecode(profileJson);
      Profile profile = Profile.fromJson(map);
      print(profile.toString());
      activeSession.setProfile(profile);
    });
  }

  void register(Profile profile) async
  {
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/";
    http.post(remoteUrl, body: profile.toString());
  }

  Future<AuthCredentials> login(AuthCredentials credentials) async
  {
    //String remoteUrl = "http://10.0.2.2:8080/registration/login/";
    String remoteUrl = "http://localhost:8080/registration/login/";
    var response = await http.post(remoteUrl, body: credentials.toString());
    String responseJson = response.body;
    print(responseJson);
    AuthCredentials authResponse = AuthCredentials.fromJson(jsonDecode(responseJson));
    return authResponse;
  }
}