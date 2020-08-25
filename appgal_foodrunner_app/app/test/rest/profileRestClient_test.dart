import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/activeView.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRunnerLoginData.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/cloudBusinessException.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:test/test.dart';

void main() {
  /*test('profileNotFound', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Profile> profileFuture = profileRestClient.getProfile("notFound@blah.com");
        profileFuture.catchError((cbe){
          CloudBusinessException cloudBusinessException = cbe;
          expect(cloudBusinessException.statusCode, 404);
    });
  });

  test('profileSuccess', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Profile> profileFuture = profileRestClient.getProfile("m@s.com");
    profileFuture.then((profile){
      print(profile.toString());
      expect(profile.email, "m@s.com");
    });
  });

  test('loginSuccess', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    AuthCredentials credentials = new AuthCredentials();
    credentials.email = "m@s.com";
    credentials.password = "s";
    Future<FoodRunnerLoginData> future = profileRestClient.login(credentials);
    future.then((foodRunnerLoginData){
      AuthCredentials authCredentials = foodRunnerLoginData.authCredentials;
      Profile profile = authCredentials.getProfile();
      print(profile.toString());
      expect(profile.email, "m@s.com");
    });
  });

  test('loginFail', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    AuthCredentials credentials = new AuthCredentials();
    credentials.email = "m@s.com";
    credentials.password = "c";
    Future<FoodRunnerLoginData> future = profileRestClient.login(credentials);
    future.then((foodRunnerLoginData){
      AuthCredentials authCredentials = foodRunnerLoginData.authCredentials;
      expect(authCredentials.statusCode, 401);
    });
  });*/

  test('register', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Profile profile = new Profile(null,"testsuite@blah.com","8675309","photu","password");
    profile.setProfileType("FOOD_RUNNER");
    profileRestClient.register(profile);
  });
}