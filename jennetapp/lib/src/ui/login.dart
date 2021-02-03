// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:app/src/ui/profileOptions.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';

import 'profileFunctions.dart';

class JenNetworkApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    MaterialApp materialApp = new MaterialApp(home: new Login());
    return materialApp;
  }
}


class Login extends StatefulWidget
{
  @override
  LoginState createState() => LoginState();

  @override
  Widget build(BuildContext context) {
    MaterialApp materialApp = new MaterialApp(home: new Login());
    return materialApp;
  }
}

class LoginState extends State<Login> {

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
                    icon: Icon(Icons.visibility_off),
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
                //ButtonTheme.bar(
                  //child: ButtonBar(
                  //  children: <Widget>[
                  //    FlatButton(
                  //      child: const Text('Register', style: TextStyle(color: Colors.black)),
                  //      onPressed: () {
                  //        Navigator.push(context,MaterialPageRoute(builder: (context) => new Registration()));
                  //      },
                  //    ),
                  //  ],
                  //),
                //),
                //sizedBoxSpace,
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
    Scaffold scaffold = new Scaffold(appBar: appBar, body: form,
      bottomNavigationBar: ProfileOptions.bottomNavigationBar(context)
    );
    return scaffold;
  }
}