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
  //Launch the App
  runApp(new Login());

  //CloudDataPoller cloudDataPoller = new CloudDataPoller();
  //cloudDataPoller.startPolling();
}
