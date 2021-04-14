import 'dart:convert';

class FoodRunnerLocation
{
  double latitude;
  double longitude;

  FoodRunnerLocation(double latitude, double longitude)
  {
      this.latitude = latitude;
      this.longitude = longitude;
  }

  double getLatitude()
  {
    return this.latitude;
  }

  double getLongitude()
  {
    return this.longitude;
  }

  setLatitude(double latitude)
  {
    this.latitude = latitude;
  }

  setLongitude(double longitude)
  {
    this.longitude = longitude;
  }

  FoodRunnerLocation.fromJson(Map<String, dynamic> json)
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