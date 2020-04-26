// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';
import 'dart:ui';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/ui/appGalFoodRunnerCards.dart';
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
                        profileFunctions.showAlertDialog(context);
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
    List<Item> users = <Item>[
    const Item('Android',Icon(Icons.android,color:  const Color(0xFF167F67),)),
    const Item('Flutter',Icon(Icons.flag,color:  const Color(0xFF167F67),)),
    const Item('ReactNative',Icon(Icons.format_indent_decrease,color:  const Color(0xFF167F67),)),
    const Item('iOS',Icon(Icons.mobile_screen_share,color:  const Color(0xFF167F67),)),
    ];
    Item selectedUser;
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
                  child:  DropdownButton<Item>(
                    hint:  Text("Select item"),
                    value: selectedUser,
                    onChanged: (Item Value) {
                      /*setState(() {
                        selectedUser = Value;
                      });*/
                    },
                    items: users.map((Item user) {
                      return  DropdownMenuItem<Item>(
                        value: user,
                        child: Row(
                          children: <Widget>[
                            user.icon,
                            SizedBox(width: 10,),
                            Text(
                              user.name,
                              style:  TextStyle(color: Colors.black),
                            ),
                          ],
                        ),
                      );
                    }).toList(),
                  ),
                ),
                sizedBoxSpace,
                Center(
                  child: RaisedButton(
                    child: Text("Register"),
                    onPressed: () 
                    {
                      profileFunctions.showAlertDialog(context);
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
  void showAlertDialog(BuildContext context) 
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
    credentials.email = "blah@blah.com";
    credentials.password = "blahblah";
    login(context, credentials);
  }

  void login (BuildContext context, AuthCredentials authCredentials) {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<AuthCredentials> future = profileRestClient.login(authCredentials);
    future.then((response){
      print(response.toString());

      Navigator.of(context, rootNavigator: true).pop();

      //Navigator.push(
      //context,
      //MaterialPageRoute(builder: (context) => new LandingScene(response)));
      Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => new CardsDemo(ActiveSession.getInstance().getProfile())));
    });
  }  
}

class DropdownScreen extends StatefulWidget {
  State createState() =>  DropdownScreenState();
}
class DropdownScreenState extends State<DropdownScreen> {
  Item selectedUser;
  List<Item> users = <Item>[
    const Item('Android',Icon(Icons.android,color:  const Color(0xFF167F67),)),
    const Item('Flutter',Icon(Icons.flag,color:  const Color(0xFF167F67),)),
    const Item('ReactNative',Icon(Icons.format_indent_decrease,color:  const Color(0xFF167F67),)),
    const Item('iOS',Icon(Icons.mobile_screen_share,color:  const Color(0xFF167F67),)),
  ];
  @override
  Widget build(BuildContext context) {
    return  MaterialApp(
      home:  Scaffold(
        appBar: AppBar(
          backgroundColor: const Color(0xFF167F67),
          title: Text(
            'Dropdown options',
            style: TextStyle(color: Colors.white),
          ),
        ),
        body:  Center(
          child:  DropdownButton<Item>(
            hint:  Text("Select item"),
            value: selectedUser,
            onChanged: (Item Value) {
              setState(() {
                selectedUser = Value;
              });
            },
            items: users.map((Item user) {
              return  DropdownMenuItem<Item>(
                value: user,
                child: Row(
                  children: <Widget>[
                    user.icon,
                    SizedBox(width: 10,),
                    Text(
                      user.name,
                      style:  TextStyle(color: Colors.black),
                    ),
                  ],
                ),
              );
            }).toList(),
          ),
        ),
      ),
    );
  }
}
class Item {
  const Item(this.name,this.icon);
  final String name;
  final Icon icon;
}