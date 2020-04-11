import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

main(List<String> strings)
{
  
}

class Localization
{
  static MaterialLocalizations of(BuildContext context) 
  {
    return Localizations.of<MaterialLocalizations>(context, MaterialLocalizations);
  }
}