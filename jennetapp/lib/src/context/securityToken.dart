import 'dart:convert';

import 'package:app/src/model/profile.dart';

class SecurityToken
{
  String email;
  String bearerToken;

  SecurityToken(String email,String bearerToken){
    this.email = email;
    this.bearerToken = bearerToken;
  }
}