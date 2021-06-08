import 'dart:convert';

import 'package:app/src/model/schedulePickupNotification.dart';

class FoodRecoveryTransaction
{
  String id;
  SchedulePickupNotification schedulePickupNotification;
  String pickupEstimate;
  String dropOffEstimate;
  String transactionState;

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

  String getId()
  {
    return this.id;
  }
  setId(String id)
  {
    this.id = id;
  }

  String getTransactionState()
  {
      return this.transactionState;
  }

  void setTransactionState(String transactionState)
  {
    this.transactionState = transactionState;
  }

  FoodRecoveryTransaction.fromJson(Map<String, dynamic> json)
  {
    if(json["pickupNotification"] != null)
    {
      this.schedulePickupNotification = SchedulePickupNotification.fromJson(json["pickupNotification"]);
    }
    setPickupEstimate(json["estimatedPickupTime"]);
    setDropOffEstimate(json["estimatedDropOffTime"]);
    setId(json["id"]);
    this.setTransactionState(json["transactionState"]);
  }

  Map<String, dynamic> toJson()
  {
    Map<String, dynamic> map = new Map();
    map["id"] = this.id;
    if(this.schedulePickupNotification != null)
    {
      map["pickupNotification"] = this.schedulePickupNotification.toJson();
    }
    if(this.pickupEstimate != null) {
      map["estimatedPickupTime"] = this.getPickupEstimate();
    }
    if(this.dropOffEstimate != null) {
      map["estimatedDropOffTime"] = this.getDropOffEstimate();
    }
    if(this.transactionState != null) {
      map["transactionState"] = this.getTransactionState();
    }

    return map;
  }

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}