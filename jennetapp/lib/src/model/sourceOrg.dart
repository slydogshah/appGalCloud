import 'dart:convert';

import 'foodRunnerLocation.dart';

class SourceOrg
{
    String orgId;
    String orgName;
    String orgContactEmail;
    bool isProducer=false;

    FoodRunnerLocation location = new FoodRunnerLocation(0.0, 0.0);

    //private DeliveryPreference deliveryPreference;
    SourceOrg(String orgId, String orgName, String orgContactEmail, FoodRunnerLocation location, bool isProducer)
    {
      this.orgId = orgId;
      this.orgName = orgName;
      this.orgContactEmail = orgContactEmail;
      if(location != null)
      {
        this.location = location;
      }
      this.isProducer = isProducer;
    }

    SourceOrg.fromJson(Map<String, dynamic> json)
    {
      this.orgId = json['orgId'];
      this.orgName = json['orgName'];
      this.orgContactEmail = json['orgContactEmail'];
      this.isProducer = json['producer'];
      if(json['location'] != null)
      {
        this.location = FoodRunnerLocation.fromJson(json['location']);
      }
    }

    Map<String, dynamic> toJson()
    {
      Map<String, dynamic> json = new Map();
      if(this.location == null)
      {
        this.location = new FoodRunnerLocation(0.0, 0.0);
      }
      json['orgId'] = this.orgId;
      json['orgName'] = this.orgName;
      json['orgContactEmail'] = this.orgContactEmail;
      json['location'] = this.location;
      json['producer'] = this.isProducer;
      return json;
    }

    String toString()
    {
      String json = jsonEncode(this.toJson());
      return json;
    }
}