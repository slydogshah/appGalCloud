import 'dart:io';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:app/src/ui/app.dart';
import 'package:app/src/ui/foodRunner.dart';
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

  Future<Map<String,dynamic>> config = UrlFunctions.getConfig(env);
  config.then((jsonMap) {
    UrlFunctions.getInstance().androidApiUrl = jsonMap['androidApiUrl'];
    UrlFunctions.getInstance().iosApiUrl = jsonMap['iosApiUrl'];
    launchApp();
  });
}

void launchApp()
{
  Future<Map<String,String>> future = ActiveSession.getInstance().readCredentials();
  future.then((credentials){
    String email = credentials['email'];
    String password = credentials['password'];
    print(email);
    print(password);
    if(email == null || password == null) {
      runApp(new JenNetworkApp());
    }
    else
    {
        autoLogin(email, password);
    }
  });
}

void autoLogin(String email,String password) {
  AuthCredentials credentials = new AuthCredentials();
  credentials.email = email;
  credentials.password = password;
  ProfileRestClient profileRestClient = new ProfileRestClient();
  Future<Map<String, dynamic>> future = profileRestClient.login(credentials);
  future.then((json) {
    //TODO check for error

    Profile foodRunner = Profile.fromJson(json);
    ActiveSession activeSession = ActiveSession.getInstance();
    activeSession.setProfile(foodRunner);

    ActiveNetworkRestClient client = new ActiveNetworkRestClient();
    Future<Map<String, List<FoodRecoveryTransaction>>> future = client
        .getFoodRecoveryTransaction(foodRunner.email);
    future.then((txs) {
      runApp(new FoodRunnerApp(txs));
    });
  });
}
