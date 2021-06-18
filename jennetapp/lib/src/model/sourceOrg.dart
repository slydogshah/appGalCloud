import 'dart:convert';

import 'foodRunnerLocation.dart';

class SourceOrg
{
    String orgId;
    String orgName;
    String orgContactEmail;
    bool isProducer=false;
    String street;
    String zip;

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
      FoodRunnerLocation foodRunnerLocation = new FoodRunnerLocation(0.0, 0.0);
      if(json['latitude'] != null)
      {
        foodRunnerLocation.latitude = json['latitude'];
      }
      if(json['longitude'] != null)
      {
        foodRunnerLocation.longitude = json['longitude'];
      }
      this.location = foodRunnerLocation;
      if(json['street'] != null){
        this.street = json['street'];
      }
      if(json['zip'] != null){
        this.zip = json['zip'];
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
      json['latitude'] = this.location.latitude;
      json['longitude'] = this.location.longitude;
      json['street'] = this.street;
      json['zip'] = this.zip;
      return json;
    }

    String toString()
    {
      String json = jsonEncode(this.toJson());
      return json;
    }
}