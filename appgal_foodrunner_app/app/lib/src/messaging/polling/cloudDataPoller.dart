import 'dart:async';
import 'dart:ui';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:flutter/material.dart';

import 'package:background_fetch/background_fetch.dart';
import 'dart:math';

class CloudDataPoller
{

  static void startPolling(BuildContext context)
  {
    //pollData(context);
    /*const oneSec = const Duration(seconds:3);
    new Timer.periodic(oneSec, (Timer t) {
      print(new DateTime.now().millisecondsSinceEpoch);
      pollData(context);
    } );*/
      // Configure BackgroundFetch.
      /*BackgroundFetch.configure(BackgroundFetchConfig(
          minimumFetchInterval: 1,
          forceAlarmManager: false,
          stopOnTerminate: false,
          startOnBoot: true,
          enableHeadless: true,
          requiresBatteryNotLow: false,
          requiresCharging: false,
          requiresStorageNotLow: false,
          requiresDeviceIdle: false,
          requiredNetworkType: NetworkType.NONE,
      ), _onBackgroundFetch).then((int status) {
        print('[BackgroundFetch] configure success: $status');
      }).catchError((e) {
        print('[BackgroundFetch] configure ERROR: $e');
      });*/

      // Register to receive BackgroundFetch events after app is terminated.
    // Requires {stopOnTerminate: false, enableHeadless: true}
    BackgroundFetch.registerHeadlessTask(backgroundFetchHeadlessTask);

      // Schedule a "one-shot" custom-task in 10000ms.
    // These are fairly reliable on Android (particularly with forceAlarmManager) but not iOS,
    // where device must be powered (and delay will be throttled by the OS).
    var rng = new Random();
    startBackgroundTask("taskId$rng.nextInt(10000)");
  }

  static void pollData(BuildContext context)
  {
    ActiveSession activeSession = ActiveSession.getInstance();
    Profile profile = activeSession.getProfile();
    FoodRunner foodRunner = new FoodRunner(profile);
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Future<String> futureP = activeNetworkRestClient.getSchedulePickUpNotification(profile.email);
    futureP.then((response){
      //Navigator.push(context, MaterialPageRoute(builder: (context) => new PickupSource(sourceOrgs)));
      print(response);
    });
  }

  static void _onBackgroundFetch(String taskId) async {
    print("[BackgroundFetch] Event received: $taskId");

    // IMPORTANT:  You must signal completion of your fetch task or the OS can punish your app
    // for taking too long in the background.
    BackgroundFetch.finish(taskId);
  }

  /// This "Headless Task" is run when app is terminated.
  static void backgroundFetchHeadlessTask(String taskId) async {
    print("[BackgroundFetch] Headless event received: $taskId");
    DateTime timestamp = DateTime.now();

    BackgroundFetch.finish(taskId);

    var rng = new Random();
    startBackgroundTask("taskId$rng.nextInt(10000)");
  }

  static void startBackgroundTask(String taskId)
  {
    BackgroundFetch.scheduleTask(TaskConfig(
          taskId: taskId,
          delay: 1000,
          periodic: true,
          forceAlarmManager: true,
          stopOnTerminate: false,
          enableHeadless: true
    ));
  }
}