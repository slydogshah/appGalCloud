import 'dart:convert';

import 'location.dart';

class SourceOrg
{
    String orgId;
    String orgName;
    String orgContactEmail;

    Location location;

    //private DeliveryPreference deliveryPreference;
    SourceOrg(String orgId, String orgName, String orgContactEmail, Location location)
    {
      this.orgId = orgId;
      this.orgName = orgName;
      this.orgContactEmail = orgContactEmail;
      this.location = location;
    }

    SourceOrg.fromJson(Map<String, dynamic> json);

    Map<String, dynamic> toJson() =>
    {
      "orgId": this.orgId,
      "orgName": this.orgName,
      "orgContactEmail": this.orgContactEmail
    };

    String toString()
    {
      String json = jsonEncode(this.toJson());
      return json;
    }
}