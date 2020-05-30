import 'dart:convert';

import 'package:app/src/model/location.dart';

class Profile {
  String id;
  String email;
  String mobile;
  String photo;
  String password;
  String profileType;
  Location location;


  Profile(this.id, this.email, this.mobile, this.photo, this.password);

  Location getLocation()
  {
    return this.location;
  }

  setLocation(Location location)
  {
    this.location = location;
  }


  String getProfileType()
  {
    return this.profileType;
  }

  Profile.fromJson(Map<String, dynamic> json)
  {
    id = json['id'];
    email = json['email'];
    mobile = json['mobile'];
    photo = json['photo'];
    this.location = Location.fromJson(json['location']);
    password = json['password'];
    profileType = json['profileType'];
  }

  Map<String, dynamic> toJson() =>
  {
    "id": this.id,
    "email": this.email,
    "mobile": this.mobile,
    "photo": this.photo,
    "location": this.location.toJson(),
    "password": this.password,
    "profileType":this.profileType
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}