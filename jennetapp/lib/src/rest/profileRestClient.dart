import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/rest/cloudBusinessException.dart';

import '../model/profile.dart';

import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:app/src/model/authCredentials.dart';

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'dart:io' show Platform;

class ProfileRestClient
{
  Future<Map<String,dynamic>> register(Profile profile) async
  {
    var response;
    Map<String, dynamic> json;

    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"registration/profile/";
    try {
      response = await http.post(Uri.parse(remoteUrl), body: profile.toString());
    }
    catch (e) {
      print(e);
      json = UrlFunctions.handleError(e, response);
      return json;
    }

    if(response.statusCode == 400)
    {
      //validation error
      json  = jsonDecode(response.body);
      return json;
    }

    json = UrlFunctions.handleError(null, response);
    if(json != null)
    {
      return json;
    }

    json  = jsonDecode(response.body);
    json['statusCode'] = 200;
    return json;
  }

  Future<Map<String,dynamic>> login(AuthCredentials credentials) async
  {
    var response;
    Map<String, dynamic> json;

    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"registration/login/";
    try {
      //TODO: IOS location issue. most likely simulator related
      Map<String,dynamic> payload = credentials.toJson();
      if (Platform.isIOS) {
        payload["latitude"] = 30.2698104;
        payload["longitude"] = -97.75115579999999;
      }
      else
      {
        payload["latitude"] = credentials.latitude;
        payload["longitude"] = credentials.longitude;
        //payload["latitude"] = 30.2698104;
        //payload["longitude"] = -97.75115579999999;
      }

       response = await http.post(Uri.parse(remoteUrl), body: jsonEncode(payload)).
       timeout(Duration(seconds: 30),onTimeout: () {
         print("NETWORK_TIMEOUT");
         //json = new Map();
         //json["exception"] = "NETWORK_TIME_OUT";
         //json["statusCode"] = 500;
         throw new CloudBusinessException(500, "NETWORK_TIME_OUT");
       });
    }
    catch (e) {
      print(e);
      json = UrlFunctions.handleError(e, response);
      return json;
    }

    json  = jsonDecode(response.body);

    json = UrlFunctions.handleError(null, response);
    if(json != null)
    {
      return json;
    }

    json  = jsonDecode(response.body);

    /*Iterable sourceOrgIterable = json['sourceOrgs'];
    List<SourceOrg> sourceOrgs = new List();
    for(Map<String, dynamic> sourceOrgJson in sourceOrgIterable)
    {
        SourceOrg sourceOrg = SourceOrg.fromJson(sourceOrgJson);
        sourceOrgs.add(sourceOrg);
    }*/

    Map<String,dynamic> result = json['profile'];
    result['statusCode'] = 200;
    return result;
  }
}
