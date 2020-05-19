import 'dart:convert';

class AuthCredentials
{
  String email;
  String password;
  int statusCode;
  double latitude;
  double longitude;

  AuthCredentials();

  AuthCredentials.fromJson(Map<String, dynamic> json)
  : email = json['email'],
    password = json['password'],
    statusCode = json['statusCode'],
    latitude = json['latitude'],
    longitude = json['longitude'];

  Map<String, dynamic> toJson() =>
  {
    "email": this.email,
    "password": this.password,
    "statusCode": this.statusCode
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}