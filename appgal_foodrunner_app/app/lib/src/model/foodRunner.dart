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
    "profile": "profile",
    "Location": this.location.toJson()
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}