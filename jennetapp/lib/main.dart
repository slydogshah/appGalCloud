import 'dart:io';
import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:app/src/ui/app.dart';
import 'package:app/src/ui/foodRunner.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:timezone/data/latest.dart' as tz;
import 'package:timezone/timezone.dart' as tz;
import 'notifications.dart';

class MyHttpOverrides extends HttpOverrides{
  @override
  HttpClient createHttpClient(SecurityContext context){
    return super.createHttpClient(context)
      ..badCertificateCallback = (X509Certificate cert, String host, int port)=> true;
  }
}

/*void main(){
  //Launch the App
  HttpOverrides.global = new MyHttpOverrides();

  WidgetsFlutterBinding.ensureInitialized();

  String env = "dev";
  Future<Map<String,dynamic>> config = UrlFunctions.getConfig(env);
  config.then((jsonMap) {
    UrlFunctions.getInstance().androidApiUrl = jsonMap['androidApiUrl'];
    UrlFunctions.getInstance().iosApiUrl = jsonMap['iosApiUrl'];
    //print("APP_URL: "+UrlFunctions.getInstance().iosApiUrl);
    launchApp();
  });
}*/


void main(String env)
{
  //Launch the App
  HttpOverrides.global = new MyHttpOverrides();

  WidgetsFlutterBinding.ensureInitialized();

  Future<Map<String,dynamic>> config = UrlFunctions.getConfig(env);
  config.then((jsonMap) {
    UrlFunctions.getInstance().androidApiUrl = jsonMap['androidApiUrl'];
    UrlFunctions.getInstance().iosApiUrl = jsonMap['iosApiUrl'];
    //print("APP_URL: "+UrlFunctions.getInstance().iosApiUrl);
    launchApp();
  });
}


Future<void> _configureLocalTimeZone() async {
  tz.initializeTimeZones();
  final String timeZoneName =
  await platform.invokeMethod<String>('getTimeZoneName');
  tz.setLocalLocation(tz.getLocation(timeZoneName));
}

void launchApp()
{
  Future<Map<String,dynamic>> future = ActiveSession.getInstance().readCredentials();
  future.then((credentials){
    String email = credentials['email'];
    String password = credentials['password'];

    if(email == null || password == null) {
      runApp(new JenNetworkApp());
      return;
    }

    Future<FoodRunnerLocation> locationFuture = LocationUpdater.getLocation();
    locationFuture.then((location){
      double latitude = credentials['latitude'];
      double longitude = credentials['longitude'];
      //print(email);
      //print(password);
      //print(latitude);
      //print(longitude);
      if(email == null || password == null) {
        runApp(new JenNetworkApp());
      }
      else
      {
        autoLogin(email, password,latitude,longitude);
      }
    });
  });
}

//TODO
void autoLogin(String email,String password,double latitude,double longitude) {
  AuthCredentials credentials = new AuthCredentials();
  credentials.email = email;
  credentials.password = password;
  credentials.latitude = latitude;
  credentials.longitude = longitude;
  ProfileRestClient profileRestClient = new ProfileRestClient();
  Future<Map<String, dynamic>> future = profileRestClient.login(credentials);
  future.then((json) {
    if(json['statusCode'] != 200)
    {
      runApp(new JenNetworkApp());
    }
    else {
      Profile foodRunner = Profile.fromJson(json);

      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(foodRunner);
      activeSession.foodRunner.offlineCommunitySupport =
      json["offlineCommunitySupport"];

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<Map<String, List<FoodRecoveryTransaction>>> future = client
          .getFoodRecoveryTransaction(foodRunner.email);
      future.then((txs) {
          runApp(new FoodRunnerApp(txs));
      }).catchError((e) {
        runApp(new JenNetworkApp());
      });
    }
  });
}
