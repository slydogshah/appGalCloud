import 'dart:convert';

import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/context/securityToken.dart';
import 'package:app/src/messaging/polling/cloudDataPoller.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/model/foodRunnerLoginData.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/cloudBusinessException.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/ui/foodRunner.dart';
import 'package:app/src/ui/registration.dart';
import 'package:app/src/ui/app.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';

class ProfileFunctions
{
  void showAlertDialog(BuildContext context, final LoginView loginState,
      final LoginState loginScene,
      final TextFormField emailField,
      final TextFormField passwordField)
  {
    String email = emailField.controller.text;
    String password = passwordField.controller.text;

    if(email == null || email.isEmpty)
    {
        email = null;
    }
    if(password == null || password.isEmpty)
    {
       password = null;
    }

    AuthCredentials credentials = new AuthCredentials();
    credentials.email = email;
    credentials.password = password;
    login(context,loginState, loginScene, credentials, emailField, passwordField);
  }

  void showAlertDialogRegistration(BuildContext context,final RegistrationState registrationState, final RegisterView state,
      final TextFormField emailField,
      final TextFormField passwordField,
      final String profileType)
  {
    final String email = emailField.controller.text;
    final String password = passwordField.controller.text;
    if(email.isEmpty || password.isEmpty)
    {
      emailField.controller.value = new TextEditingValue(text:email);
      passwordField.controller.value = new TextEditingValue(text:password);

      String emailIsRequired;
      String passwordIsRequired;
      if(email.isEmpty)
        {
          emailIsRequired = "Email is required";
        }
      if(password.isEmpty)
        {
          passwordIsRequired = "Password is required";
        }
      registrationState.notifyEmailIsInvalid(email,password,emailIsRequired,passwordIsRequired);
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

    Profile profile = new Profile("", email, "123", "", password);
    profile.setProfileType(profileType);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Map<String,dynamic>> future = profileRestClient.register(profile);
    future.then((json){
      Navigator.of(context, rootNavigator: true).pop();

      if(json['violations'] != null)
      {
        List<dynamic> errors = json['violations'];
        bool isEmailInvalid = false;
        errors.forEach((element) {
          if (element.startsWith("email")) {
            isEmailInvalid = true;
          }
        });
        if(isEmailInvalid) {
          emailField.controller.value = new TextEditingValue(text: email);
          passwordField.controller.value = new TextEditingValue(text: password);
          registrationState.notifyEmailIsInvalid(
              email, password, "Email is invalid", null);
        }
      }
      else if(json['statusCode'] == 409) {
        emailField.controller.value = new TextEditingValue(text:email);
        passwordField.controller.value = new TextEditingValue(text:password);
        registrationState.notifyEmailIsInvalid(email,password,"Email is already registered",null);
      }
      else {
        AuthCredentials credentials = new AuthCredentials();
        credentials.email = profile.email;
        credentials.password = profile.password;
        loginAfterRegistration(context, credentials,registrationState,emailField,passwordField,email,password);
      }
    }).catchError((e) {
      this.notifyRegistrationSystemError(context,registrationState,emailField,passwordField,email,password);
    });
  }

  void notifyRegistrationSystemError(BuildContext context,RegistrationState registrationState,final TextFormField emailField,
      final TextFormField passwordField,String email,String password){
    emailField.controller.value = new TextEditingValue(text:email);
    passwordField.controller.value = new TextEditingValue(text:password);
    registrationState.notifySystemError(email,password);
  }

  void login (BuildContext context,LoginView loginState, LoginState loginScene, AuthCredentials authCredentials,
      final TextFormField emailField, final TextFormField passwordField) {
    FoodRunnerLoginData foodRunnerLoginData = new FoodRunnerLoginData();
    foodRunnerLoginData.setAuthCredentials(authCredentials);

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
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Map<String,dynamic>> future = profileRestClient.login(authCredentials);
    future.then((json) {
      //print("*****************");
      //print(json);
      if(json['statusCode'] != 200)
      {
        Navigator.of(context, rootNavigator: true).pop();
        if(json['statusCode'] == 401)
        {
          String message = json['message'];
          if(message == "profile_not_found"){
            loginScene.notifyAuthFailed(
                "The email is not registered");
          }
          else {
            loginScene.notifyAuthFailed(
                "Login Failed: Email or Password error");
          }
          return;
        }
        else if(json['statusCode'] == 403){
          loginScene.notifyAuthFailed("The App is used by only by FoodRunners");
          return;
        }
        loginScene.notifySystemError("System Error: Please try again");
        return;
      }

      Profile foodRunner = Profile.fromJson(json);
      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(foodRunner);
      activeSession.foodRunner.offlineCommunitySupport = json["offlineCommunitySupport"];

      ActiveSession.getInstance().securityToken = new SecurityToken(foodRunner.email, foodRunner.bearerToken);

      CloudDataPoller.startPolling(context,foodRunner);
      LocationUpdater.startPolling(foodRunner);

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<Map<String,List<FoodRecoveryTransaction>>> future = client
          .getFoodRecoveryTransaction(foodRunner.email);
      future.then((txs) {
        Navigator.of(context, rootNavigator: true).pop();
        Navigator.push(context, MaterialPageRoute(
            builder: (context) => FoodRunnerApp(txs)));
      }).catchError((e) {
        loginScene.notifySystemError("System Error: Please try again");
      });
    }).catchError((e) {
      loginScene.notifySystemError("System Error: Please try again");
    });
  }

  void loginAfterRegistration (BuildContext context, AuthCredentials authCredentials,RegistrationState registrationState,final TextFormField emailField,
      final TextFormField passwordField,String email,String password) {
    FoodRunnerLoginData foodRunnerLoginData = new FoodRunnerLoginData();
    foodRunnerLoginData.setAuthCredentials(authCredentials);

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
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Map<String,dynamic>> future = profileRestClient.login(authCredentials);
    future.then((json) {
      //print("********REGISTER*********");
      //print(json);
      if(json['statusCode'] != 200)
      {
        Navigator.of(context, rootNavigator: true).pop();
        AlertDialog dialog = AlertDialog(
          title: Text('System Error....'),
          content: Text(
            "Unknown System Error....",
            textAlign: TextAlign.left,
            style: TextStyle(
              fontWeight: FontWeight.w600,
              fontSize: 16,
            ),
          ),
          actions: [
            FlatButton(
              textColor: Color(0xFF6200EE),
              onPressed: () {
                Navigator.of(context, rootNavigator: true).pop();
              },
              child: Text('OK'),
            ),
          ],
        );

        // show the dialog
        showDialog(
          context: context,
          builder: (BuildContext context) {
            return dialog;
          },
        );
        return;
      }
      Profile foodRunner = Profile.fromJson(json);
      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(foodRunner);
      activeSession.foodRunner.offlineCommunitySupport = json["offlineCommunitySupport"];

      ActiveSession.getInstance().securityToken = new SecurityToken(foodRunner.email, foodRunner.bearerToken);

      CloudDataPoller.startPolling(context,foodRunner);
      LocationUpdater.startPolling(foodRunner);
      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<Map<String,List<FoodRecoveryTransaction>>> future = client
          .getFoodRecoveryTransaction(foodRunner.email);
      future.then((txs) {
        Navigator.of(context, rootNavigator: true).pop();
        /*if(txs['pending'].isNotEmpty || txs['inProgress'].isNotEmpty) {
          Navigator.push(context, MaterialPageRoute(
              builder: (context) => FoodRunnerApp(txs)));
        }else{
          Navigator.push(context, MaterialPageRoute(
              builder: (context) => TasksNotFound()));
        }*/
        Navigator.push(context, MaterialPageRoute(
            builder: (context) => FoodRunnerApp(txs)));
      }).catchError((e) {
        Navigator.of(context, rootNavigator: true).pop();
        this.notifyRegistrationSystemError(context,registrationState,emailField,passwordField,email,password);
      });
    }).catchError((e) {
      Navigator.of(context, rootNavigator: true).pop();
      this.notifyRegistrationSystemError(context,registrationState,emailField,passwordField,email,password);
    });
  }

  static void launchAppFromNotification(BuildContext context)
  {
    ActiveSession activeSession = ActiveSession.getInstance();
    Profile foodRunner = activeSession.getProfile();

    ActiveNetworkRestClient client = new ActiveNetworkRestClient();
    Future<Map<String,List<FoodRecoveryTransaction>>> future = client
        .getFoodRecoveryTransaction(foodRunner.email);
    future.then((txs) {
      Navigator.of(context, rootNavigator: true).pop();

      Navigator.push(context, MaterialPageRoute(
          builder: (context) => FoodRunnerApp(txs)));
    }).catchError((e) {
      Navigator.push(context, MaterialPageRoute(
          builder: (context) => JenNetworkApp()));
    });
  }


  //DEAD_CODE_CLEAN_LATER
  void showAlertDialogRegister(BuildContext context, final LoginView loginState,
      final LoginState loginScene,
      final TextFormField emailField,
      final TextFormField passwordField,
      final String profileType)
  {

  }
}