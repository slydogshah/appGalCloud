import 'package:test/test.dart';

import '../../messaging/notificationSender.dart';
import '../../messaging/model/sourceNotification.dart';

void main(List<String> args) {
  test("send_source_notification", () {
    NotificationSender notificationSender = new NotificationSender();
    SourceNotification sourceNotification = new SourceNotification("blahblah","latitude","longitude");
    notificationSender.sendSourceNotification(sourceNotification);
  });
}