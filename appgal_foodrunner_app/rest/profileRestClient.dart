import 'package:http/http.dart' as http;

class ProfileRestClient
{
  void getNotification()
  {
    String remoteUrl = "http://localhost:8080/notification/getOutstandingFoodRunnerNotification/";
    http.post(remoteUrl).then((response) {
      print(response.body);
    });
  }
}