import 'dart:async';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/pickupSource.dart';
import 'package:app/src/model/location.dart';
import 'package:flutter/material.dart';

class CloudDataPoller
{
  static void startPolling(BuildContext context)
  {
    const oneSec = const Duration(seconds:30);
    new Timer.periodic(oneSec, (Timer t) {
      print(new DateTime.now().millisecondsSinceEpoch);
      ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
      //TODO:REMOVE_MOCK_DATA
      FoodRunner foodRunner = new FoodRunner(new Profile("0", "ms.dhoni@gmail.com", "8675309", "", ""), new Location(0.0, 0.0));
      Future<List<SourceOrg>> futureP = activeNetworkRestClient.findBestDestination(foodRunner);
      futureP.then((sourceOrgs){
        Navigator.push(context, MaterialPageRoute(builder: (context) => new PickupSource(sourceOrgs)));
      });
    } );
  }
}