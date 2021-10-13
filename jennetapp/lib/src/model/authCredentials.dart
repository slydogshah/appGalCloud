import 'dart:convert';

import 'package:app/src/model/profile.dart';

class AuthCredentials
{
  String email;
  String password;
  double latitude;
  double longitude;
  int statusCode;

  Profile profile;

  AuthCredentials();

  double getLatitude()
  {
    return this.latitude;
  }
  void setLatitude(double latitude)
  {
    this.latitude = latitude;
  }
  double getLongitude()
  {
    return this.longitude;
  }
  void setLongitude(double longitude)
  {
    this.longitude = longitude;
  }

  AuthCredentials.fromJson(Map<String, dynamic> json)
  {
    Map<String,dynamic> profileJson = json['profile'];

    this.profile = Profile.fromJson(profileJson['profile']);
    email = this.profile.email;
    password = this.profile.password;
    statusCode = profileJson['statusCode'];
    latitude = profileJson['latitude'];
    longitude = profileJson['longitude'];
  }

  Map<String, dynamic> toJson() =>
  {
    "email": this.email,
    "password": this.password,
    "statusCode": this.statusCode,
    "profile": this.profile,
    "latitude":this.latitude,
    "longitude":this.longitude
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