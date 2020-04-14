// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/ui/appGalFoodRunnerCards.dart';
import 'package:app/src/ui/landingScene.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';

class MyApp extends StatelessWidget
{
  @override
  Widget build(BuildContext context) {
   MaterialApp materialApp = new MaterialApp(home: new RegistrationScene());
   return materialApp;
  }
  
}

class RegistrationScene extends StatelessWidget {
  BuildContext context;

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    final cursorColor = Theme.of(context).cursorColor;
    const sizedBoxSpace = SizedBox(height: 24);
    this.context = context;
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
                    child: Text("Submit"),
                    onPressed: () 
                    {
                      showAlertDialog(context);
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

  
  void showAlertDialog(BuildContext context) 
  {
    // set up the SimpleDialog
    SimpleDialog dialog = SimpleDialog(
      //title: const Text('Choose an animal'),
      children: [
        CupertinoActivityIndicator(),]
        /*RaisedButton(
                    child: Text("Submit"),
                    onPressed: () 
                    {
                        createOrderMessage();
                    }
                  ),
      ]*/
    );

    // show the dialog
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return dialog;
      },
    );
    login();
  }

  void login () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Profile> profileFuture = profileRestClient.getProfile("blah@blah.com");
    profileFuture.then((profile){
      ActiveSession session = ActiveSession.getInstance();
      profileRestClient.register(session.getProfile());
      Navigator.of(this.context).pop();

      Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => new CardsDemo(profile)),
      );
    });
  }
}

class CupertinoProgressIndicatorDemo extends OverlayEntry {
  @override
  Widget build(BuildContext context) {
    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        automaticallyImplyLeading: false,
        middle: Text(
          "Registration In Progress",
        ),
      ),
      child: const Center(
        child: CupertinoActivityIndicator(),
      ),
    );
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