import 'dart:convert';

import 'package:app/src/model/profile.dart';

import 'location.dart';

class FoodRunner
{
  Profile profile;
  Location location;

  FoodRunner(Profile profile, Location location)
  {
    this.profile = profile;
    this.location = location;
  }

  FoodRunner.fromJson(Map<String, dynamic> json);

  Map<String, dynamic> toJson() =>
  {
    'id': "id",
    'email': "email",
    'mobile': "mobile",
    'photo': "photo"
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}