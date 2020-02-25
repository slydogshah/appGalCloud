import 'dart:collection';
import 'dart:convert';

class NotificationReceiver
{
  Map<String, String> getSourceNotifications()
  {
      Map<String, String> sourceNotifications = new HashMap<String, String>(); 

      sourceNotifications.putIfAbsent("sourceNotificationId", () => "blah");

      return sourceNotifications;
  }
}