import 'dart:io';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:app/src/ui/app.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class MyHttpOverrides extends HttpOverrides{
  @override
  HttpClient createHttpClient(SecurityContext context){
    return super.createHttpClient(context)
      ..badCertificateCallback = (X509Certificate cert, String host, int port)=> true;
  }
}


void main(String env)
{
  //Launch the App
  HttpOverrides.global = new MyHttpOverrides();

  WidgetsFlutterBinding.ensureInitialized();

  Future<String> config = UrlFunctions.getConfig(env);
  config.then((url) {
    UrlFunctions.getInstance().setApiUrl(url);
    runApp(new JenNetworkApp());
  });
}
