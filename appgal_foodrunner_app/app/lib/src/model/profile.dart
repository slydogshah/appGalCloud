import 'dart:convert';

class Profile {
  String id;
  String email;
  String mobile;
  String photo;

  Profile(this.id, this.email, this.mobile, this.photo);

  Profile.fromJson(Map<String, dynamic> json)
  : id = json['id'],
    email = json['email'],
    mobile = json['mobile'],
    photo = json['photo'];

  Map<String, dynamic> toJson() =>
  {
    "id": this.id,
    "email": this.email,
    "mobile": this.mobile,
    "photo": this.photo
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}