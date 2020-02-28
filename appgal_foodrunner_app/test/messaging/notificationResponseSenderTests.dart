import 'package:test/test.dart';

import "../../messaging/notificationResponseSender.dart";

void main(List<String> args) {
  test("send_source_notification_response", () {
    NotificationResponseSender notificationResponseSender = new NotificationResponseSender();
    notificationResponseSender.sendSourceNotificationResponse("blah");
  });
}