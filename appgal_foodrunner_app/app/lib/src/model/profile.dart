import 'dart:convert';

class Profile {
  String id;
  String email;
  String mobile;
  String photo;
  String password;

  double latitude;
  double longitude;

  String profileType;


  Profile(this.id, this.email, this.mobile, this.photo, this.password);

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

  String getProfileType()
  {
    return this.profileType;
  }

  Profile.fromJson(Map<String, dynamic> json)
  : id = json['id'],
    email = json['email'],
    mobile = json['mobile'],
    photo = json['photo'],
    latitude = json['latitude'],
    longitude = json['longitude'],
    password = json['password'],
    profileType = json['profileType'];

  Map<String, dynamic> toJson() =>
  {
    "id": this.id,
    "email": this.email,
    "mobile": this.mobile,
    "photo": this.photo,
    "latitude": this.latitude,
    "longitude": this.longitude,
    "password": this.password,
    "profileType":this.profileType
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}