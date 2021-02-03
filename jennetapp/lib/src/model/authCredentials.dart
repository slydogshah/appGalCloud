import 'dart:convert';

import 'package:app/src/model/profile.dart';

class AuthCredentials
{
  String email;
  String password;
  int statusCode;

  Profile profile;

  AuthCredentials();

  AuthCredentials.fromJson(Map<String, dynamic> json)
  {
    Map<String,dynamic> profileJson = json['profile'];

    this.profile = Profile.fromJson(profileJson['profile']);
    email = this.profile.email;
    password = this.profile.password;
    statusCode = profileJson['statusCode'];
  }

  Map<String, dynamic> toJson() =>
  {
    "email": this.email,
    "password": this.password,
    "statusCode": this.statusCode,
    "profile": this.profile
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }

  Profile getProfile()
  {
    return this.profile;
  }
}