import '../model/profile.dart';

import 'package:app/src/model/foodRunnerLoginData.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/cloudBusinessException.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:http/http.dart' as http;
import 'package:app/src/model/authCredentials.dart';

import 'dart:convert';

class ProfileRestClient
{
  Future<Profile> getProfile(String email) async
  {
    String remoteUrl = Uri.encodeFull('http://'+UrlFunctions.resolveHost()+':8080/registration/profile/?email='+email);
    var response = await http.get(Uri.parse(remoteUrl));
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
    var response = await http.post(Uri.parse(remoteUrl), body: profile.toString());

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

  Future<Map<String,dynamic>> login(AuthCredentials credentials) async
  {
    var response;
    Map<String, dynamic> json;

    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/registration/login/';
    try {
       response = await http.post(Uri.parse(remoteUrl), body: credentials.toString());
    }
    catch (e) {
      print(e);
      json = new Map();
      json["exception"] = "NETWORK_ERROR";
      json["statusCode"] = 500;
      return json;
    }

    String responseJson = response.body;

    if(response.statusCode == 401)
    {
      json = new Map();
      json["exception"] = "AUTH_FAILURE";
      json["statusCode"] = 401;
      return json;
    }
    else if(response.statusCode != 200)
    {
      json = new Map();
      json["exception"] = "AUTH_FAILURE";
      json["statusCode"] = response.statusCode;
      return json;
    }

    //TODO: cleanup
    json  = jsonDecode(responseJson);
    var json2 = json['profile'];
    Iterable sourceOrgIterable = json2['sourceOrgs'];
    List<SourceOrg> sourceOrgs = new List();
    for(Map<String, dynamic> sourceOrgJson in sourceOrgIterable)
    {
        SourceOrg sourceOrg = SourceOrg.fromJson(sourceOrgJson);
        sourceOrgs.add(sourceOrg);
    }

    return json;
  }
}



//.timeout(
//Duration(seconds: 1),
//onTimeout: () {
//print("NETWORK_TIMEOUT");
//json = new Map();
//json["exception"] = "NETWORK_TIME_OUT";
//json["statusCode"] = 500;
//},
//);