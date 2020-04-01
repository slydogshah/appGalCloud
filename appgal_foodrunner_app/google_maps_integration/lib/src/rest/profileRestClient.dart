import 'package:google_maps_integration/src/context/activeSession.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import '../model/profile.dart';
import '../context/activeSession.dart';

class ProfileRestClient
{
  void setProfile(ActiveSession activeSession)
  {
    /*Profile profile = new Profile("CLOUD_ID","blah@blah.com","8675309","photu");
    activeSession.setProfile(profile);
    print("****HERE***");
    print(activeSession.getProfile());
    print("****HERE***");*/

    print("START");
    //String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    String remoteUrl = "http://localhost:8080/registration/profile/";
    http.get(remoteUrl).then((response) {
      print("BACK");
      String profileJson = response.body;
      Map<String, dynamic> map = jsonDecode(profileJson);
      Profile profile = Profile.fromJson(map);
      print(profile);
      activeSession.setProfile(profile);
    });
    print("LEAVING");
  }
}