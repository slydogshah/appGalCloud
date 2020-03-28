import 'package:http/http.dart' as http;

import '../model/profile.dart';

class ProfileRestClient
{
  Profile getProfile()
  {
    /*String remoteUrl = "http://localhost:8080/notification/getOutstandingFoodRunnerNotification/";
    http.post(remoteUrl).then((response) {
      print(response.body);
    });*/

    Profile profile = new Profile("BLAHBLAHBLAH","email","mobile","photo");
    return profile;
  }
}