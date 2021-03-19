import 'dart:convert';

import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/messaging/polling/cloudDataPoller.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunnerLoginData.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/ui/foodRunner.dart';
import 'package:app/src/ui/registration.dart';
import 'package:app/src/ui/app.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';

import 'package:app/src/rest/urlFunctions.dart';

class ProfileFunctions
{
  void showAlertDialog(BuildContext context, final LoginState loginState, final TextFormField emailField, final TextFormField passwordField)
  {
    final String email = emailField.controller.text;
    final String password = passwordField.controller.text;

    // set up the SimpleDialog
    SimpleDialog dialog = SimpleDialog(
      children: [CupertinoActivityIndicator()]
    );

    // show the dialog
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return dialog;
      },
    );

    AuthCredentials credentials = new AuthCredentials();
    credentials.email = email;
    credentials.password = password;
    login(context, dialog, loginState, credentials);
  }

  void showAlertDialogRegistration(BuildContext context, final RegistrationState state, final TextFormField emailField,
  final TextFormField passwordField,
  final TextFormField phoneField,
  final String profileType)
  {
    final String email = emailField.controller.text;
    final String password = passwordField.controller.text;
    final String mobile = phoneField.controller.text;
    if(email.isEmpty || password.isEmpty || mobile.isEmpty)
    {
      emailField.controller.value = new TextEditingValue(text:email);
      passwordField.controller.value = new TextEditingValue(text:password);
      phoneField.controller.value = new TextEditingValue(text:mobile);
      state.notifyEmailIsInvalid(email,password,mobile,email.isEmpty,password.isEmpty,mobile.isEmpty);
      return;
    }

    // set up the SimpleDialog
    SimpleDialog dialog = SimpleDialog(
      children: [CupertinoActivityIndicator()]
    );

    // show the dialog
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return dialog;
      },
    );

    Profile profile = new Profile("", email, mobile, "", password);
    profile.setProfileType(profileType);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Map<String,dynamic>> future = profileRestClient.register(profile);
    future.then((json){
      Navigator.of(context, rootNavigator: true).pop();

      if(json['violations'] != null)
      {
        List<dynamic> errors = json['violations'];
        bool emailIsInvalid = false;
        bool passwordIsRequired = false;
        bool phoneIsInvalid = false;
        errors.forEach((element) {
          if (element.startsWith("email")) {
            emailIsInvalid = true;
          }
          else if(element.startsWith("password"))
          {
            passwordIsRequired = true;
          }
          else if(element.startsWith("phone"))
          {
            phoneIsInvalid = true;
          }
        });

        emailField.controller.value = new TextEditingValue(text:email);
        passwordField.controller.value = new TextEditingValue(text:password);
        phoneField.controller.value = new TextEditingValue(text:mobile);
        state.notifyEmailIsInvalid(email,password,mobile,emailIsInvalid,passwordIsRequired,phoneIsInvalid);
      }
      else {
        AuthCredentials credentials = new AuthCredentials();
        credentials.email = profile.email;
        credentials.password = profile.password;
        loginAfterRegistration(context, dialog, credentials);
      }
    });
  }

  void loginAfterRegistration (BuildContext context, SimpleDialog dialog, AuthCredentials authCredentials) {
    FoodRunnerLoginData foodRunnerLoginData = new FoodRunnerLoginData();
    foodRunnerLoginData.setAuthCredentials(authCredentials);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Map<String,dynamic>> future = profileRestClient.login(authCredentials);
    future.then((json) {
      Navigator.of(context, rootNavigator: true).pop();

      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(authCredentials.getProfile());
      Profile profile = activeSession.getProfile();

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<List<FoodRecoveryTransaction>> future = client
          .getFoodRecoveryTransaction();
      future.then((txs) {
        Navigator.push(context, MaterialPageRoute(
            builder: (context) => FoodRunnerMainScene(txs)));
      });

      showCards(context, profile);
    });
  }

  void login (BuildContext context, SimpleDialog dialog, LoginState loginState, AuthCredentials authCredentials) {
    FoodRunnerLoginData foodRunnerLoginData = new FoodRunnerLoginData();
    foodRunnerLoginData.setAuthCredentials(authCredentials);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Map<String,dynamic>> future = profileRestClient.login(authCredentials);
    future.then((json) {
      Navigator.of(context, rootNavigator: true).pop();

      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(authCredentials.getProfile());
      Profile profile = activeSession.getProfile();

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<List<FoodRecoveryTransaction>> future = client
          .getFoodRecoveryTransaction();
      future.then((txs) {
        Navigator.push(context, MaterialPageRoute(
            builder: (context) => FoodRunnerMainScene(txs)));
      });

      showCards(context, profile);
    });
  }

  void showCards(BuildContext context, Profile profile) 
  {
    CloudDataPoller.startPolling(profile);
    LocationUpdater.startPolling(profile);
  }  
}