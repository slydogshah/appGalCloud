import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

void main(List<String> args)
{
  print("HELLO_WORLD");
}

class Localization
{
  static MaterialLocalizations of(BuildContext context) 
  {
    return Localizations.of<MaterialLocalizations>(context, MaterialLocalizations);
  }
}