import 'dart:io';
import 'dart:convert';
import 'package:app/src/context/activeSession.dart';
import 'package:flutter/services.dart';

class UrlFunctions
{
  static String resolveHost()
  {
    /*if(Platform.isAndroid)
    {
        //return "10.0.2.2";
        return 'https://10.0.2.2/';
    }
    else if(Platform.isIOS)
    {
        return 'https://localhost/';
    }
    return "https://localhost/";*/
    String apiUrl = ActiveSession.getInstance().getApiUrl();
    return apiUrl;
  }

  static Future<String> getConfig() async
  {
    String env = ActiveSession.getInstance().getEnvironment();
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