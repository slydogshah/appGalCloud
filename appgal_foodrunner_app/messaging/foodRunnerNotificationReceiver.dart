import 'package:http/http.dart' as http;

import 'model/outstandingFoodRunnerNotification.dart';

class FoodRunnerNotificationReceiver
{
  void getNotification()
  {
    String remoteUrl = "http://localhost:8080/notification/getOutstandingFoodRunnerNotification/";
    http.post(remoteUrl).then((response) {
      print(response.body);
    });
  }
}