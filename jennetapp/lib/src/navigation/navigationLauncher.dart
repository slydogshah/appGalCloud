import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:flutter/cupertino.dart';
import 'package:maps_launcher/maps_launcher.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:device_apps/device_apps.dart';

class NavigationLauncher{
  static Future<void> launchMaps() async {
    print("******INVOKED_NAVIGATION*******");

    AppInfo.getinstalledAppList();

    const url = 'https://www.google.com/maps/search/?api=1&query=52.32,4.917';
    if (await canLaunch(url)) {
      await launch(url);
    } else {
      throw 'Could not launch $url';
    }
  }

  static void launchNavigation(SourceOrg sourceOrg,FoodRunnerLocation foodRunnerLocation){
    String street = sourceOrg.street;
    String zip = sourceOrg.zip;
    MapsLauncher.launchQuery(
        street+","+zip);
  }
}

class AppInfo {
  String appName, packageName, versionName;

  AppInfo({
    this.appName,
    this.packageName,
    this.versionName,
  });

  static Future<void> getinstalledAppList() async{
    List<Application> apps = await DeviceApps.getInstalledApplications();
    print(apps);
    // Using foreach statement
    apps.forEach((app) {
      print(app.appName);
      // TODO Backend operation
    });
  }
}
