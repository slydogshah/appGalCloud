import 'package:app/src/rest/urlFunctions.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:app/src/model/authCredentials.dart';
import '../model/profile.dart';
import '../context/activeSession.dart';

class ProfileRestClient
{
  Future<Profile> getProfile(String email) async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/registration/profile/';
    var response = await http.get(remoteUrl);
    String profileJson = response.body;
    Profile profile = Profile.fromJson(jsonDecode(profileJson));
    return profile;
  }

  void setProfile(ActiveSession activeSession) async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/registration/profile/';
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
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/registration/profile/';
    http.post(remoteUrl, body: profile.toString());
  }

  Future<AuthCredentials> login(AuthCredentials credentials) async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/registration/login/';
    var response = await http.post(remoteUrl, body: credentials.toString());
    String responseJson = response.body;
    print("BANDCHOD[original]:"+responseJson);
    Map<String, dynamic> json  = jsonDecode(responseJson);
    if(json['statusCode'] == 401)
    {
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.statusCode = 401;
        return authCredentials;
    }

    AuthCredentials authResponse = AuthCredentials.fromJson(json);
    print("BANDCHOD[pre]:"+authResponse.getProfile().toString());
    return authResponse;
  }
}