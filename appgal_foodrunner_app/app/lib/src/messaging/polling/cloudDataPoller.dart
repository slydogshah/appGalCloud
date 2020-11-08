import 'dart:async';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/pickupSource.dart';
import 'package:flutter/material.dart';

class CloudDataPoller
{
  static void startPolling(BuildContext context)
  {
    pollData(context);
    const oneSec = const Duration(seconds:30);
    new Timer.periodic(oneSec, (Timer t) {
      print(new DateTime.now().millisecondsSinceEpoch);
      pollData(context);
    } );
  }

  static void pollData(BuildContext context)
  {
    ActiveSession activeSession = ActiveSession.getInstance();
    Profile profile = activeSession.getProfile();
    FoodRunner foodRunner = new FoodRunner(profile);
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Future<List<SourceOrg>> futureP = activeNetworkRestClient.findBestDestination(foodRunner);
    futureP.then((sourceOrgs){
      Navigator.push(context, MaterialPageRoute(builder: (context) => new PickupSource(sourceOrgs)));
    });
  }
}