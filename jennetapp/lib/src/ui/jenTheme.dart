import 'dart:ui';

import 'package:flutter/cupertino.dart';
import 'package:stylist/stylist.dart';



class JenTheme
{
  StyleData cardStyle = StyleData({
    "card-color": Color(0xFF129892),
    "card-border-radius": BorderRadius.circular(16.0),
    "card-border-color": "app-primary-color",
  });

  @override
  Widget build(BuildContext context) {
    return StaticStyle();
  }
}