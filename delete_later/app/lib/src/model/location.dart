import 'dart:convert';

class Location
{
  double latitude;
  double longitude;

  Location(double latitude, double longitude)
  {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  Location.fromJson(Map<String, dynamic> json)
  {
    if(json['latitude'] != null)
    {
      latitude = json['latitude'];
    }
    if(json['longitude'] != null)
    {
      longitude = json['longitude'];
    }
  }

  Map<String, dynamic> toJson() =>
  {
    "latitude": this.latitude,
    "longitude": this.longitude
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}