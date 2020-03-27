class Profile {
  final String id;
  final String email;
  final String mobile;
  final String photo;

  Profile(this.id, this.email, this.mobile, this.photo);

  Profile.fromJson(Map<String, dynamic> json)
  : id = json['id'],
    email = json['email'],
    mobile = json['mobile'],
    photo = json['photo'];

  Map<String, dynamic> toJson() =>
  {
    'id': id,
    'email': email,
    'mobile': mobile,
    'photo': photo
  };
}