import 'package:test/test.dart';

import '../../messaging/foodRunnerNotificationReceiver.dart';
import '../../messaging/model/outstandingFoodRunnerNotification.dart';

void main(List<String> args) {
  test("getNotification", () {
    FoodRunnerNotificationReceiver foodRunnerNotificationReceiver = new FoodRunnerNotificationReceiver();
    foodRunnerNotificationReceiver.getNotification();
  });
}