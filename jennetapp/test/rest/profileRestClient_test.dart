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

  test('registerAndLogin', () async {
    var uuid = Uuid();

    // Generate a v1 (time-based) id
    var v1 = uuid.v1();
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Profile profile = new Profile(
        null, "testsuite"+v1+"@blah.com", "8675309", "photu", "password");
    profile.setProfileType("FOOD_RUNNER");

    Profile newProfile = await profileRestClient.register(profile);
    expect(newProfile.email, profile.email);

    AuthCredentials credentials = new AuthCredentials();
    credentials.email = profile.email;
    credentials.password = profile.password;
    FoodRunnerLoginData foodRunnerLoginData = await profileRestClient.login(credentials);
    print(foodRunnerLoginData.getAuthCredentials());
    expect(credentials.email, foodRunnerLoginData.getAuthCredentials().email);
  });

  test('registerValidationFailure', () async {
    var uuid = Uuid();

    // Generate a v1 (time-based) id
    var v1 = uuid.v1();
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Profile profile = new Profile(
        null, "testsuite"+v1+"/blah.com", "8675309", "photu", "password");
    profile.setProfileType("FOOD_RUNNER");
    print(profile);

    Profile rejectedProfile = await profileRestClient.register(profile);
    Map<String,dynamic> validationError = rejectedProfile.validationError;
    print(validationError);
    List<dynamic> values = validationError['violations'];
    expect("email_invalid", values.elementAt(0));
  });

  test('loginFail', () async {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    AuthCredentials credentials = new AuthCredentials();
    credentials.email = "notFound@blah.com";
    credentials.password = "c";
    FoodRunnerLoginData foodRunnerLoginData = await profileRestClient.login(credentials);
    AuthCredentials authCredentials = foodRunnerLoginData.authCredentials;
    print(authCredentials);
    print(foodRunnerLoginData.authFailure);
    expect(authCredentials.statusCode, 401);
  });
}