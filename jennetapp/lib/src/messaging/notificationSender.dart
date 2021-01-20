import 'package:http/http.dart' as http;

import 'model/sourceNotification.dart';

class NotificationSender
{
  void sendSourceNotification(SourceNotification sourceNotification)
  {
    String sourceNotificationId = sourceNotification.sourceNotificationId;
    String remoteUrl = "http://localhost:8080/notification/receiveNotificationForPickup/"+sourceNotificationId;
    http.post(remoteUrl).then((response) {
      print(response.body);
    });
  }
}