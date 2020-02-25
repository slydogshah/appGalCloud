import 'dart:collection';
import 'dart:convert';
import 'package:http/http.dart' as http;

class NotificationResponseSender
{
  void sendSourceNotificationResponse(String sourceNotificationId)
  {
    String remoteUrl = "http://localhost:8080/notification/receiveNotificationForPickup/"+sourceNotificationId;
    http.post(remoteUrl).then((response) {
      print(response.body);
    });
  }
}