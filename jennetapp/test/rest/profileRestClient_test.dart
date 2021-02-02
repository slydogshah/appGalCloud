import 'dart:convert';
import 'package:uuid/uuid.dart';


import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRunnerLoginData.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/cloudBusinessException.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:test/test.dart';

//Future<void> main() async {
void main() {

  test('loginSuccess', () async {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    AuthCredentials credentials = new AuthCredentials();
    credentials.email = "b@z.com";
    credentials.password = "by";
    FoodRunnerLoginData foodRunnerLoginData = await profileRestClient.login(credentials);
    AuthCredentials authCredentials = foodRunnerLoginData.authCredentials;
    Profile profile = authCredentials.getProfile();
    print(profile.toString());
    //expect(profile.email, "m@s.com");
  });

  test('register', () async {
    var uuid = Uuid();

    // Generate a v1 (time-based) id
    var v1 = uuid.v1();
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Profile profile = new Profile(
        null, "testsuite"+v1+"@blah.com", 8675309, "photu", "password");
    profile.setProfileType("FOOD_RUNNER");
    //print(profile);

    Profile newProfile = await profileRestClient.register(profile);
    print(newProfile);
    expect(newProfile.email, profile.email);
  });

  test('registerValidationFailure', () async {
    var uuid = Uuid();

    // Generate a v1 (time-based) id
    var v1 = uuid.v1();
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Profile profile = new Profile(
        null, "testsuite"+v1+"/blah.com", 8675309, "photu", "password");
    profile.setProfileType("FOOD_RUNNER");
    print(profile);

    Profile rejectedProfile = await profileRestClient.register(profile);
    Map<String,dynamic> validationError = rejectedProfile.validationError;
    print(validationError);
    List<dynamic> values = validationError['violations'];
    expect("email_invalid", values.elementAt(0));
  });

  test('profile404', () async {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    try {
      await profileRestClient.getProfile(
          "notFound@blah.com");
    }on CloudBusinessException catch(e)
    {
      print(e);
      expect(e.statusCode, 404);
    }
  });

  //TODO: DEBUG
  /*test('profileSuccess', () async {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Profile profile = await profileRestClient.getProfile("m@s.com");
    print(profile.toString());
    expect(profile.email, "m@s.com");
  });*/

  //TODO: DEBUG
  /*test('loginFail', () async {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    AuthCredentials credentials = new AuthCredentials();
    credentials.email = "m@s.com";
    credentials.password = "c";
    FoodRunnerLoginData foodRunnerLoginData = await profileRestClient.login(credentials);
    print(foodRunnerLoginData);
    AuthCredentials authCredentials = foodRunnerLoginData.authCredentials;
    expect(authCredentials.statusCode, 401);
  });*/
}