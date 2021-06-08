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

  Map<String, dynamic> toJson()
  {
    Map<String,dynamic> json = new Map();
    if(this.profile != null){
      json["profile"] = this.profile.toJson();
    }
    return json;
  }

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }

  Profile getProfile()
  {
    return this.profile;
  }

  static getActiveFoodRunner()
  {
    Profile profile = ActiveSession.getInstance().getProfile();
    return new FoodRunner(profile);
  }
}