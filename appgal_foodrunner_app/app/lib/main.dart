// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
import 'package:flutter/material.dart';

import './src/context/activeSession.dart';
import './src/ui/appGalFoodRunnerRegistration.dart';

void main() 
{
  print("I_LOVE_YOU_DHONI_FAMILY_YOU_ARE_ALWAYS_MY_CAPTAIN_BLESS_YOU_AND_YOUR_SWEET_WIFE_AND_DAUGHTER_ARE_ALWAYS_HAPPY_HAPPY");
  ActiveSession session = ActiveSession.getInstance();
  session.activate();
  ActiveSession.getInstance();

  runApp(MyApp());
}
