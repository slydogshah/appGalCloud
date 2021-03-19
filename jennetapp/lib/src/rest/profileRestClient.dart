import '../model/profile.dart';

import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:app/src/model/authCredentials.dart';

import 'dart:convert';
import 'package:http/http.dart' as http;

class ProfileRestClient
{
  Future<Map<String,dynamic>> register(Profile profile) async
  {
    var response;
    Map<String, dynamic> json;

    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'registration/profile/';
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
    return json;
  }

  Future<Map<String,dynamic>> login(AuthCredentials credentials) async
  {
    var response;
    Map<String, dynamic> json;

    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"registration/login/";
    try {
       response = await http.post(Uri.parse(remoteUrl), body: credentials.toString());
    }
    catch (e) {
      print(e);
      json = UrlFunctions.handleError(e, response);
      return json;
    }

    json = UrlFunctions.handleError(null, response);
    if(json != null)
    {
      return json;
    }

    json  = jsonDecode(response.body);
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