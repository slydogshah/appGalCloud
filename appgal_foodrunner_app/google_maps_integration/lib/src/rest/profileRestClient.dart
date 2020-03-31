import 'package:http/http.dart' as http;
import 'dart:convert';

import '../model/profile.dart';

class ProfileRestClient
{
  Future<Profile> getProfile() async
  {
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/";
    String profileJson = await http.read(remoteUrl);
    Map<String, dynamic> map = jsonDecode(profileJson);
    Profile profile = Profile.fromJson(map);
    print(profile.toString());
    return profile;

    //Profile profile = new Profile("CLOUD_ID","blah@blah.com","8675309","photu");
    //print("*******");
    //print(profile);
    //print("*******");
    //return profile;
  }

  Profile getSynchronousProfile()
  {
    String remoteUrl = "http://localhost:8080/registration/profile/";
    http.get(remoteUrl).then((response) {
      print(response.body);
    });
    return Profile("","","","");
  }
}