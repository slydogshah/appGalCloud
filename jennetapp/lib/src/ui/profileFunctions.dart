import 'package:app/src/context/activeSession.dart';
import 'package:app/src/messaging/polling/cloudDataPoller.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunnerLoginData.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/cloudBusinessException.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/ui/foodRunner.dart';
import 'package:app/src/ui/registration.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';

class ProfileFunctions
{
  void showAlertDialog(BuildContext context, String email, String password) 
  {
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
    login(context, dialog, credentials);
  }

  void showAlertDialogRegistration(BuildContext context, final RegistrationState state, TextFormField emailField,
      final String email, String password, int mobile,
  String profileType) 
  {
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

    Profile profile = new Profile("", email, 123456789, "", password);
    profile.setProfileType(profileType);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Profile> future = profileRestClient.register(profile);
    future.then((profile){
      Navigator.of(context, rootNavigator: true).pop();
      if(profile.validationError != null)
      {
        List<dynamic> errors = profile.validationError['violations'];
        errors.forEach((element) {
          //TODO: UI_HANDLING
          if (element.startsWith("email")) {
            emailField.controller.value = new TextEditingValue(text:email);
            state.notifyEmailIsInvalid(email);
          }
          /*else if(element.startsWith("password"))
          {

          }
          else if(element.startsWith("phone"))
          {

          }*/
        });
      }
      else {
        AuthCredentials credentials = new AuthCredentials();
        credentials.email = profile.email;
        credentials.password = profile.password;
        login(context, dialog, credentials);
      }
    });
  }

  void login (BuildContext context, SimpleDialog dialog, AuthCredentials authCredentials) {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<FoodRunnerLoginData> future = profileRestClient.login(authCredentials);
    future.then((FoodRunnerLoginData){
      Navigator.of(context, rootNavigator: true).pop();


      AuthCredentials authCredentials = FoodRunnerLoginData.authCredentials;

      //TODO: UI_HANDLING
      if(authCredentials.statusCode == 401)
      {
          return;
      }

      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(authCredentials.getProfile());
      Profile profile = activeSession.getProfile();

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<List<FoodRecoveryTransaction>> future = client.getFoodRecoveryTransaction();
      future.then((txs){
        Navigator.push(context,MaterialPageRoute(builder: (context) => FoodRunnerMainScene(txs)));
      });

      showCards(context, profile);
    });
  }

  void showCards(BuildContext context, Profile profile) 
  {
    //CloudDataPoller.startPolling(profile);
  }  
}