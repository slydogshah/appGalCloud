// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
import 'package:app/src/ui/login.dart';
import 'package:flutter/material.dart';

import './src/context/activeSession.dart';

void main() 
{
  //Activate the data source related Components (Loading Data from the Cloud)
  ActiveSession activeSession = ActiveSession.getInstance();
  activeSession.activate();

  //Launch the App
  runApp(new Login());
}
