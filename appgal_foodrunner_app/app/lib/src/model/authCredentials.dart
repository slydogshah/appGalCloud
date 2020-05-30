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
    email = json['email'];
    password = json['password'];
    statusCode = json['statusCode'];
    this.profile = Profile.fromJson(json['profile']);
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
    print("BANDCHOD:[before]"+this.profile.toString());
    return this.profile;
  }
}