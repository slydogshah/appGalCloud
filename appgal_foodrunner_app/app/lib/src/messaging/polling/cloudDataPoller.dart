import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

import 'package:background_fetch/background_fetch.dart';

import 'package:shared_preferences/shared_preferences.dart';

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
    SharedPreferences prefs = await SharedPreferences.getInstance();

    // Read fetch_events from SharedPreferences
    List<String> events = [];
    String json = prefs.getString(standardTaskId);
    if (json != null) {
      events = jsonDecode(json).cast<String>();

    }

    // Add new event.
    events.insert(0, profile.toString());
    // Persist fetch events in SharedPreferences
    prefs.setString(standardTaskId, jsonEncode(events));

    // Configure BackgroundFetch.
    BackgroundFetch.configure(BackgroundFetchConfig(
        minimumFetchInterval: 1,
        forceAlarmManager: false,
        stopOnTerminate: false,
        startOnBoot: true,
        enableHeadless: true,
        requiresBatteryNotLow: false,
        requiresCharging: false,
        requiresStorageNotLow: false,
        requiresDeviceIdle: false,
        requiredNetworkType: NetworkType.NONE
    ), standardTask).then((int status) {
      print('[StandardFetch] configure success: $status');
      BackgroundFetch.start().then((int status) {
        print('[StandardFetch] start success: $status');
      }).catchError((e) {
        print('[StandardFetch] start FAILURE: $e');
      });
    }).catchError((e) {
      print('[StandardFetch] configure ERROR: $e');
    });

    BackgroundFetch.finish(standardTaskId);
  }
  //--------android----------------------------------------
  static void startAndroidPolling(Profile profile) async
  {
    SharedPreferences prefs = await SharedPreferences.getInstance();

    // Read fetch_events from SharedPreferences
    List<String> events = [];
    String json = prefs.getString(scheduledTaskId);
    if (json != null) {
      events = jsonDecode(json).cast<String>();

    }

    // Add new event.
    events.insert(0, profile.toString());
    // Persist fetch events in SharedPreferences
    prefs.setString(scheduledTaskId, jsonEncode(events));

    // Register to receive BackgroundFetch events after app is terminated.
    // Requires {stopOnTerminate: false, enableHeadless: true}
    BackgroundFetch.registerHeadlessTask(scheduledTask);

    BackgroundFetch.scheduleTask(TaskConfig(
          taskId: scheduledTaskId,
          delay: 1000,
          periodic: true,
          forceAlarmManager: true,
          stopOnTerminate: false,
          enableHeadless: true
    ));
  }
  //--------------------------------------------------------
  static void pollData(Profile profile)
  {
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Future<String> futureP = activeNetworkRestClient.getSchedulePickUpNotification(profile.email);
    futureP.then((response){
      print("********PUSH**********");
      print(response);
      print("********PUSH**********");
    });
  }

  /// This "Headless Task" is run when app is terminated.
  static void scheduledTask(String taskId) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();

    // Read fetch_events from SharedPreferences
    List<String> events = [];
    String eventJson = prefs.getString(taskId);
    if (eventJson != null) {
      events = jsonDecode(eventJson).cast<String>();

    }
    String profileJson = events[0];
    Profile profile = Profile.fromJson(
    json.decode(profileJson));

    pollData(profile);

    BackgroundFetch.finish(taskId);

    BackgroundFetch.scheduleTask(TaskConfig(
          taskId: taskId,
          delay: 1000,
          periodic: true,
          forceAlarmManager: true,
          stopOnTerminate: false,
          enableHeadless: true
    ));
  }

  static void standardTask(String taskId) async
  {
    SharedPreferences prefs = await SharedPreferences.getInstance();

    // Read fetch_events from SharedPreferences
    List<String> events = [];
    String eventJson = prefs.getString(taskId);
    if (eventJson != null) {
      events = jsonDecode(eventJson).cast<String>();

    }
    String profileJson = events[0];
    Profile profile = Profile.fromJson(
    json.decode(profileJson));

    pollData(profile);

    BackgroundFetch.finish(taskId);
  }
}