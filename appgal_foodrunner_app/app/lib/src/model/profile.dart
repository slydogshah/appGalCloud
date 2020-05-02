import 'dart:convert';

import 'package:app/src/model/location.dart';

class Profile {
  String id;
  String email;
  String mobile;
  String photo;

  double latitude;
  double longitude;

  Profile(this.id, this.email, this.mobile, this.photo);

  double getLatitude()
  {
    return this.latitude;
  }

  double getLongitude()
  {
    return this.longitude;
  }

  void setLatitude(double latitude)
  {
    this.latitude = latitude;
  }

  void setLongitude(double longitude)
  {
    this.longitude = longitude;
  }

  Profile.fromJson(Map<String, dynamic> json)
  : id = json['id'],
    email = json['email'],
    mobile = json['mobile'],
    photo = json['photo'],
    latitude = json['latitude'],
    longitude = json['longitude'];

  Map<String, dynamic> toJson() =>
  {
    "id": this.id,
    "email": this.email,
    "mobile": this.mobile,
    "photo": this.photo,
    "latitude": this.latitude,
    "longitude": this.longitude
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}