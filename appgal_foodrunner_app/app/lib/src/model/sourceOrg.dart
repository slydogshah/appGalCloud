import 'dart:convert';

import 'location.dart';

class SourceOrg
{
    String orgId;
    String orgName;
    String orgContactEmail;

    Location location = new Location(0.0, 0.0);

    //private DeliveryPreference deliveryPreference;
    SourceOrg(String orgId, String orgName, String orgContactEmail, Location location)
    {
      this.orgId = orgId;
      this.orgName = orgName;
      this.orgContactEmail = orgContactEmail;
      if(location != null)
      {
        this.location = location;
      }
    }

    SourceOrg.fromJson(Map<String, dynamic> json)
    {
      this.orgId = json['orgId'];
      this.orgName = json['orgName'];
      this.orgContactEmail = json['orgContactEmail'];
      if(json['location'] != null)
      {
        this.location = Location.fromJson(json['location']);
      }
    }

    Map<String, dynamic> toJson()
    {
      Map<String, dynamic> json = new Map();
      if(this.location == null)
      {
        this.location = new Location(0.0, 0.0);
      }
      json['orgId'] = this.orgId;
      json['orgName'] = this.orgName;
      json['orgContactEmail'] = this.orgContactEmail;
      json['location'] = this.location;
      return json;
    }

    String toString()
    {
      String json = jsonEncode(this.toJson());
      return json;
    }
}