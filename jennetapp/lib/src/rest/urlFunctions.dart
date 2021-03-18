import 'dart:io';
import 'dart:convert';
import 'package:flutter/services.dart';

class UrlFunctions
{
  static String resolveHost()
  {
    if(Platform.isAndroid)
    {
        //return "10.0.2.2";
        return 'https://10.0.2.2/';
    }
    else if(Platform.isIOS)
    {
        return 'https://localhost/';
    }
    return "https://localhost/";
  }

  static Future<String> getConfig() async
  {
    final contents = await rootBundle.loadString(
      'assets/config/dev.json',
    );
    final Map<String,dynamic> json = jsonDecode(contents);
    print("******CONFIG***********");
    print(contents);
    return jsonEncode(json);
  }
}