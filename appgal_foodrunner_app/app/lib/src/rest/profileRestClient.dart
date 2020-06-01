import 'package:app/src/rest/cloudBusinessException.dart';
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
    String remoteUrl = Uri.encodeFull('http://'+UrlFunctions.resolveHost()+':8080/registration/profile/?email='+email);
    var response = await http.get(remoteUrl);
    if(response.statusCode != 200)
    {
      throw new CloudBusinessException(response.statusCode, response.body);
    }
    String profileJson = response.body;
    print(profileJson);
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
    Map<String, dynamic> json  = jsonDecode(responseJson);
    if(response.statusCode == 401)
    {
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.statusCode = 401;
        return authCredentials;
    }
    else if(response.statusCode != 200)
    {
      throw new CloudBusinessException(response.statusCode, response.body);
    }


    AuthCredentials authResponse = AuthCredentials.fromJson(json);
    return authResponse;
  }
}