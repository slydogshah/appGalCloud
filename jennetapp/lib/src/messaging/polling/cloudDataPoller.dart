import 'dart:async';
import 'dart:io';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/profileFunctions.dart';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:rxdart/subjects.dart';

class CloudDataPoller
{
  static String scheduledTaskId = "com.transistersoft.io.appgallabs.jen.network";
  static String standardTaskId = "com.transistorsoft.fetch";

  static NotificationProcessor notificationProcessor = new NotificationProcessor();

  static BuildContext context;

  static void startPolling(BuildContext buildContext,Profile profile) async
  {
      context = buildContext;
      notificationProcessor.configureProcessor(context,profile);
  }
}
//-------------------------------------

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

  void checkNewPickupRequests()
  {
    /*if(this.context == null){
      return;
    }
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Future<List<FoodRecoveryTransaction>> future = activeNetworkRestClient.getFoodRecoveryPush(ActiveSession.getInstance().getProfile().email);
    future.then((txs) async{
      print(txs);
      if(txs.length > 0) {
        const AndroidNotificationDetails androidPlatformChannelSpecifics =
        AndroidNotificationDetails(
            'your channel id', 'your channel name', 'your channel description',
            importance: Importance.max,
            priority: Priority.high,
            ticker: 'ticker');
        const NotificationDetails platformChannelSpecifics =
        NotificationDetails(android: androidPlatformChannelSpecifics);
        int numberOfRequests = txs.length;
        await flutterLocalNotificationsPlugin.show(
            0, '#Jen Network', "You have ($numberOfRequests) new pickup requests", platformChannelSpecifics,
            payload: 'item x');
      }
    }).catchError((e) {});*/
  }

  Future<void> configureProcessor(BuildContext context,Profile profile) async {
    //print("********CONFIGURE_PROCESSOR********");
    this.context = context;

    this.requestPermissions();

    final NotificationAppLaunchDetails notificationAppLaunchDetails =
    await flutterLocalNotificationsPlugin.getNotificationAppLaunchDetails();

    const AndroidInitializationSettings initializationSettingsAndroid =
    AndroidInitializationSettings('app_icon');

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


          ProfileFunctions.launchAppFromNotification(context);
        },notificationCallback: checkNewPickupRequests);

    this.repeatNotification(profile.email);
  }

  Future<void> repeatNotification(String email) async {
    //print("****************REPEAT********************************");
    //print(email);
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
    AndroidNotificationDetails('repeating channel id',
        'repeating channel name', 'repeating description');
    const NotificationDetails platformChannelSpecifics =
    NotificationDetails(android: androidPlatformChannelSpecifics);
    await flutterLocalNotificationsPlugin.periodicallyShow(0, email,
        email, RepeatInterval.everyMinute, platformChannelSpecifics,
        androidAllowWhileIdle: true);

    //print("********CLOUD_POLLER********");
    //print(email);
  }

  void requestPermissions() {
    if(Platform.isIOS) {
      IOSFlutterLocalNotificationsPlugin test = flutterLocalNotificationsPlugin
          .resolvePlatformSpecificImplementation<
          IOSFlutterLocalNotificationsPlugin>();
      if (test != null) {
        test.requestPermissions(
          alert: true,
          badge: true,
          sound: true,
        );
      }

      MacOSFlutterLocalNotificationsPlugin test2 = flutterLocalNotificationsPlugin
          .resolvePlatformSpecificImplementation<
          MacOSFlutterLocalNotificationsPlugin>();

      if (test2 != null) {
        test2.requestPermissions(
          alert: true,
          badge: true,
          sound: true,
        );
      }
    }
  }
}