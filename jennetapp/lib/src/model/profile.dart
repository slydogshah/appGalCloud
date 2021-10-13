import 'dart:convert';

import 'package:app/src/model/foodRunnerLocation.dart';

class Profile {
  String id;
  String email;
  String mobile;
  String photo;
  String password;
  String profileType;
  String bearerToken;
  FoodRunnerLocation location;


  Profile(this.id, this.email, this.mobile, this.photo, this.password);

  FoodRunnerLocation getLocation()
  {
    return this.location;
  }

  setLocation(FoodRunnerLocation location)
  {
    this.location = location;
  }


  String getProfileType()
  {
    return this.profileType;
  }

  void setProfileType(String profileType)
  {
    this.profileType = profileType;
  }

  Profile.fromJson(Map<String, dynamic> json)
  {
    if(json['id'] != null)
    {
      id = json['id'];
    }
    email = json['email'];
    bearerToken = json['bearerToken'];
    mobile = json['mobile'].toString();
    photo = json['photo'];
    if(json['location'] != null)
    {
      this.location = FoodRunnerLocation.fromJson(json['location']);
    }
    password = json['password'];
    profileType = json['profileType'];
  }

  Map<String, dynamic> toJson()
  {
    Map<String, dynamic> json = new Map();
    if(this.id != null)
    {
      json['id'] = this.id;
    }
    if(this.email != null) {
      json['email'] = this.email;
    }
    if(this.bearerToken != null) {
      json['bearerToken'] = this.bearerToken;
    }
    if(this.mobile != null) {
      json['mobile'] = this.mobile;
    }
    if(this.photo != null) {
      json['photo'] = this.photo;
    }
    if(this.location != null)
    {
      json['location'] = this.location.toJson();
    }
    if(this.password != null) {
      json['password'] = this.password;
    }
    if(this.profileType != null) {
      json['profileType'] = this.profileType;
    }

    return json;
  }

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}