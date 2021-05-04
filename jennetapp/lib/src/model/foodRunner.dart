import 'dart:convert';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/profile.dart';

class FoodRunner
{
  Profile profile;

  FoodRunner(Profile profile)
  {
    this.profile = profile;
  }

  FoodRunner.fromJson(Map<String, dynamic> json);

  Map<String, dynamic> toJson() =>
  {
    "profile": this.profile.toJson()
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }

  static getActiveFoodRunner()
  {
    Profile profile = ActiveSession.getInstance().getProfile();
    return new FoodRunner(profile);
  }
}