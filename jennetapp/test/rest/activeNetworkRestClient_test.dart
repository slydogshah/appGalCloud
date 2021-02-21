import 'dart:convert';
import 'dart:io';

import 'package:app/src/model/activeView.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/pickupRequest.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:test/test.dart';
import 'package:app/src/model/schedulePickupNotification.dart';

void main() {

test('getActiveView', () async {
    ActiveNetworkRestClient activeNetworkClient = new ActiveNetworkRestClient();
    print("REQUEST STARTING....");
    Future<ActiveView> activeViewFuture = activeNetworkClient.getActiveView();
    activeViewFuture.then((activeView){
      print("RESPONSE: "+activeView.toString());
      expect((activeView.activeFoodRunners)!=null, true);
      expect((activeView.activeFoodRunnerQueue)!=null, true);
      expect((activeView.finderResults)!=null, true);
    });
  });

test('sendLocationUpdate', () async {
  ActiveNetworkRestClient activeNetworkClient = new ActiveNetworkRestClient();
  print("REQUEST STARTING....");
  Location location = new Location(0.0, 0.0);
  Future<String> response = activeNetworkClient.sendLocationUpdate(location);
  response.then((activeView){
    print("RESPONSE: "+activeView.toString());
    //expect((activeView.activeFoodRunners)!=null, true);
    //expect((activeView.activeFoodRunnerQueue)!=null, true);
    //expect((activeView.finderResults)!=null, true);
  });
});

/*  test('sendDeliveryNotification', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Future<Profile> profileFuture = profileRestClient.getProfile("m@s.com");
    profileFuture.then((profile){
      FoodRunner foodRunner = new FoodRunner(profile);
      Future<List<SourceOrg>> future = activeNetworkRestClient.getSourceOrgs();
        future.then((sourceOrgs){
          SourceOrg sourceOrg;
          for(SourceOrg cour in sourceOrgs)
          {
            if(cour.orgId == "microsoft")
            {
              sourceOrg = cour;
              break;
            }
          }
          DropOffNotification dropOffNotification = new DropOffNotification(sourceOrg, foodRunner);
          Future<String> status = activeNetworkRestClient.sendDeliveryNotification(dropOffNotification);
          status.then((value) {
            print(value);
            Map<String,dynamic> json = jsonDecode(value);
            expect(200,json['statusCode']);
          });
      }); 
    });
  });

  test('findBestDestination', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    //Future<Iterable> future = activeNetworkRestClient.findBestDestination(new FoodRunner(new Profile("id","email","mobile","phone"), new Location(0.0, 0.0)));
    Future<Profile> profileFuture = profileRestClient.getProfile("m@s.com");
    profileFuture.then((profile){
      FoodRunner foodRunner = new FoodRunner(profile);
      Future<Iterable> future = activeNetworkRestClient.findBestDestination(foodRunner);
      future.then((sourceOrgs){
        expect(true, sourceOrgs.length > 0);
        for(SourceOrg sourceOrg in sourceOrgs)
        {
          print(sourceOrg.toString());
        }
      });
    });
  });*/

  /*test('sendPickupRequest', () {
    ActiveNetworkRestClient activeNetworkRestClient = ActiveNetworkRestClient();
    Future<List<SourceOrg>> future = activeNetworkRestClient.getSourceOrgs();
    future.then((sourceOrgs){
      SourceOrg sourceOrg;
      for(SourceOrg cour in sourceOrgs)
      {
        if(cour.orgId == "microsoft")
        {
          sourceOrg = cour;
          break;
        }
      }
      PickupRequest pickupRequest = new PickupRequest(sourceOrg);
      Future<Iterable> requestResponse = activeNetworkRestClient.sendPickupRequest(pickupRequest);
      requestResponse.then((list) {
        for(Map<String,dynamic> cour in list)
        {
          print(cour.toString());
        }
      });
    });
  });*/

  /*test('sendFoodRequest', () {
    ActiveNetworkRestClient activeNetworkRestClient = ActiveNetworkRestClient();
    Future<List<SourceOrg>> future = activeNetworkRestClient.getSourceOrgs();
    //print(future);
    future.then((sourceOrgs){
      //print("HELLO: "+sourceOrgs.toString());
      SourceOrg sourceOrg = sourceOrgs.first;
      FoodRequest foodRequest = new FoodRequest("id", "VEG", sourceOrg);
      Future<String> future = activeNetworkRestClient.sendFoodRequest(foodRequest);
      future.then((jsonString){
        print(jsonString);
        Map<String,dynamic> json = jsonDecode(jsonString);
        expect(200,json['statusCode']);
        expect(true, json['foodRequestId'] != null);
      });
    });
    //print("HELLO_WORLD");
  });*/

  /*test('getSourceOrgs', () {
    ActiveNetworkRestClient activeNetworkRestClient = ActiveNetworkRestClient();
    Future<List<SourceOrg>> future = activeNetworkRestClient.getSourceOrgs();
    future.then((sourceOrgs){
      print(sourceOrgs.toString());
      expect(true, sourceOrgs.length > 0);
    }); 
  });*/

  /*test('sendSchedulePickupNotification', () {
    ActiveNetworkRestClient activeNetworkRestClient = ActiveNetworkRestClient();
    SchedulePickupNotification notification = new SchedulePickupNotification(null,null,null);
    Future<int> future = activeNetworkRestClient.sendSchedulePickupNotification(notification);
    print("HELLO");
    //print(future);
    future.then((response){
      print("*************************");
      print(response);
      print("*************************");
      expect(true, false);
    });

    sleep(Duration(seconds:10));
  });*/

  /*test('getFoodRecoveryTransaction', () {
    ActiveNetworkRestClient activeNetworkRestClient = ActiveNetworkRestClient();
    Future<FoodRecoveryTransaction> future = activeNetworkRestClient.getFoodRecoveryTransaction();
    print(future);
    future.then((response){
      print("*************************");
      print(response);
      print("*************************");
      expect(true, false);
    });

    sleep(Duration(seconds:10));
  });*/
}