import 'dart:convert';

import 'package:app/src/model/location.dart';

class Profile {
  String id;
  String email;
  int mobile;
  String photo;
  String password;
  String profileType;
  Location location;

  Map<String,dynamic> validationError;


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

  void setProfileType(String profileType)
  {
    this.profileType = profileType;
  }

  Map<String,dynamic> getValidationError()
  {
    return this.validationError;
  }

  setValidationError(Map<String,dynamic> validationError)
  {
    this.validationError = validationError;
  }

  Profile.fromJson(Map<String, dynamic> json)
  {
    if(json['id'] != null)
    {
      id = json['id'];
    }
    email = json['email'];
    mobile = json['mobile'];
    photo = json['photo'];
    if(json['location'] != null)
    {
      this.location = Location.fromJson(json['location']);
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
    json['email'] = this.email;
    json['mobile'] = this.mobile;
    json['photo'] =  this.photo;
    if(this.location != null)
    {
      json['location'] = this.location.toJson();
    }
    json['password'] = this.password;
    json['profileType'] = this.profileType;

    return json;
  }

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}