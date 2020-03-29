import 'package:http/http.dart' as http;
import 'dart:convert';

import '../model/profile.dart';

class ProfileRestClient
{
  Future<Profile> getProfile() async
  {
    String remoteUrl = "http://localhost:8080/registration/profile/";
    String profileJson = await http.read(remoteUrl);
    Map<String, dynamic> map = jsonDecode(profileJson);
    Profile profile = Profile.fromJson(map);
    return profile;
  }
}