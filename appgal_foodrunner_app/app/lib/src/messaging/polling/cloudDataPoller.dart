import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

import 'package:background_fetch/background_fetch.dart';

import 'package:shared_preferences/shared_preferences.dart';

class CloudDataPoller
{
  static void startPolling(Profile profile) async
  {
    SharedPreferences prefs = await SharedPreferences.getInstance();

    // Read fetch_events from SharedPreferences
    List<String> events = [];
    String json = prefs.getString("jen_network");
    if (json != null) {
      events = jsonDecode(json).cast<String>();

    }

    // Add new event.
    events.insert(0, profile.toString());
    // Persist fetch events in SharedPreferences
    prefs.setString("jen_network", jsonEncode(events));

    // Register to receive BackgroundFetch events after app is terminated.
    // Requires {stopOnTerminate: false, enableHeadless: true}
    BackgroundFetch.registerHeadlessTask(backgroundFetchHeadlessTask);

      // Schedule a "one-shot" custom-task in 10000ms.
    // These are fairly reliable on Android (particularly with forceAlarmManager) but not iOS,
    // where device must be powered (and delay will be throttled by the OS).
    var rng = new Random();
    int id = rng.nextInt(10000); 
    startBackgroundTask("$id");
  }

  static void pollData(Profile profile)
  {
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Future<String> futureP = activeNetworkRestClient.getSchedulePickUpNotification(profile.email);
    futureP.then((response){
      print(response);
    });
  }

  /// This "Headless Task" is run when app is terminated.
  static void backgroundFetchHeadlessTask(String taskId) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();

    // Read fetch_events from SharedPreferences
    List<String> events = [];
    String eventJson = prefs.getString("jen_network");
    if (eventJson != null) {
      events = jsonDecode(eventJson).cast<String>();

    }
    String profileJson = events[0];
    Profile profile = Profile.fromJson(
    json.decode(profileJson));

    pollData(profile);

    BackgroundFetch.finish(taskId);

    var rng = new Random();
    int id = rng.nextInt(10000); 
    startBackgroundTask("$id");
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