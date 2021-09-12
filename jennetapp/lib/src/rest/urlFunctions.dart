import 'dart:convert';
import 'dart:io' show Platform;
import 'package:flutter/services.dart';

class UrlFunctions
{
  static final UrlFunctions singleton = new UrlFunctions();

  String androidApiUrl;
  String iosApiUrl;

  static UrlFunctions getInstance()
  {
    return UrlFunctions.singleton;
  }


  String resolveHost()
  {
    if (Platform.isAndroid) {
      return this.androidApiUrl;
    }
    return this.iosApiUrl;
  }

  static Future<Map<String,dynamic>> getConfig(String env) async
  {
    final contents = await rootBundle.loadString(
      'assets/config/$env.json',
    );
    final Map<String,dynamic> json = jsonDecode(contents);
    return json;
  }

  static Map<String,dynamic> handleError(Exception exception, var response)
  {
    if(exception != null) {
      Map<String,dynamic> json = new Map();
      json["exception"] = "NETWORK_ERROR";
      json["statusCode"] = 500;
      return json;
    }

    if(response.statusCode == 401 || response.statusCode == 403)
    {
      Map<String,dynamic> json = new Map();
      json["exception"] = "AUTH_FAILURE";
      json["statusCode"] = response.statusCode;
      return json;
    }
    else if(response.statusCode != 200)
    {
      Map<String,dynamic> json = new Map();
      json["exception"] = "UNKNOWN_ERROR";
      json["statusCode"] = response.statusCode;
      return json;
    }

    return null;
  }
}