import 'package:app/src/model/activeView.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:http/http.dart' as http;
import 'package:http/http.dart';
import 'dart:convert';

import '../model/profile.dart';
import '../context/activeSession.dart';

class ProfileRestClient
{
  Future<Profile> getProfile(String email) async
  {
    String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    //String remoteUrl = "http://localhost:8080/registration/profile/?email="+email;
    var response = await http.get(remoteUrl);
    String profileJson = response.body;
    Profile profile = Profile.fromJson(jsonDecode(profileJson));
    return profile;
  }

  void setProfile(ActiveSession activeSession) async
  {
    String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    //String remoteUrl = "http://localhost:8080/registration/profile/";
    http.get(remoteUrl).then((response) {
      String profileJson = response.body;
      print("BEHNCHOD_"+profileJson);
      Map<String, dynamic> map = jsonDecode(profileJson);
      Profile profile = Profile.fromJson(map);
      print(profile.toString());
      activeSession.setProfile(profile);
    });
  }

  void register(Profile profile) async
  {
    String remoteUrl = "http://10.0.2.2:8080/registration/profile/";
    //String remoteUrl = "http://localhost:8080/registration/profile/";
    http.post(remoteUrl, body: profile.toString());
  }

  Future<ActiveView> getActiveView() async
  {
    String remoteUrl = "http://10.0.2.2:8080/activeNetwork/activeView/";
    //String remoteUrl = "http://localhost:8080/activeNetwork/activeView/";
    var response = await http.get(remoteUrl);
    String activeViewJson = response.body;
    ActiveView activeView = ActiveView.fromJson(jsonDecode(activeViewJson));
    return activeView;
  }

  Future<AuthCredentials> login(AuthCredentials credentials) async
  {
    String remoteUrl = "http://10.0.2.2:8080/registration/login/";
    //String remoteUrl = "http://localhost:8080/registration/login/";
    var response = await http.post(remoteUrl, body: credentials.toString());
    String responseJson = response.body;
    print(responseJson);
    AuthCredentials authResponse = AuthCredentials.fromJson(jsonDecode(responseJson));
    return authResponse;
  }

  Future<Iterable> findBestDestination(FoodRunner foodRunner) async
  {
    String remoteUrl = "http://10.0.2.2:8080/activeNetwork/findBestDestination/";
    //String remoteUrl = "http://localhost:8080/activeNetwork/findBestDestination/";
    var response = await http.post(remoteUrl, body: foodRunner.toString());
    String responseJson = response.body;
  
    Iterable l = json.decode(responseJson);
    
    return l;
  }

  void sendDeliveryNotification(DropOffNotification dropOffNotification) async
  {
    String remoteUrl = "http://10.0.2.2:8080/activeNetwork/findBestDestination/";
    //String remoteUrl = "http://localhost:8080/activeNetwork/sendDeliveryNotification/";
    String jsonBody = dropOffNotification.toString();
    print(jsonBody);
    var response = await http.post(remoteUrl, body: jsonBody);
    String responseJson = response.body;
    print(responseJson);
  }
}