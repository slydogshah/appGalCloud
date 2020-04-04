// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:flutter/material.dart';

class CommonThings {
  static Size size;
}

class RegistrationScene extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    Text text = new Text("Hello World", textDirection: TextDirection.ltr,);
    Center center = new Center(child: text,);
    return center;
  }
}