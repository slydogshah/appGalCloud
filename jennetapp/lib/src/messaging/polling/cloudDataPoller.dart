import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

import 'package:background_fetch/background_fetch.dart';

class CloudDataPoller
{
  static String scheduledTaskId = "com.transistersoft.io.appgallabs.jen.network";
  static String standardTaskId = "com.transistorsoft.fetch";

  static void startPolling(Profile profile) async
  {
      if(Platform.isIOS)
      {
        startIOSPolling(profile);
      }
      else if(Platform.isAndroid)
      {
        startAndroidPolling(profile);
      }
  }
  //--------ios--------------------------------------------
  static void startIOSPolling(Profile profile) async
  {
    // Configure BackgroundFetch.
    int status = await BackgroundFetch.configure(BackgroundFetchConfig(
      minimumFetchInterval: 15,
      forceAlarmManager: false,
      stopOnTerminate: true,
      startOnBoot: false,
      requiresBatteryNotLow: false,
      requiresCharging: false,
      requiresStorageNotLow: false,
      requiresDeviceIdle: false,
      requiredNetworkType: NetworkType.NONE,
    ), standardTask,fetchTimeout);
    print('[BackgroundFetchIOS] configure success: $status');
  }
  //--------android----------------------------------------
  static void startAndroidPolling(Profile profile) async
  {
    // Configure BackgroundFetch.
    int status = await BackgroundFetch.configure(BackgroundFetchConfig(
      minimumFetchInterval: 15,
      forceAlarmManager: false,
      stopOnTerminate: true,
      startOnBoot: false,
      requiresBatteryNotLow: false,
      requiresCharging: false,
      requiresStorageNotLow: false,
      requiresDeviceIdle: false,
      requiredNetworkType: NetworkType.NONE,
    ), standardTask,fetchTimeout);
    print('[BackgroundFetchAndroid] configure success: $status');
  }

  static void standardTask(String taskId) async
  {
    Profile profile = ActiveSession.getInstance().getProfile();
    pollData(profile);

    BackgroundFetch.finish(taskId);

    BackgroundFetch.scheduleTask(TaskConfig(
        taskId: "flutter_background_fetch",
        delay: 10,
        periodic: false,
        forceAlarmManager: true,
        stopOnTerminate: true,
        enableHeadless: false,
        requiresNetworkConnectivity: true,
        requiresCharging: true
    ));
  }

  static void fetchTimeout(String taskId) {
    print("[BackgroundFetch] TIMEOUT: $taskId");

    Profile profile = ActiveSession.getInstance().getProfile();
    pollData(profile);

    BackgroundFetch.finish(taskId);

    BackgroundFetch.scheduleTask(TaskConfig(
        taskId: "flutter_background_fetch",
        delay: 10,
        periodic: false,
        forceAlarmManager: true,
        stopOnTerminate: true,
        enableHeadless: false,
        requiresNetworkConnectivity: true,
        requiresCharging: true
    ));
  }
  //--------------------------------------------------------
  static void pollData(Profile profile)
  {
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Future<List<FoodRecoveryTransaction>> futureP = activeNetworkRestClient.getFoodRecoveryPush(profile.email);
    futureP.then((txs){
      print("********PUSH**********");
      for(FoodRecoveryTransaction tx in txs) {
        print(jsonEncode(tx.toJson()));
      }
      print("********PUSH**********");
    });
  }
}