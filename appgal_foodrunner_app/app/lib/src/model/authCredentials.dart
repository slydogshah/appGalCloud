import 'dart:convert';

import 'package:app/src/model/profile.dart';

class AuthCredentials
{
  String email;
  String password;
  int statusCode;
  double latitude;
  double longitude;

  Profile profile;

  AuthCredentials();

  AuthCredentials.fromJson(Map<String, dynamic> json)
  : email = json['email'],
    password = json['password'],
    statusCode = json['statusCode'],
    latitude = json['latitude'],
    longitude = json['longitude'],
    profile = Profile.fromJson(json['profile']);

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