import 'dart:convert';

import 'package:app/src/model/activeView.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/pickupRequest.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:test/test.dart';

void main() {

test('getActiveView', () {
    ActiveNetworkRestClient activeNetworkClient = new ActiveNetworkRestClient();
    Future<ActiveView> activeViewFuture = activeNetworkClient.getActiveView();
    activeViewFuture.then((activeView){
      print(activeView);
      expect((activeView.activeFoodRunners)!=null, true);
      expect((activeView.activeFoodRunnerQueue)!=null, true);
      expect((activeView.finderResults)!=null, true);
    });
  });

  test('sendDeliveryNotification', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Location location = new Location(30.25860595703125,-97.74873352050781);

    Future<Profile> profileFuture = profileRestClient.getProfile("m@s.com");
    profileFuture.then((profile){
      profile.setLocation(location);
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
            Map<String,dynamic> json = jsonDecode(value);
            expect(200,json['statusCode']);
          });
      }); 
    });
  });

  /*test('sendPickupRequest', () {
    ActiveNetworkRestClient client = ActiveNetworkRestClient();
    SourceOrg sourceOrg = new SourceOrg("test", "TEST", "testing@test.com", null);
    PickupRequest pickupRequest = new PickupRequest(sourceOrg);
    Future<Iterable> future = client.sendPickupRequest(pickupRequest);
    future.then((profiles){
      for(Map<String, dynamic> json in profiles)
      {
        Profile profile = Profile.fromJson(json);
        print(profile.toString());
      }
    });
  });*/

  /*test('sendFoodRequest', () {
    ActiveNetworkRestClient client = ActiveNetworkRestClient();
    SourceOrg sourceOrg = new SourceOrg("test", "TEST", "testing@test.com", null);
    FoodRequest foodRequest = new FoodRequest("id", "VEG", sourceOrg);
    Future<String> future = client.sendFoodRequest(foodRequest);
    future.then((jsonString){
      print(jsonString);
    });
  });*/

  /*test('getSourceOrgs', () {
    ActiveNetworkRestClient client = ActiveNetworkRestClient();
    Future<List<SourceOrg>> future = client.getSourceOrgs();
    future.then((sourceOrgs){
      print(sourceOrgs.toString());
    }); 
  });*/

  /*test('login', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    AuthCredentials credentials = new AuthCredentials();
    credentials.email = "blah@blah.com";
    credentials.password = "blahblah";
    Future<AuthCredentials> future = profileRestClient.login(credentials);
    future.then((authCredentials){
      print(authCredentials.toString());
    });
  });*/

  /*test('String.trim() removes surrounding whitespace', () {
    var string = '  foo ';
    expect(string.trim(), equals('foo'));
  });*/

  /*test('findBestDestination', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Iterable> future = profileRestClient.findBestDestination(new FoodRunner(new Profile("id","email","mobile","phone"), new Location(0.0, 0.0)));
    future.then((sourceOrgs){
      for(Map<String, dynamic> json in sourceOrgs)
      {
        SourceOrg sourceOrg = SourceOrg.fromJson(json);
        Location location = new Location(0.0, 0.0);
        sourceOrg.location = location;
        print(sourceOrg.toString());
      }
    });
  });*/
}