import 'package:test/test.dart';

import '../../messaging/notificationReceiver.dart';

void main(List<String> args) {
  test("receive_source_notifications", () {
      NotificationReceiver notificationReceiver = new NotificationReceiver();
      List<dynamic> sourceNotifications = notificationReceiver.getSourceNotifications();
      expect(sourceNotifications.isEmpty, false);

      Map<String,dynamic> sourceNotification = sourceNotifications.first;
      expect(sourceNotification["sourceNotificationId"], "92ed655a-99a2-438b-8eeb-05d12a2d8a1b");
      expect(sourceNotification["startTimestamp"], 1582660041);
      expect(sourceNotification["endTimestamp"], 1582660641);
      expect(sourceNotification["latitude"], "44.9441");
      expect(sourceNotification["longitude"], "-93.0852");
    });

    test("receivePushSourceNotification", () {
      NotificationReceiver notificationReceiver = new NotificationReceiver();
      String sourcePushNotification = notificationReceiver.receivePushSourceNotification();
      print(sourcePushNotification);
    });
}


/*group('String', () {
    test('.split() splits the string on the delimiter', () {
      var string = 'foo,bar,baz';
      expect(string.split(','), equals(['foo', 'bar', 'baz']));
    });

    test('.trim() removes surrounding whitespace', () {
      var string = '  foo ';
      expect(string.trim(), equals('foo'));
    });
  });

  group('int', () {
    test('.remainder() returns the remainder of division', () {
      expect(11.remainder(3), equals(2));
    });

    test('.toRadixString() returns a hex string', () {
      expect(11.toRadixString(16), equals('b'));
    });
  });*/