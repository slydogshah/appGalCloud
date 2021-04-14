import 'dart:io';
import 'package:app/main.dart' as App;
import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/model/foodRunnerLocation.dart';

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


void main()
{
  //Launch the App
  HttpOverrides.global = new MyHttpOverrides();
  App.main("dev");
}
