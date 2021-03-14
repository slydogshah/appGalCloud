// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:app/src/ui/profileOptions.dart';
import 'package:app/src/ui/registration.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';

import 'profileFunctions.dart';

class JenNetworkApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    Color primaryColor = Color(0xFF383EDB);
    Color backgroundColor = Color(0xFF383EDB);
    MaterialApp materialApp = new MaterialApp(
        home: new Login(),
        theme: ThemeData(
            primaryColor: primaryColor,
            backgroundColor: backgroundColor,
            accentColor: backgroundColor,
            accentColorBrightness: Brightness.dark
        )
    );
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
  String email;
  String password;
  bool authenticationFailed = false;
  Map<String,dynamic> authFailure;

  void notifyLoginFailed(Map<String,dynamic> authFailure)
  {
    setState(() {
      // This call to setState tells the Flutter framework that
      // something has changed in this State, which causes it to rerun
      // the build method below so that the display can reflect the
      // updated values. If you change _counter without calling
      // setState(), then the build method won't be called again,
      // and so nothing would appear to happen.
      this.authenticationFailed = true;
      this.authFailure = authFailure;
    });
  }

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
                  ),
                  autovalidateMode:AutovalidateMode.always,
                  validator: (value){
                    if(this.authenticationFailed)
                    {
                      if(this.authFailure['message'].startsWith("profile_not_found")) {
                        return "The email is not registered.";
                      }
                      else
                      {
                        return null;
                      }
                    }
                    else {
                      return null;
                    }
                  },
                );

    TextFormField password = TextFormField(
            controller: TextEditingController(),
            obscureText: true,
            decoration: InputDecoration(
                    filled: true,
                    icon: Icon(Icons.visibility_off),
                    //hintText: "Your email address",
                    labelText:
                        "Password",
            ),
            autovalidateMode:AutovalidateMode.always,
            validator: (value){
              if(this.authenticationFailed)
              {
                if(this.authFailure['message'].startsWith("password_mismatch")) {
                  return "Password does not match.";
                }
                else
                {
                  return null;
                }
              }
              else {
                return null;
              }
            },
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
                        profileFunctions.showAlertDialog(context, this, email, password);
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
      bottomNavigationBar: Container(
        height: 60,
        color: Colors.black12,
        child: InkWell(
        onTap: () => Navigator.push(context,MaterialPageRoute(builder: (context) => new Registration())),
        child: Padding(
        padding: EdgeInsets.only(top: 8.0),
        child: Column(
        children: <Widget>[
        Icon(
        Icons.person_add,
        color: Theme.of(context).accentColor,
        ),
        Text('Register'),
        ],
        ),
        ),
        ),
        )
    );
    return scaffold;
  }
}