import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/schedulePickupNotification.dart';

class FoodRecoveryTransaction
{
  SchedulePickupNotification schedulePickupNotification;
  DropOffNotification dropOffNotification;

  FoodRecoveryTransaction(SchedulePickupNotification schedulePickupNotification, DropOffNotification dropOffNotification)
  {
    this.schedulePickupNotification = schedulePickupNotification;
    this.dropOffNotification = dropOffNotification;
  }

  SchedulePickupNotification getPickupNotification()
  {
    return this.schedulePickupNotification;
  }

  setPickupNotification(SchedulePickupNotification schedulePickupNotification)
  {
    this.schedulePickupNotification = schedulePickupNotification;
  }

  DropOffNotification getDropOffNotification()
  {
    return this.dropOffNotification;
  }

  setDropOffNotification(DropOffNotification dropOffNotification)
  {
    this.dropOffNotification = dropOffNotification;
  }

  FoodRecoveryTransaction.fromJson(Map<String, dynamic> json)
  {
    if(json["pickupNotification"] != null)
    {
      this.schedulePickupNotification = SchedulePickupNotification.fromJson(json["pickupNotification"]);
    }
    if(json["dropOffNotification"] != null)
    {
      this.dropOffNotification = DropOffNotification.fromJson(json["dropOffNotification"]);
    }
  }
}