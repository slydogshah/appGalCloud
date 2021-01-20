import 'dart:convert';

import 'package:app/src/model/sourceOrg.dart';

class FoodRequest
{
  String id;
  String foodType;
  SourceOrg sourceOrg;

  FoodRequest(String id, String foodType, SourceOrg sourceOrg)
  {
    this.id = id;
    this.foodType = foodType;
    this.sourceOrg = sourceOrg;
  }

  String getId()
  {
    return this.id;
  }

  void setId(String id)
  {
    this.id = id;
  }

  String getFoodType()
  {
    return this.foodType;
  }

  void setFoodType(String foodType)
  {
    this.foodType = foodType;
  }

  SourceOrg getSourceOrg()
  {
    return this.sourceOrg;
  }

  void setSourceOrg(SourceOrg sourceOrg)
  {
    this.sourceOrg = sourceOrg;
  }

  FoodRequest.fromJson(Map<String, dynamic> json) :
    this.id = json['id'],
    this.foodType = json['foodType'],
    this.sourceOrg = SourceOrg.fromJson(json['sourceOrg']);

  Map<String, dynamic> toJson() =>
  {
    "id": this.id,
    "foodType": this.foodType,
    "sourceOrg": this.sourceOrg
  };

    String toString()
    {
      String json = jsonEncode(this.toJson());
      return json;
    }
}