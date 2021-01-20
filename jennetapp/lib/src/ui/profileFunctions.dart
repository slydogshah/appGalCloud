import 'dart:ffi';

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

    //print("EMAIL: "+email);
    //print("PASSWORD: "+password);

    AuthCredentials credentials = new AuthCredentials();
    credentials.email = email;
    credentials.password = password;
    login(context, dialog, credentials);
  }

  void showAlertDialogRegistration(BuildContext context, String email, String password, int mobile,
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

    //print("EMAIL: "+email);
    //print("PASSWORD: "+password);
    //print("PROFILE_TYPE: "+profileType);

    Profile profile = new Profile("", email, 123456789, "", password);
    profile.setProfileType(profileType);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Profile> future = profileRestClient.register(profile);
    future.catchError((cbe){
          CloudBusinessException cloudBusinessException = cbe;
          Navigator.of(context, rootNavigator: true).pop();
          _handleClickMe(context);
    });
    future.then((profile){
      AuthCredentials credentials = new AuthCredentials();
      credentials.email = profile.email;
      credentials.password = profile.password;
      login(context, dialog, credentials);
    });
  }

  Future<void> _handleClickMe(BuildContext context) async {
    return showDialog<void>(
      context: context,
      barrierDismissible: false, // user must tap button!
      builder: (BuildContext context) {
        return CupertinoAlertDialog(
          title: Text('Allow "Maps" to access your location while you use the app?'),
          content: Text('Your current location will be displayed on the map and used for directions, nearby search results, and estimated travel times.'),
          actions: <Widget>[
            CupertinoDialogAction(
              child: Text('Don\'t Allow'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
            CupertinoDialogAction(
              child: Text('Allow'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  void login (BuildContext context, SimpleDialog dialog, AuthCredentials authCredentials) {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<FoodRunnerLoginData> future = profileRestClient.login(authCredentials);
    future.then((FoodRunnerLoginData){
      Navigator.of(context, rootNavigator: true).pop();

      print("*************************************");
      print(FoodRunnerLoginData.authCredentials);
      print(FoodRunnerLoginData.sourceOrgs);
      print("*************************************");


      AuthCredentials authCredentials = FoodRunnerLoginData.authCredentials;
      if(authCredentials.statusCode == 401)
      {
          return;
      }

      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(authCredentials.getProfile());
      Profile profile = activeSession.getProfile();

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<FoodRecoveryTransaction> future = client.getFoodRecoveryTransaction();
      future.then((foodRecoveryTransaction){
        print(foodRecoveryTransaction);
        List<FoodRecoveryTransaction> recoveryTxs = new List();
        recoveryTxs.add(foodRecoveryTransaction);

        Navigator.push(context,MaterialPageRoute(builder: (context) => FoodRunnerMainScene(recoveryTxs)));
      });

      showCards(context, profile);
    });
  }

  void showCards(BuildContext context, Profile profile) 
  {
    CloudDataPoller.startPolling(profile);
  }  
}