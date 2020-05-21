import 'package:app/src/model/activeView.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:test/test.dart';

void main() {
  test('getProfile', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Profile> profileFuture = profileRestClient.getProfile("c@s.com");
    profileFuture.then((profile){
      //expect(profile.email, "blah@blah.com");
      print(profile.toString());
      print(profile.latitude);
    });
  });

  /*test('getActiveView', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<ActiveView> activeViewFuture = profileRestClient.getActiveView();
    activeViewFuture.then((activeView){
      print(activeView);
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

  /*test('sendDeliveryNotification', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Location location = new Location(30.25860595703125,-97.74873352050781);
    SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",location);
    Profile profile = new Profile("123", "bugs.bunny.shah@gmail.com", "8675309", "");
    FoodRunner foodRunner = new FoodRunner(profile, location);
    DropOffNotification dropOffNotification = new DropOffNotification(sourceOrg, location, foodRunner);
    profileRestClient.sendDeliveryNotification(dropOffNotification);
  });*/
}