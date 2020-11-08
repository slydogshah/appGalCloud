import 'package:app/src/context/activeSession.dart';
import 'package:app/src/messaging/polling/cloudDataPoller.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRunnerLoginData.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';

import 'marker_icons.dart';

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

    //print("EMAIL: "+email);
    //print("PASSWORD: "+password);

    AuthCredentials credentials = new AuthCredentials();
    credentials.email = email;
    credentials.password = password;
    login(context, dialog, credentials);
  }

  void showAlertDialogRegistration(BuildContext context, String email, String password, String mobile,
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

    print("EMAIL: "+email);
    print("PASSWORD: "+password);
    print("PROFILE_TYPE: "+profileType);

    Profile profile = new Profile("", email, mobile, "", password);
    profile.setProfileType(profileType);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    profileRestClient.register(profile);
    AuthCredentials credentials = new AuthCredentials();
    credentials.email = profile.email;
    credentials.password = profile.password;
    login(context, dialog, credentials);
  }

  void login (BuildContext context, SimpleDialog dialog, AuthCredentials authCredentials) {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<FoodRunnerLoginData> future = profileRestClient.login(authCredentials);
    future.then((FoodRunnerLoginData){
      AuthCredentials authCredentials = FoodRunnerLoginData.authCredentials;

      //Navigator.of(context, rootNavigator: true).pop();
      Navigator.pop(context);

      if(authCredentials.statusCode == 401)
      {
          return;
      }

      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(authCredentials.getProfile());

      Profile profile = activeSession.getProfile();
      String profileType = profile.getProfileType();

      if(profileType == "FOOD_RUNNER")
      {
        //LandingScene landingScene = new LandingScene(profile);
        //Navigator.push(context,MaterialPageRoute(builder: (context) => landingScene));
        //LandingSceneState landingSceneState = landingScene.getLandingSceneState();
        //landingSceneState.map();
        Navigator.push(context,MaterialPageRoute(builder: (context) => FoodRunnerMainScene(FoodRunnerLoginData.sourceOrgs)));
      }
      else
      {
        showCards(context, profile);
      }
    });
  }

  void showCards(BuildContext context, Profile profile) {
    CloudDataPoller.startPolling(context);
  }  
}