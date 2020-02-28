import 'dart:collection';
import 'dart:convert';

import 'firebase_options.dart';

class NotificationReceiver
{
  String json = "[{\"sourceNotificationId\":\"92ed655a-99a2-438b-8eeb-05d12a2d8a1b\",\"startTimestamp\":1582660041,\"endTimestamp\":1582660641,\"latitude\":\"44.9441\",\"longitude\":\"-93.0852\"}]";


  List<dynamic> getSourceNotifications()
  {
      List<dynamic> sourceNotifications = jsonDecode(this.json); 
      return sourceNotifications;
  }

  String receivePushSourceNotification()
  {
    FirebaseOptions firebaseOptions = new FirebaseOptions(googleAppID: "googleAppId");
    return firebaseOptions.toString();
  }
}