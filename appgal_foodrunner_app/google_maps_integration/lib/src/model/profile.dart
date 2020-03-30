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
    'id': id,
    'email': email,
    'mobile': mobile,
    'photo': photo
  };

  String toString()
  {
    Map<String, dynamic> json = this.toJson();
    StringBuffer buffer = new StringBuffer();
    buffer.write("ID: "+json['id']+"\n");
    buffer.write("EMAIL: "+json['email']+"\n");
    buffer.write("MOBILE: "+json['mobile']+"\n");
    buffer.write("PHOTO: "+json['photo']+"\n");
    return buffer.toString();
  }
}