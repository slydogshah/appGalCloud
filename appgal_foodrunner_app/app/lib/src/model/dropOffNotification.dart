import 'dart:convert';

import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/sourceOrg.dart';

class DropOffNotification
{
  SourceOrg sourceOrg;
  Location location;
  FoodRunner foodRunner;

  DropOffNotification(SourceOrg sourceOrg,Location location, FoodRunner foodRunner)
  {
    this.sourceOrg = sourceOrg;
    this.location = location;
    this.foodRunner = foodRunner;
  }

  DropOffNotification.fromJson(Map<String, dynamic> json);

  Map<String, dynamic> toJson() =>
  {
    "sourceOrg": this.sourceOrg.toJson(),
    "location": this.location.toJson(),
    "foodRunner": this.foodRunner.toJson()
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}