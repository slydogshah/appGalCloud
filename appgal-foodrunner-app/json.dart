import 'dart:convert';

void main(List<String> args)
{
  String jsonString = "{\"name\":\"blah\",\"email\":\"blah@blah.com\"}";

  Map<String, dynamic> user = jsonDecode(jsonString);

  print('Howdy, ${user['name']}!');
  print('We sent the verification link to ${user['email']}.');
}