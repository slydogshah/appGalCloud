import 'dart:convert';

import 'package:app/src/model/sourceOrg.dart';
import 'foodRunner.dart';

class SchedulePickupNotification
{
  SourceOrg sourceOrg;
  FoodRunner foodRunner;
  int  start;
  
  SchedulePickupNotification(SourceOrg sourceOrg, FoodRunner foodRunner, int start)
  {
    this.sourceOrg = sourceOrg;
    this.foodRunner = foodRunner;
    this.start = start;
  }

  void setSourceOrg(SourceOrg sourceOrg)
  {
    this.sourceOrg = sourceOrg;
  }

  SourceOrg getSourceOrg()
  {
    return this.sourceOrg;
  }

  void setFoodRunner(FoodRunner foodRunner)
  {
    this.foodRunner = foodRunner;
  }

  void setStart(int start)
  {
    this.start = start;
  }

  int getStart()
  {
    return this.start;
  }

  SchedulePickupNotification.fromJson(Map<String, dynamic> json)
  { 
    if(json["sourceOrg"] != null)
    {
      this.sourceOrg = SourceOrg.fromJson(json["sourceOrg"]);
    }
    if(json["foodRunner"] != null)
    {
      this.foodRunner = FoodRunner.fromJson(json["foodRunner"]);
    }
    if(json["start"] != null)
    {
      this.start = json['start'];
    }
  }

  Map<String, dynamic> toJson()
  {
    Map<String, dynamic> json = new Map();
    if(this.sourceOrg != null)
    {
      json['sourceOrg'] = this.sourceOrg;
    }
    if(this.foodRunner != null)
    {
      json['foodRunner'] = this.sourceOrg;
    }
    if(this.start != null)
    {
      json['start'] = this.start;
    }

    return json;
  }

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}