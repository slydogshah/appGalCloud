// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:flutter/material.dart';

class RegistrationScene extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    Text text = new Text("Hello World", textDirection: TextDirection.ltr,);
    AppBar appBar = new AppBar(automaticallyImplyLeading: false, title: new Text("Register"),);
    Scaffold scaffold = new Scaffold(appBar: appBar, body: text,);
    return scaffold;
  }
}