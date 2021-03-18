import 'dart:convert';
import 'package:app/src/context/activeSession.dart';
import 'package:flutter/services.dart';

class UrlFunctions
{
  static final UrlFunctions singleton = new UrlFunctions();

  String apiUrl;

  static UrlFunctions getInstance()
  {
    return UrlFunctions.singleton;
  }

  void setApiUrl(String apiUrl)
  {
    this.apiUrl = apiUrl;
  }


  String resolveHost()
  {
    return this.apiUrl;
  }

  static Future<String> getConfig(String env) async
  {
    final contents = await rootBundle.loadString(
      'assets/config/$env.json',
    );
    final Map<String,dynamic> json = jsonDecode(contents);
    return json['apiUrl'];
  }

  static Map<String,dynamic> handleError(Exception exception, var response)
  {
    Map<String,dynamic> json = new Map();

    if(exception != null) {
      json["exception"] = "NETWORK_ERROR";
      json["statusCode"] = 500;
    }

    if(response.statusCode == 401)
    {
      json = new Map();
      json["exception"] = "AUTH_FAILURE";
      json["statusCode"] = 401;
    }
    else if(response.statusCode != 200)
    {
      json = new Map();
      json["exception"] = "AUTH_FAILURE";
      json["statusCode"] = response.statusCode;
    }

    return json;
  }
}