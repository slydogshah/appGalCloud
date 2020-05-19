import 'dart:convert';

void main(List<String> args)
{
  String jsonString = "{\"name\":\"blah\",\"email\":\"blah@blah.com\"}";

  Map<String, dynamic> user = jsonDecode(jsonString);

  print('Howdy, ${user['name']}!');
  print('We sent the verification link to ${user['email']}.');

  User model = new User("model", "model@model.com");
  Map<String, dynamic> modelJson = model.toJson();

  print('Howdy, ${modelJson['name']}!');
  print('We sent the verification link to ${modelJson['email']}.');

  App app = new App();
  app.name = "blahblah";
  print(app.getMap());
}

class User {
  final String name;
  final String email;

  User(this.name, this.email);

  User.fromJson(Map<String, dynamic> json)
      : name = json['name'],
        email = json['email'];

  Map<String, dynamic> toJson() =>
    {
      'name': name,
      'email': email,
    };
}

class App
{
  String name;

  Map<String,String> getMap()
  {
    Map<String,String> map = new Map();
    map['name'] = this.name;
    return map;
  }
}