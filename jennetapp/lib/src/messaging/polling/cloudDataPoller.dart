import 'dart:async';
import 'dart:io';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/foodRunner.dart';

import 'package:background_fetch/background_fetch.dart';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:path_provider/path_provider.dart';
import 'package:rxdart/subjects.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:timezone/data/latest.dart' as tz;
import 'package:timezone/timezone.dart' as tz;
import 'package:workmanager/workmanager.dart' as Workmanager;

class CloudDataPoller
{
  static String scheduledTaskId = "com.transistersoft.io.appgallabs.jen.network";
  static String standardTaskId = "com.transistorsoft.fetch";

  static NotificationProcessor notificationProcessor = new NotificationProcessor();
  static WorkManagerProcessor workManagerProcessor = new WorkManagerProcessor();

  static BuildContext context;

  static void startPolling(BuildContext buildContext,Profile profile) async
  {
      context = buildContext;
      notificationProcessor.configureProcessor(context);
      //workManagerProcessor.configureProcessor();

      if(Platform.isIOS)
      {
        startIOSPolling(profile);
      }
      else if(Platform.isAndroid)
      {
        startAndroidPolling(profile);
      }
  }

  static void showNotification(List<FoodRecoveryTransaction> txs)
  {
    for(FoodRecoveryTransaction tx in txs) {
      notificationProcessor.showNotification(context,tx.getPickupNotification().getSourceOrg().orgName);
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
    activeNetworkRestClient.getFoodRecoveryPush(profile.email);
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
  BuildContext context;

  /// Streams are created so that app can respond to notification-related events
  /// since the plugin is initialised in the `main` function
  final BehaviorSubject<ReceivedNotification> didReceiveLocalNotificationSubject =
  BehaviorSubject<ReceivedNotification>();

  final BehaviorSubject<String> selectNotificationSubject =
  BehaviorSubject<String>();

  static const MethodChannel platform =
  MethodChannel('dexterx.dev/flutter_local_notifications_example');

  Future<void> configureProcessor(BuildContext context) async {
    this.context = context;

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
          /*if (payload != null) {
            debugPrint('notification payload: $payload');
          }
          selectedNotificationPayload = payload;
          selectNotificationSubject.add(payload);*/

          print("*******************NOTIFICATION_SELECTED***********************");
          ActiveSession activeSession = ActiveSession.getInstance();
          Profile foodRunner = activeSession.getProfile();

          ActiveNetworkRestClient client = new ActiveNetworkRestClient();
          Future<List<FoodRecoveryTransaction>> future = client
              .getFoodRecoveryTransaction(foodRunner.email);
          future.then((txs) {
            Navigator.pushReplacement(context, MaterialPageRoute(
                builder: (context) => FoodRunnerMainScene(txs)));
          });
        });
  }

  Future<void> configureLocalTimeZone() async {
    tz.initializeTimeZones();
    final String timeZoneName =
    await platform.invokeMethod<String>('getTimeZoneName');
    tz.setLocalLocation(tz.getLocation(timeZoneName));
  }

  Future<void> showNotification(BuildContext context,String body) async {
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
    AndroidNotificationDetails(
        'your channel id', 'your channel name', 'your channel description',
        importance: Importance.max,
        priority: Priority.high,
        ticker: 'ticker');
    const NotificationDetails platformChannelSpecifics =
    NotificationDetails(android: androidPlatformChannelSpecifics);
    await flutterLocalNotificationsPlugin.show(
        0, "PickUp Request", body, platformChannelSpecifics,
        payload: '');
  }
}

class WorkManagerProcessor
{
  static const simpleTaskKey = "simpleTask";
  static const simpleDelayedTask = "simpleDelayedTask";
  static const simplePeriodicTask = "simplePeriodicTask";
  static const simplePeriodic1HourTask = "simplePeriodic1HourTask";

  void configureProcessor()
  {
    Workmanager.Workmanager().initialize(
      callbackDispatcher,
      isInDebugMode: true,
    );
  }

  void callbackDispatcher() {
    Workmanager.Workmanager().executeTask((task, inputData) async {
      switch (task) {
        case simpleTaskKey:
          print("$simpleTaskKey was executed. inputData = $inputData");
          final prefs = await SharedPreferences.getInstance();
          prefs.setBool("test", true);
          print("Bool from prefs: ${prefs.getBool("test")}");
          break;
        case simpleDelayedTask:
          print("$simpleDelayedTask was executed");
          break;
        case simplePeriodicTask:
          print("$simplePeriodicTask was executed");
          break;
        case simplePeriodic1HourTask:
          print("$simplePeriodic1HourTask was executed");
          break;
        case Workmanager.Workmanager.iOSBackgroundTask:
          print("The iOS background fetch was triggered");
          Directory tempDir = await getTemporaryDirectory();
          String tempPath = tempDir?.path;
          print(
              "You can access other plugins in the background, for example Directory.getTemporaryDirectory(): $tempPath");
          break;
      }

      return Future.value(true);
    });
  }
}