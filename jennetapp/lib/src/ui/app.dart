import 'package:app/hotel_booking/hotel_app_theme.dart';
import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/ui/registration.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';

import 'profileFunctions.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

class JenNetworkApp extends StatelessWidget {
  JenNetworkApp()
  {
    LocationUpdater.getLocation();
  }

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

    TextField emailTextField = TextField(
      controller: TextEditingController(),
      onChanged: (String txt) {},
      style: const TextStyle(
        fontSize: 18,
      ),
      cursorColor: HotelAppTheme.buildLightTheme().primaryColor,
      decoration: InputDecoration(
        border: InputBorder.none,
        hintText: 'Login...',
      ),
    );
    Widget email = this.getLoginUIBar(context,emailTextField);

    TextField passwordTextField = TextField(
      controller: TextEditingController(),
      onChanged: (String txt) {},
      obscureText: true,
      style: const TextStyle(
        fontSize: 18,
      ),
      cursorColor: HotelAppTheme.buildLightTheme().primaryColor,
      decoration: InputDecoration(
        border: InputBorder.none,
        hintText: 'Password...',
      ),
    );
    Widget password = this.getPasswordUIBar(context,profileFunctions,emailTextField,passwordTextField);

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
                /*Center(
                  child:
                    RaisedButton(
                      child: Text("Login"),
                      onPressed: () 
                      {
                        profileFunctions.showAlertDialog(context, this, email, password);
                      }
                    )
                )*/
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

  Widget getLoginUIBar(BuildContext context, TextField emailTextField) {
    return Padding(
      padding: const EdgeInsets.only(left: 16, right: 16, top: 8, bottom: 8),
      child: Row(
        children: <Widget>[
          Expanded(
            child: Padding(
              padding: const EdgeInsets.only(right: 16, top: 8, bottom: 8),
              child: Container(
                decoration: BoxDecoration(
                  color: HotelAppTheme.buildLightTheme().backgroundColor,
                  borderRadius: const BorderRadius.all(
                    Radius.circular(38.0),
                  ),
                  boxShadow: <BoxShadow>[
                    BoxShadow(
                        color: Colors.grey.withOpacity(0.2),
                        offset: const Offset(0, 2),
                        blurRadius: 8.0),
                  ],
                ),
                child: Padding(
                  padding: const EdgeInsets.only(
                      left: 16, right: 16, top: 4, bottom: 4),
                  child: emailTextField,
                ),
              ),
            ),
          ),
          Container(
            decoration: BoxDecoration(
              color: HotelAppTheme.buildLightTheme().primaryColor,
              borderRadius: const BorderRadius.all(
                Radius.circular(38.0),
              ),
              boxShadow: <BoxShadow>[
                BoxShadow(
                    color: Colors.grey.withOpacity(0.4),
                    offset: const Offset(0, 2),
                    blurRadius: 8.0),
              ],
            ),
            /*child: Material(
              color: Colors.transparent,
              child: InkWell(
                borderRadius: const BorderRadius.all(
                  Radius.circular(32.0),
                ),
                onTap: () {
                  FocusScope.of(context).requestFocus(FocusNode());
                },
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Icon(FontAwesomeIcons.search,
                      size: 20,
                      color: HotelAppTheme.buildLightTheme().backgroundColor),
                ),
              ),
            ),*/
          ),
        ],
      ),
    );
  }

  Widget getPasswordUIBar(BuildContext context,ProfileFunctions profileFunctions,
      TextField emailTextField,
      TextField passwordTextField) {
    return Padding(
      padding: const EdgeInsets.only(left: 16, right: 16, top: 8, bottom: 8),
      child: Row(
        children: <Widget>[
          Expanded(
            child: Padding(
              padding: const EdgeInsets.only(right: 16, top: 8, bottom: 8),
              child: Container(
                decoration: BoxDecoration(
                  color: HotelAppTheme.buildLightTheme().backgroundColor,
                  borderRadius: const BorderRadius.all(
                    Radius.circular(38.0),
                  ),
                  boxShadow: <BoxShadow>[
                    BoxShadow(
                        color: Colors.grey.withOpacity(0.2),
                        offset: const Offset(0, 2),
                        blurRadius: 8.0),
                  ],
                ),
                child: Padding(
                  padding: const EdgeInsets.only(
                      left: 16, right: 16, top: 4, bottom: 4),
                  child: passwordTextField,
                ),
              ),
            ),
          ),
          Container(
            decoration: BoxDecoration(
              color: HotelAppTheme.buildLightTheme().primaryColor,
              borderRadius: const BorderRadius.all(
                Radius.circular(38.0),
              ),
              boxShadow: <BoxShadow>[
                BoxShadow(
                    color: Colors.grey.withOpacity(0.4),
                    offset: const Offset(0, 2),
                    blurRadius: 8.0),
              ],
            ),
            child: Material(
              color: Colors.transparent,
              child: InkWell(
                borderRadius: const BorderRadius.all(
                  Radius.circular(32.0),
                ),
                onTap: () {
                  FocusScope.of(context).requestFocus(FocusNode());
                  profileFunctions.showAlertDialog(context, this, emailTextField,
                      passwordTextField);
                },
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Icon(FontAwesomeIcons.surprise,
                      size: 20,
                      color: HotelAppTheme.buildLightTheme().backgroundColor),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}