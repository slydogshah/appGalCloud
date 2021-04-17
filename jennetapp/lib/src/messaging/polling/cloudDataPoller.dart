import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

import 'package:background_fetch/background_fetch.dart';

import 'dart:typed_data';
import 'dart:ui';

import 'package:device_info/device_info.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:http/http.dart' as http;
import 'package:path_provider/path_provider.dart';
import 'package:rxdart/subjects.dart';
import 'package:timezone/data/latest.dart' as tz;
import 'package:timezone/timezone.dart' as tz;

class CloudDataPoller
{
  static String scheduledTaskId = "com.transistersoft.io.appgallabs.jen.network";
  static String standardTaskId = "com.transistorsoft.fetch";

  static NotificationProcessor notificationProcessor = new NotificationProcessor();

  static void startPolling(Profile profile) async
  {
      notificationProcessor.configureProcessor();

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
    notificationProcessor.showNotification();
  }
}



class ReceivedNotification {
  final int id;
  final String title;
  final String body;
  final String payload;

  ReceivedNotification({
    this.id,
    this.title,
    this.body,
    this.payload,
  });
}

class NotificationProcessor
{
  String selectedNotificationPayload;
  final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
  FlutterLocalNotificationsPlugin();

  /// Streams are created so that app can respond to notification-related events
  /// since the plugin is initialised in the `main` function
  final BehaviorSubject<ReceivedNotification> didReceiveLocalNotificationSubject =
  BehaviorSubject<ReceivedNotification>();

  final BehaviorSubject<String> selectNotificationSubject =
  BehaviorSubject<String>();

  static const MethodChannel platform =
  MethodChannel('dexterx.dev/flutter_local_notifications_example');

  Future<void> configureProcessor() async {
    await this.configureLocalTimeZone();

    final NotificationAppLaunchDetails notificationAppLaunchDetails =
    await flutterLocalNotificationsPlugin.getNotificationAppLaunchDetails();

    const AndroidInitializationSettings initializationSettingsAndroid =
    AndroidInitializationSettings("secondary_icon");

    /// Note: permissions aren't requested here just to demonstrate that can be
    /// done later
    final IOSInitializationSettings initializationSettingsIOS =
    IOSInitializationSettings(
        requestAlertPermission: false,
        requestBadgePermission: false,
        requestSoundPermission: false,
        onDidReceiveLocalNotification:
            (int id, String title, String body, String payload) async {
          didReceiveLocalNotificationSubject.add(ReceivedNotification(
              id: id, title: title, body: body, payload: payload));
        });
    const MacOSInitializationSettings initializationSettingsMacOS =
    MacOSInitializationSettings(
        requestAlertPermission: false,
        requestBadgePermission: false,
        requestSoundPermission: false);
    final InitializationSettings initializationSettings = InitializationSettings(
        android: initializationSettingsAndroid,
        iOS: initializationSettingsIOS,
        macOS: initializationSettingsMacOS);

    await flutterLocalNotificationsPlugin.initialize(initializationSettings,
        onSelectNotification: (String payload) async {
          if (payload != null) {
            debugPrint('notification payload: $payload');
          }
          selectedNotificationPayload = payload;
          selectNotificationSubject.add(payload);
        });
  }

  Future<void> configureLocalTimeZone() async {
    tz.initializeTimeZones();
    final String timeZoneName =
    await platform.invokeMethod<String>('getTimeZoneName');
    tz.setLocalLocation(tz.getLocation(timeZoneName));
  }

  Future<void> showNotification() async {
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
    AndroidNotificationDetails(
        'your channel id', 'your channel name', 'your channel description',
        importance: Importance.max,
        priority: Priority.high,
        ticker: 'ticker');
    const NotificationDetails platformChannelSpecifics =
    NotificationDetails(android: androidPlatformChannelSpecifics);
    await flutterLocalNotificationsPlugin.show(
        0, 'plain title', 'plain body', platformChannelSpecifics,
        payload: 'item x');
  }
}