import 'dart:convert';

import 'package:app/src/model/schedulePickupNotification.dart';

class FoodRecoveryTransaction
{
  SchedulePickupNotification schedulePickupNotification;

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

  FoodRecoveryTransaction.fromJson(Map<String, dynamic> json)
  {
    if(json["pickupNotification"] != null)
    {
      this.schedulePickupNotification = SchedulePickupNotification.fromJson(json["pickupNotification"]);
    }
  }

  Map<String, dynamic> toJson()
  {
    Map<String, dynamic> map = new Map();
    if(this.schedulePickupNotification != null)
    {
      map["pickupNotification"] = this.schedulePickupNotification;
    }
    return map;
  }

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}