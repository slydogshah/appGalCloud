import 'package:app/src/model/sourceOrg.dart';
import 'foodRunner.dart';

class SchedulePickupNotification
{
  SourceOrg sourceOrg;
  FoodRunner foodRunner;
  String  start;
  
  SchedulePickupNotification(SourceOrg sourceOrg, FoodRunner foodRunner, String start)
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

  void setStart(String start)
  {
    this.start = start;
  }

  String getStart()
  {
    return this.start;
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
}