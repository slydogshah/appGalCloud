import 'dart:convert';

import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/sourceOrg.dart';

class DropOffNotification
{
  SourceOrg sourceOrg;
  FoodRunner foodRunner;

  DropOffNotification(SourceOrg sourceOrg, FoodRunner foodRunner)
  {
    this.sourceOrg = sourceOrg;
    this.foodRunner = foodRunner;
  }

  DropOffNotification.fromJson(Map<String, dynamic> json)
  { 
    if(json["sourceOrg"] != null)
    {
      this.sourceOrg = SourceOrg.fromJson(json["sourceOrg"]);
    }
    if(json["foodRunner"] != null)
    {
      this.foodRunner = FoodRunner.fromJson(json["foodRunner"]);
    }
  }

  Map<String, dynamic> toJson() 
  {
    Map<String, dynamic> map = new Map();
    if(this.sourceOrg != null)
    {
      map["sourceOrg"] = this.sourceOrg;
    }
    if(this.sourceOrg != null)
    {
      map["foodRunner"] = this.foodRunner;
    }
    return map;
  }

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}