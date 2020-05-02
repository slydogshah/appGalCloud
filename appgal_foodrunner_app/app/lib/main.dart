// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
import 'package:app/src/messaging/polling/cloudDataPoller.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/ui/landingScene.dart';
import 'package:app/src/ui/login.dart';
import 'package:flutter/material.dart';

import './src/context/activeSession.dart';

void main() 
{
  //Activate the data source related Components (Loading Data from the Cloud)
  ActiveSession activeSession = ActiveSession.getInstance();
  activeSession.activate();

  print("BLAH_"+activeSession.getProfile().toString());
  //print(activeSession.getProfile().getLatitude());

  //Launch the App
  runApp(new Login());

  //CloudDataPoller cloudDataPoller = new CloudDataPoller();
  //cloudDataPoller.startPolling();
}
