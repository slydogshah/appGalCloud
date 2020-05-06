// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/ui/pickupSource.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';

import 'landingScene.dart';

class Login extends StatelessWidget
{
  @override
  Widget build(BuildContext context) {
   MaterialApp materialApp = new MaterialApp(home: new LoginScene());
   return materialApp;
  }
}

class LoginScene extends StatelessWidget {

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    final cursorColor = Theme.of(context).cursorColor;
    const sizedBoxSpace = SizedBox(height: 24);
    ProfileFunctions profileFunctions = new ProfileFunctions();

    TextFormField email = TextFormField(
                  controller: TextEditingController(),
                  textCapitalization: TextCapitalization.words,
                  cursorColor: cursorColor,
                  decoration: InputDecoration(
                    filled: true,
                    icon: Icon(Icons.person),
                    //hintText: "Your email address",
                    labelText:
                        "Email",
                  )
                );

    TextField password = TextField(
            controller: TextEditingController(),
            obscureText: true,
            decoration: InputDecoration(
                    filled: true,
                    icon: Icon(Icons.person),
                    //hintText: "Your email address",
                    labelText:
                        "Password",
            )
    );

    Scrollbar scrollbar = new Scrollbar(child: SingleChildScrollView(
            dragStartBehavior: DragStartBehavior.down,
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                sizedBoxSpace,
                email,
                sizedBoxSpace,
                password,
                sizedBoxSpace,
                ButtonTheme.bar(
                  child: ButtonBar(
                    children: <Widget>[
                      FlatButton(
                        child: const Text('Register', style: TextStyle(color: Colors.black)),
                        onPressed: () {
                          Navigator.push(context,MaterialPageRoute(builder: (context) => new Registration()));
                        },
                      ),
                    ],
                  ),
                ),
                sizedBoxSpace,
                Center(
                  child:
                    RaisedButton(
                      child: Text("Login"),
                      onPressed: () 
                      {
                        profileFunctions.showAlertDialog(context, email.controller.text, password.controller.text);
                      }
                    )
                )
              ],
            )
          )
    );

    Form form = new Form(child: scrollbar);

    AppBar appBar = new AppBar(automaticallyImplyLeading: false, title: new Text("Login"),);
    Scaffold scaffold = new Scaffold(appBar: appBar, body: form,);
    return scaffold;
  }
}

class Registration extends StatelessWidget
{
  @override
  Widget build(BuildContext context) {
   MaterialApp materialApp = new MaterialApp(home: new RegistrationScene());
   return materialApp;
  }
  
}

class RegistrationScene extends StatelessWidget {

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    final cursorColor = Theme.of(context).cursorColor;
    const sizedBoxSpace = SizedBox(height: 24);
    ProfileFunctions profileFunctions = new ProfileFunctions();
    Scrollbar scrollbar = new Scrollbar(child: SingleChildScrollView(
            dragStartBehavior: DragStartBehavior.down,
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                sizedBoxSpace,
                TextFormField(
                  textCapitalization: TextCapitalization.words,
                  cursorColor: cursorColor,
                  decoration: InputDecoration(
                    filled: true,
                    icon: Icon(Icons.person),
                    hintText: "Your email address",
                    labelText:
                        "Email",
                  )
                ),
                sizedBoxSpace,
                PasswordField(fieldKey: new Key("0"),
                hintText: "Password", 
                labelText: "Password",
                helperText: "Password", obscureText: "Password",),
                sizedBoxSpace,
                TextFormField(
                  textCapitalization: TextCapitalization.words,
                  cursorColor: cursorColor,
                  decoration: InputDecoration(
                    filled: true,
                    icon: Icon(Icons.phone),
                    hintText: "Your mobile number",
                    labelText:
                        "Mobile",
                  )
                ),
                sizedBoxSpace,
                Center(
                  child: RaisedButton(
                    child: Text("Register"),
                    onPressed: () 
                    {
                      profileFunctions.showAlertDialog(context, "", ""); //TODO: FIXME
                    }
                  )
                ),
              ],
            )
          )
    );

    Form form = new Form(child: scrollbar);

    AppBar appBar = new AppBar(automaticallyImplyLeading: false, title: new Text("Register"),);
    Scaffold scaffold = new Scaffold(appBar: appBar, body: form,);
    return scaffold;
  }
}

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
    login(context, credentials);
  }

  void login (BuildContext context, AuthCredentials authCredentials) {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<AuthCredentials> future = profileRestClient.login(authCredentials);
    future.then((authCredentials){
      ActiveSession activeSession = ActiveSession.getInstance();
      Profile profile = activeSession.getProfile();
      profile.setLatitude(authCredentials.latitude);
      profile.setLongitude(authCredentials.longitude);


      print(profile.getLatitude());
      print(profile.getLongitude());
      
      Navigator.of(context, rootNavigator: true).pop();

      Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => new LandingScene()));

      showCards(context, profile);
    });
  }

  void showCards(BuildContext context, Profile profile) {
    sleep(const Duration(seconds:5));
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Iterable> futureP = profileRestClient.findBestDestination(new FoodRunner(new Profile("id","email","mobile","phone"), new Location(0.0, 0.0)));
    futureP.then((sourceOrgs){
      Map<String, dynamic> json = sourceOrgs.elementAt(0);
      SourceOrg sourceOrg = SourceOrg.fromJson(json);
      Navigator.push(context, MaterialPageRoute(builder: (context) => new PickupSource(sourceOrg)));
    });
  }  
}



class PasswordField extends StatelessWidget {
  const PasswordField({
    this.fieldKey,
    this.hintText,
    this.labelText,
    this.helperText,
    this.obscureText,
    this.onSaved,
    this.validator,
    this.onFieldSubmitted,
  });

  final Key fieldKey;
  final String hintText;
  final String labelText;
  final String helperText;
  final String obscureText;
  final FormFieldSetter<String> onSaved;
  final FormFieldValidator<String> validator;
  final ValueChanged<String> onFieldSubmitted;

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      key: this.fieldKey,
      cursorColor: Theme.of(context).cursorColor,
      maxLength: 8,
      onSaved: this.onSaved,
      validator: this.validator,
      onFieldSubmitted: this.onFieldSubmitted,
      decoration: InputDecoration(
        filled: true,
        hintText: this.hintText,
        labelText: this.labelText,
        helperText: this.helperText,
        icon: Icon(Icons.pregnant_woman),
        suffixIcon: GestureDetector(
          dragStartBehavior: DragStartBehavior.down,
          //onTap: () {
          //  setState(() {
          //    _obscureText = !_obscureText;
          //  });
          //},
          child: Icon(
            true ? Icons.visibility : Icons.visibility_off,  //(lol)
            semanticLabel: true
                ? "Visible"
                : "Hidden",
          ),
        ),
      ),
    );
  }
}