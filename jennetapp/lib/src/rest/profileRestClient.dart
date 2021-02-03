import 'package:app/src/model/foodRunnerLoginData.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/cloudBusinessException.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:app/src/model/authCredentials.dart';
import '../model/profile.dart';

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
    Profile profile = Profile.fromJson(jsonDecode(profileJson));
    return profile;
  }

  Future<Profile> register(Profile profile) async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/registration/profile/';
    var response = await http.post(remoteUrl, body: profile.toString());

    //print(response.body);
    //print(response.statusCode);
    //print(response.headers);
    if(response.statusCode == 400)
    {
      Map<String,dynamic> validationError = jsonDecode(response.body);
      profile.setValidationError(validationError);
      return profile;
    }
    else if(response.statusCode != 200)
    {
      throw new CloudBusinessException(response.statusCode, response.body);
    }

    Profile result = Profile.fromJson(jsonDecode(response.body));
    return result;
  }

  Future<FoodRunnerLoginData> login(AuthCredentials credentials) async
  {
    FoodRunnerLoginData foodRunnerLoginData = new FoodRunnerLoginData();


    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/registration/login/';
    var response = await http.post(remoteUrl, body: credentials.toString());
    String responseJson = response.body;

    if(response.statusCode == 401)
    {
        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.statusCode = 401;
        foodRunnerLoginData.setAuthCredentials(authCredentials);
        foodRunnerLoginData.authFailure = jsonDecode(responseJson);
        return foodRunnerLoginData;
    }
    else if(response.statusCode != 200)
    {
      throw new CloudBusinessException(response.statusCode, response.body);
    }

    Map<String, dynamic> json  = jsonDecode(responseJson);
    var json2 = json['profile'];
    Iterable sourceOrgIterable = json2['sourceOrgs'];
    List<SourceOrg> sourceOrgs = new List();
    for(Map<String, dynamic> sourceOrgJson in sourceOrgIterable)
    {
        SourceOrg sourceOrg = SourceOrg.fromJson(sourceOrgJson);
        sourceOrgs.add(sourceOrg);
    }
    foodRunnerLoginData.setSourceOrgs(sourceOrgs);

    AuthCredentials authResponse = AuthCredentials.fromJson(json);
    foodRunnerLoginData.setAuthCredentials(authResponse);

    return foodRunnerLoginData;
  }
}