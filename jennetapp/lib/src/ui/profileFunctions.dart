import 'package:app/design_course/home_design_course.dart';
import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/messaging/polling/cloudDataPoller.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/model/foodRunnerLoginData.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/ui/emptyHome.dart';
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
    login(context, dialog, loginState, loginScene, credentials, emailField, passwordField);
  }

  void showAlertDialogRegister(BuildContext context, final LoginView loginState,
      final LoginState loginScene,
      final TextFormField emailField,
      final TextFormField passwordField,
      final String profileType)
  {
    final String email = emailField.controller.text;
    final String password = passwordField.controller.text;

    // set up the SimpleDialog
    SimpleDialog dialog = SimpleDialog(
        children: [CupertinoActivityIndicator()]
    );

    String emailRejectedMessage;
    String passwordRejectedMessage;
    Profile profile = new Profile("", email, "123", "", password);
    profile.setProfileType(profileType);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Map<String,dynamic>> future = profileRestClient.register(profile);
    future.then((json){
      if(json['violations'] != null)
      {
        Navigator.of(context, rootNavigator: true).pop();

        List<dynamic> errors = json['violations'];
        bool emailIsInvalid = false;
        errors.forEach((element) {
          if (element == "email_required") {
            emailRejectedMessage = "Email is required";
          }
          else if(element == "email_invalid"  && emailRejectedMessage == null)
          {
            emailRejectedMessage = email+" is invalid";
          }
          else if(element == "password_required")
          {
            passwordRejectedMessage = "Password is required";
          }
        });

        loginScene.notifyValidationFailure(emailRejectedMessage, passwordRejectedMessage);
      }
      else {
        AuthCredentials credentials = new AuthCredentials();
        credentials.email = profile.email;
        credentials.password = profile.password;
        registration(context, dialog, loginState, loginScene, credentials);
      }
    });
  }

  void registration (BuildContext context,SimpleDialog dialog, LoginView loginState, LoginState loginScene, AuthCredentials authCredentials) {
    FoodRunnerLoginData foodRunnerLoginData = new FoodRunnerLoginData();
    foodRunnerLoginData.setAuthCredentials(authCredentials);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Map<String,dynamic>> future = profileRestClient.login(authCredentials);
    future.then((json) {
      if(json['statusCode'] != 200)
      {
        Navigator.of(context, rootNavigator: true).pop();
        loginScene.notifySystemError("System Error: Please try again");
        return;
      }


      Profile foodRunner = Profile.fromJson(json);

      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(foodRunner);
      activeSession.foodRunner.offlineCommunitySupport = json["offlineCommunitySupport"];

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<Map<String,List<FoodRecoveryTransaction>>> future = client
          .getFoodRecoveryTransaction(foodRunner.email);
      future.then((txs) {
        Navigator.push(context, MaterialPageRoute(
            builder: (context) => FoodRunnerMainScene(txs)));
      }).catchError((e) {
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
                Navigator.pop(context);
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
      });

      showCards(context, foodRunner);
    });
  }

  void login (BuildContext context, SimpleDialog dialog, LoginView loginState, LoginState loginScene, AuthCredentials authCredentials,
      final TextFormField emailField, final TextFormField passwordField) {
    FoodRunnerLoginData foodRunnerLoginData = new FoodRunnerLoginData();
    foodRunnerLoginData.setAuthCredentials(authCredentials);
    ProfileRestClient profileRestClient = new ProfileRestClient();

    Future<Map<String,dynamic>> future = profileRestClient.login(authCredentials);
    future.then((json) {

      if(json['statusCode'] != 200)
      {
        Navigator.of(context, rootNavigator: true).pop();
        if(json['statusCode'] == 401)
        {
          loginScene.notifyAuthFailed("Login Failed: Email or Password error");
          return;
        }
        loginScene.notifySystemError("System Error: Please try again");
        return;
      }
      Profile foodRunner = Profile.fromJson(json);
      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(foodRunner);
      activeSession.foodRunner.offlineCommunitySupport = json["offlineCommunitySupport"];

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<Map<String,List<FoodRecoveryTransaction>>> future = client
          .getFoodRecoveryTransaction(foodRunner.email);
      future.then((txs) {
        Navigator.of(context, rootNavigator: true).pop();
        Navigator.push(context, MaterialPageRoute(
            builder: (context) => FoodRunnerMainScene(txs)));

      }).catchError((e) {
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
                Navigator.pop(context);
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
      });

      showCards(context, foodRunner);
    });
  }

  void showCards(BuildContext context, Profile profile) 
  {
    //print("PROFILE: $profile");
    CloudDataPoller.startPolling(context,profile);
    LocationUpdater.startPolling(profile);
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
          builder: (context) => FoodRunnerMainScene(txs)));
    }).catchError((e) {
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
              Navigator.pop(context);
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
    });
  }























































  void showAlertDialogRegistration(BuildContext context,final RegistrationState registrationState, final RegisterView state,
      final TextField emailField,
      final TextField passwordField,
      final String profileType)
  {
    final String email = emailField.controller.text;
    final String password = passwordField.controller.text;
    if(email.isEmpty || password.isEmpty)
    {
      emailField.controller.value = new TextEditingValue(text:email);
      passwordField.controller.value = new TextEditingValue(text:password);
      registrationState.notifyEmailIsInvalid(email,password,email.isEmpty,password.isEmpty);
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
      //Navigator.of(context, rootNavigator: true).pop();

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
        registrationState.notifyEmailIsInvalid(email,password,emailIsInvalid,passwordIsRequired);
      }
      else {
        AuthCredentials credentials = new AuthCredentials();
        credentials.email = profile.email;
        credentials.password = profile.password;
        loginAfterRegistration(context, credentials);
      }
    });
  }

  void loginAfterRegistration (BuildContext context, AuthCredentials authCredentials) {
    FoodRunnerLoginData foodRunnerLoginData = new FoodRunnerLoginData();
    foodRunnerLoginData.setAuthCredentials(authCredentials);
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Map<String,dynamic>> future = profileRestClient.login(authCredentials);
    future.then((json) {
      Navigator.of(context, rootNavigator: true).pop();

      if(json['statusCode'] != 200)
      {
        //TODO: show message
        return;
      }


      Profile foodRunner = Profile.fromJson(json);

      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(foodRunner);

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<Map<String,List<FoodRecoveryTransaction>>> future = client
          .getFoodRecoveryTransaction(foodRunner.email);
      future.then((txs) {
        //Navigator.pushReplacement(context, MaterialPageRoute(
        //    builder: (context) => FoodRunnerMainScene(txs)));
        if(txs!=null && !txs.isEmpty) {
          Navigator.pushReplacement(context, MaterialPageRoute(
              builder: (context) => FoodRunnerMainScene(txs)));
        }
        else
        {
          Navigator.push(context, MaterialPageRoute(
              builder: (context) => DesignCourseHomeScreen()));
        }

        Navigator.push(context, MaterialPageRoute(
            builder: (context) => FoodRunnerMainScene(txs)));
      }).catchError((e) {
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
                Navigator.pop(context);
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
      });

      showCards(context, foodRunner);
    });
  }
}