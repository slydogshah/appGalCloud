import 'dart:io';
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


void main(String s)
{
  //Launch the App
  HttpOverrides.global = new MyHttpOverrides();
  runApp(new JenNetworkApp());
}
