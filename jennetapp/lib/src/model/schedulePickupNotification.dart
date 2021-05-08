import 'dart:convert';

import 'package:app/src/model/sourceOrg.dart';
import 'foodRunner.dart';

class SchedulePickupNotification
{
  SourceOrg sourceOrg;
  FoodRunner foodRunner;
  int  start;
  SourceOrg dropOffOrg;
  String foodPic;
  
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

  void setDropOffOrg(SourceOrg dropOffOrg)
  {
    this.dropOffOrg = dropOffOrg;
  }

  SourceOrg getDropOffOrg()
  {
    return this.dropOffOrg;
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
    if(json["dropOffOrg"] != null)
    {
      this.dropOffOrg = SourceOrg.fromJson(json["dropOffOrg"]);
    }
    if(json["foodDetails"] != null)
      {
        Map<String, dynamic> details = json["foodDetails"];
        this.foodPic = details["foodPic"];
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
      json['foodRunner'] = this.foodRunner.toJson();
    }
    if(this.start != null)
    {
      json['start'] = this.start;
    }
    if(this.dropOffOrg != null)
    {
      json['dropOffOrg'] = this.dropOffOrg;
    }

    return json;
  }

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}