import 'dart:convert';

import 'package:app/src/model/schedulePickupNotification.dart';

class FoodRecoveryTransaction
{
  SchedulePickupNotification schedulePickupNotification;
  String pickupEstimate;
  String dropOffEstimate;

  FoodRecoveryTransaction(SchedulePickupNotification schedulePickupNotification)
  {
    this.schedulePickupNotification = schedulePickupNotification;
  }

  SchedulePickupNotification getPickupNotification()
  {
    return this.schedulePickupNotification;
  }

  setPickupNotification(SchedulePickupNotification schedulePickupNotification)
  {
    this.schedulePickupNotification = schedulePickupNotification;
  }

  String getPickupEstimate()
  {
    return this.pickupEstimate;
  }
  setPickupEstimate(String pickupEstimate)
  {
    this.pickupEstimate = pickupEstimate;
  }
  String getDropOffEstimate()
  {
    return this.dropOffEstimate;
  }
  setDropOffEstimate(String dropOffEstimate)
  {
    this.dropOffEstimate = dropOffEstimate;
  }

  FoodRecoveryTransaction.fromJson(Map<String, dynamic> json)
  {
    if(json["pickupNotification"] != null)
    {
      this.schedulePickupNotification = SchedulePickupNotification.fromJson(json["pickupNotification"]);
    }
    setPickupEstimate(json["estimatedPickupTime"]);
    setDropOffEstimate(json["estimatedDropOffTime"]);
  }

  Map<String, dynamic> toJson()
  {
    Map<String, dynamic> map = new Map();
    if(this.schedulePickupNotification != null)
    {
      map["pickupNotification"] = this.schedulePickupNotification;
    }
    map["estimatedPickupTime"] = this.getPickupEstimate();
    map["estimatedDropOffTime"] = this.getDropOffEstimate();
    return map;
  }

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}