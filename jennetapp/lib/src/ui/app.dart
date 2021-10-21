import 'package:app/hotel_booking/filters_screen.dart';
import 'package:app/hotel_booking/hotel_app_theme.dart';
import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/ui/registration.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/rendering.dart';

import 'profileFunctions.dart';

class JenNetworkApp extends StatelessWidget {
  AnimationController animationController;

  JenNetworkApp()
  {
    //LocationUpdater.getLocation();
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
}

class LoginState extends State<Login> with TickerProviderStateMixin{
  String email;
  String password;
  Map<String,dynamic> authFailure;

  String emailRejectedMessage;
  String passwordRejectedMessage;
  String authRejectedMessage;
  String systemErrorMessage;

  AnimationController animationController;
  final ScrollController _scrollController = ScrollController();
  String title;
  Text titleWidget;
  TextEditingController emailController;
  TextEditingController passwordController;

  @override
  void initState() {
    animationController = AnimationController(
        duration: const Duration(milliseconds: 1000), vsync: this);
    this.emailController = new TextEditingController();
    this.passwordController = new TextEditingController();
    super.initState();
  }

  @override
  void dispose() {
    animationController.dispose();
    super.dispose();
  }

  void notifyAuthFailed(String authRejectedMessage)
  {
    setState(() {
      // This call to setState tells the Flutter framework that
      // something has changed in this State, which causes it to rerun
      // the build method below so that the display can reflect the
      // updated values. If you change _counter without calling
      // setState(), then the build method won't be called again,
      // and so nothing would appear to happen.
      this.systemErrorMessage = null;
      this.emailRejectedMessage = null;
      this.passwordRejectedMessage = null;
      this.authRejectedMessage = authRejectedMessage;
    });
  }

  void notifySystemError(String systemErrorMessage)
  {
    setState(() {
      // This call to setState tells the Flutter framework that
      // something has changed in this State, which causes it to rerun
      // the build method below so that the display can reflect the
      // updated values. If you change _counter without calling
      // setState(), then the build method won't be called again,
      // and so nothing would appear to happen.
      this.systemErrorMessage = systemErrorMessage;
      this.emailRejectedMessage = null;
      this.passwordRejectedMessage = null;
      this.authRejectedMessage = null;
    });
  }

  void notifyValidationFailure(String emailRejectedMessage,String passwordRejectedMessage) {
    setState(() {
      // This call to setState tells the Flutter framework that
      // something has changed in this State, which causes it to rerun
      // the build method below so that the display can reflect the
      // updated values. If you change _counter without calling
      // setState(), then the build method won't be called again,
      // and so nothing would appear to happen.
      this.systemErrorMessage = null;
      this.emailRejectedMessage = emailRejectedMessage;
      this.passwordRejectedMessage = passwordRejectedMessage;
      this.authRejectedMessage = authRejectedMessage;
    });
  }

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    //print(0);
    this.title = "#Jen Network";
    this.titleWidget = Text(
        this.title,
        style: TextStyle(
          fontWeight: FontWeight.w600,
          fontSize: 22,)
    );

    TextFormField emailTextField = TextFormField(
      controller: this.emailController,
      autovalidateMode:AutovalidateMode.always,
      validator: (value) {
        return this.emailRejectedMessage;
      },
      onChanged: (String txt) {},
      style: const TextStyle(
        fontSize: 18,
      ),
      cursorColor: HotelAppTheme
          .buildLightTheme()
          .primaryColor,
      decoration: InputDecoration(
        border: InputBorder.none,
        hintText: 'Email...',
      ),
    );

    TextFormField passwordTextField = TextFormField(
      controller: this.passwordController,
      autovalidateMode:AutovalidateMode.always,
      validator: (value) {
        if(this.passwordRejectedMessage != null)
        {
          return this.passwordRejectedMessage;
        }
        else if(this.authRejectedMessage != null)
        {
          return this.authRejectedMessage;
        }
        else if(this.systemErrorMessage != null)
        {
          return this.systemErrorMessage;
        }
        return null;
      },
      onChanged: (String txt) {},
      obscureText: true,
      style: const TextStyle(
        fontSize: 18,
      ),
      cursorColor: HotelAppTheme
          .buildLightTheme()
          .primaryColor,
      decoration: InputDecoration(
        border: InputBorder.none,
        hintText: 'Password...',
      ),
    );

    Color primaryColor = Color(0xFF383EDB);
    Color backgroundColor = Color(0xFF383EDB);
    Theme theme = Theme(
      data: HotelAppTheme.buildLightTheme(),
      child: Container(
        child: Scaffold(
          body: Stack(
            children: <Widget>[
              InkWell(
                splashColor: Colors.transparent,
                focusColor: Colors.transparent,
                highlightColor: Colors.transparent,
                hoverColor: Colors.transparent,
                onTap: () {
                  FocusScope.of(context).requestFocus(FocusNode());
                },
                child: Column(
                  children: <Widget>[
                    getAppBarUI(context, titleWidget),
                    Expanded(
                      child: NestedScrollView(
                        controller: _scrollController,
                        headerSliverBuilder:
                            (BuildContext context, bool innerBoxIsScrolled) {
                          return <Widget>[
                          ];
                        },
                        body: Container(
                          color: primaryColor,
                          //color: Colors.pink,
                          child: ListView.builder(
                            itemCount: 3,
                            padding: const EdgeInsets.only(top: 8),
                            scrollDirection: Axis.vertical,
                            itemBuilder: (BuildContext context, int index) {
                              final int count = 3;
                              final Animation<double> animation =
                              Tween<double>(begin: 0.0, end: 1.0).animate(
                                  CurvedAnimation(
                                      parent: animationController,
                                      curve: Interval(
                                          (1 / count) * index, 1.0,
                                          curve: Curves.fastOutSlowIn)));
                              animationController.forward();
                              return LoginView(
                                callback: () {},
                                animation: animation,
                                index: index,
                                loginState: this,
                                emailTextField: /*TextFormField(
                                  controller: this.emailController,
                                  autovalidateMode:AutovalidateMode.always,
                                  validator: (value) {
                                    return this.emailRejectedMessage;
                                  },
                                  onChanged: (String txt) {},
                                  style: const TextStyle(
                                    fontSize: 18,
                                  ),
                                  cursorColor: HotelAppTheme
                                      .buildLightTheme()
                                      .primaryColor,
                                  decoration: InputDecoration(
                                    border: InputBorder.none,
                                    hintText: 'Email...',
                                  ),
                                ),*/
                                emailTextField,
                                passwordTextField: /*TextFormField(
                                  // The validator receives the text that the user has entered.
                                  validator: (value) {
                                    if (value == null || value.isEmpty) {
                                      return 'Please enter some text';
                                    }
                                    return null;
                                  },
                                ),*/
                                passwordTextField,
                                animationController: animationController,
                              );
                            },
                          ),
                        ),
                      ),
                    )
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );

    return theme;
  }

  Widget getAppBarUI(BuildContext context, Text titleWidget) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.blueGrey,
        boxShadow: <BoxShadow>[
          BoxShadow(
              color: Colors.grey.withOpacity(0.2),
              offset: const Offset(0, 2),
              blurRadius: 8.0),
        ],
      ),
      child: Padding(
        padding: EdgeInsets.only(
            top: MediaQuery.of(context).padding.top, left: 8, right: 8),
        child: Row(
          children: <Widget>[
            Container(
              alignment: Alignment.centerLeft,
              width: AppBar().preferredSize.height + 40,
              height: AppBar().preferredSize.height,
              child: Material(
                color: Colors.transparent,
                child: InkWell(
                  borderRadius: const BorderRadius.all(
                    Radius.circular(32.0),
                  ),
                  onTap: () {
                    //Navigator.pop(context);
                  },
                ),
              ),
            ),
            Expanded(
              child: Center(
                child: titleWidget,
              ),
            ),
            Container(
              width: AppBar().preferredSize.height + 40,
              height: AppBar().preferredSize.height,
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.end,
                children: <Widget>[
                  /*Material(
                    color: Colors.transparent,
                    child: InkWell(
                      borderRadius: const BorderRadius.all(
                        Radius.circular(32.0),
                      ),
                      onTap: () {
                        Navigator.push(context,MaterialPageRoute(builder: (context) => new Registration()));
                      },
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Icon(
                          Icons.person_add,
                          color: Theme.of(context).accentColor,
                        ),
                      ),
                    ),
                  ),*/
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}

class LoginView extends StatelessWidget {
  const LoginView(
      {Key key,
        this.animationController,
        this.animation,
        this.index,
        this.loginState,
        this.emailTextField,
        this.passwordTextField,
        this.callback})
      : super(key: key);

  final VoidCallback callback;
  final AnimationController animationController;
  final Animation<dynamic> animation;
  final int index;
  final LoginState loginState;
  final TextFormField emailTextField;
  final TextFormField passwordTextField;

  @override
  Widget build(BuildContext context) {
    Widget email = this.getLoginUIBar(context, emailTextField);

    ProfileFunctions profileFunctions = new ProfileFunctions();
    Widget password = this.getPasswordUIBar(
        context, profileFunctions, emailTextField, passwordTextField);

    if(index == 0)
    {
      return AnimatedBuilder(
        animation: animationController,
        builder: (BuildContext context, Widget child) {
          return FadeTransition(
            opacity: animation,
            child: Transform(
              transform: Matrix4.translationValues(
                  0.0, 50 * (1.0 - animation.value), 0.0),
              child: Padding(
                padding: const EdgeInsets.only(
                    left: 24, right: 24, top: 8, bottom: 16),
                child: InkWell(
                  splashColor: Colors.transparent,
                  onTap: () {
                    callback();
                  },
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: const BorderRadius.all(
                          Radius.circular(16.0)),
                      boxShadow: <BoxShadow>[
                        BoxShadow(
                          color: Colors.grey.withOpacity(0.6),
                          offset: const Offset(4, 4),
                          blurRadius: 16,
                        ),
                      ],
                    ),
                    child: ClipRRect(
                      borderRadius: const BorderRadius.all(
                          Radius.circular(16.0)),
                      child: Stack(
                        children: <Widget>[
                          Column(
                            children: <Widget>[
                              Container(
                                color: HotelAppTheme
                                    .buildLightTheme()
                                    .backgroundColor,
                                child: Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: <Widget>[
                                    Expanded(
                                      child: Container(
                                        child: Padding(
                                          padding: const EdgeInsets.only(
                                              left: 16, top: 8, bottom: 8),
                                          child: Column(
                                            mainAxisAlignment:
                                            MainAxisAlignment.center,
                                            crossAxisAlignment:
                                            CrossAxisAlignment.start,
                                            children: <Widget>[
                                              email,
                                              password,
                                            ],
                                          ),
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                          Positioned(
                            top: 8,
                            right: 8,
                            child: Material(
                              color: Colors.transparent,
                              child: InkWell(
                                borderRadius: const BorderRadius.all(
                                  Radius.circular(32.0),
                                ),
                                onTap: () {},
                                child: Padding(
                                  padding: const EdgeInsets.all(8.0),
                                ),
                              ),
                            ),
                          )
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
          );
        },
      );
    }
    else if(index == 1)
    {
      return AnimatedBuilder(
        animation: animationController,
        builder: (BuildContext context, Widget child) {
          return FadeTransition(
            opacity: animation,
            child: Transform(
              transform: Matrix4.translationValues(
                  0.0, 50 * (1.0 - animation.value), 0.0),
              child: Padding(
                padding: const EdgeInsets.only(
                    left: 24, right: 24, top: 8, bottom: 16),
                child: InkWell(
                  splashColor: Colors.transparent,
                  onTap: () {
                    callback();
                  },
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: const BorderRadius.all(
                          Radius.circular(16.0)),
                      boxShadow: <BoxShadow>[
                        BoxShadow(
                          color: Colors.grey.withOpacity(0.6),
                          offset: const Offset(4, 4),
                          blurRadius: 16,
                        ),
                      ],
                    ),
                    child: ClipRRect(
                      borderRadius: const BorderRadius.all(
                          Radius.circular(16.0)),
                      child: Stack(
                        children: <Widget>[
                          Column(
                            children: <Widget>[
                              Container(
                                color: HotelAppTheme
                                    .buildLightTheme()
                                    .backgroundColor,
                                child: Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: <Widget>[
                                    Expanded(
                                      child: Container(
                                        child: Padding(
                                          padding: const EdgeInsets.only(
                                              left: 16, top: 8, bottom: 8),
                                          child: Column(
                                            mainAxisAlignment:
                                            MainAxisAlignment.center,
                                            crossAxisAlignment:
                                            CrossAxisAlignment.center,
                                            children: <Widget>[
                                              Padding(
                                                padding: const EdgeInsets.only(
                                                    left: 16, right: 16, bottom: 16, top: 8),
                                                child: Container(
                                                  height: 48,
                                                  decoration: BoxDecoration(
                                                    color: Colors.pink,
                                                    borderRadius: const BorderRadius.all(Radius.circular(24.0)),
                                                    boxShadow: <BoxShadow>[
                                                      BoxShadow(
                                                        color: Colors.grey.withOpacity(0.6),
                                                        blurRadius: 8,
                                                        offset: const Offset(4, 4),
                                                      ),
                                                    ],
                                                  ),
                                                  child: Material(
                                                    color: Colors.transparent,
                                                    child: InkWell(
                                                      borderRadius: const BorderRadius.all(Radius.circular(24.0)),
                                                      highlightColor: Colors.transparent,
                                                      onTap: () {
                                                        FocusScope.of(context).requestFocus(FocusNode());
                                                        profileFunctions.showAlertDialog(context, this,this.loginState, emailTextField,
                                                            passwordTextField);
                                                      },
                                                      child: Center(
                                                        child: Text(
                                                          'Login',
                                                          style: TextStyle(
                                                              fontWeight: FontWeight.w500,
                                                              fontSize: 18,
                                                              color: Colors.white),
                                                        ),
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              )
                                            ],
                                          ),
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                          Positioned(
                            top: 8,
                            right: 8,
                            child: Material(
                              color: Colors.transparent,
                              child: InkWell(
                                borderRadius: const BorderRadius.all(
                                  Radius.circular(32.0),
                                ),
                                onTap: () {},
                                child: Padding(
                                  padding: const EdgeInsets.all(8.0),
                                ),
                              ),
                            ),
                          )
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
          );
        },
      );
    }
    else
    {
      return AnimatedBuilder(
        animation: animationController,
        builder: (BuildContext context, Widget child) {
          return FadeTransition(
            opacity: animation,
            child: Transform(
              transform: Matrix4.translationValues(
                  0.0, 50 * (1.0 - animation.value), 0.0),
              child: Padding(
                padding: const EdgeInsets.only(
                    left: 24, right: 24, top: 8, bottom: 16),
                child: InkWell(
                  splashColor: Colors.transparent,
                  onTap: () {
                    callback();
                  },
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: const BorderRadius.all(
                          Radius.circular(16.0)),
                      boxShadow: <BoxShadow>[
                        BoxShadow(
                          color: Colors.grey.withOpacity(0.6),
                          offset: const Offset(4, 4),
                          blurRadius: 16,
                        ),
                      ],
                    ),
                    child: ClipRRect(
                      borderRadius: const BorderRadius.all(
                          Radius.circular(16.0)),
                      child: Stack(
                        children: <Widget>[
                          Column(
                            children: <Widget>[
                              Container(
                                color: HotelAppTheme
                                    .buildLightTheme()
                                    .backgroundColor,
                                child: Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: <Widget>[
                                    Expanded(
                                      child: Container(
                                        child: Padding(
                                          padding: const EdgeInsets.only(
                                              left: 16, top: 8, bottom: 8),
                                          child: Column(
                                            mainAxisAlignment:
                                            MainAxisAlignment.center,
                                            crossAxisAlignment:
                                            CrossAxisAlignment.center,
                                            children: <Widget>[
                                              Padding(
                                                padding: const EdgeInsets.only(
                                                    left: 16, right: 16, bottom: 16, top: 8),
                                                child: Container(
                                                  height: 48,
                                                  decoration: BoxDecoration(
                                                    color: Colors.pink,
                                                    borderRadius: const BorderRadius.all(Radius.circular(24.0)),
                                                    boxShadow: <BoxShadow>[
                                                      BoxShadow(
                                                        color: Colors.grey.withOpacity(0.6),
                                                        blurRadius: 8,
                                                        offset: const Offset(4, 4),
                                                      ),
                                                    ],
                                                  ),
                                                  child: Material(
                                                    color: Colors.transparent,
                                                    child: InkWell(
                                                      borderRadius: const BorderRadius.all(Radius.circular(24.0)),
                                                      highlightColor: Colors.transparent,
                                                      onTap: () {
                                                        AlertDialog dialog = AlertDialog(
                                                          title: Text('Create an account?'),
                                                          content: Text('Thank you for giving your time to Hunger. We hope the Community helps you in your time of need, like you are helping now.\n\n-#Jen Network'),
                                                          actions: [
                                                            FlatButton(
                                                              textColor: Color(0xFF6200EE),
                                                              onPressed: () {
                                                                Navigator.of(context,rootNavigator: true).pop();
                                                              },
                                                              child: Text('CANCEL'),
                                                            ),
                                                            FlatButton(
                                                              textColor: Color(0xFF6200EE),
                                                              onPressed: () {
                                                                Navigator.of(context,rootNavigator: true).pop();
                                                                Navigator.push(context, MaterialPageRoute(
                                                                    builder: (context) => Registration()));
                                                              },
                                                              child: Text('ACCEPT'),
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
                                                      },
                                                      child: Center(
                                                        child: Text(
                                                          'Join',
                                                          style: TextStyle(
                                                              fontWeight: FontWeight.w500,
                                                              fontSize: 18,
                                                              color: Colors.white),
                                                        ),
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              )
                                            ],
                                          ),
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                          Positioned(
                            top: 8,
                            right: 8,
                            child: Material(
                              color: Colors.transparent,
                              child: InkWell(
                                borderRadius: const BorderRadius.all(
                                  Radius.circular(32.0),
                                ),
                                onTap: () {},
                                child: Padding(
                                  padding: const EdgeInsets.all(8.0),
                                ),
                              ),
                            ),
                          )
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),
          );
        },
      );
    }
  }

  Widget getLoginUIBar(BuildContext context, TextFormField emailTextField) {
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
          ),
        ],
      ),
    );
  }

  Widget getPasswordUIBar(BuildContext context,ProfileFunctions profileFunctions,
      TextFormField emailTextField,
      TextFormField passwordTextField) {
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
          /*Container(
            decoration: BoxDecoration(
              //color: HotelAppTheme.buildLightTheme().primaryColor,
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
            child:
              ElevatedButton(
                child: Text('Login'),
                style: ElevatedButton.styleFrom(
                  //primary: Color(0xFF383EDB)
                    primary: Colors.pink
                ),
                onPressed: () {
                  FocusScope.of(context).requestFocus(FocusNode());
                  profileFunctions.showAlertDialog(context, this, emailTextField,
                      passwordTextField);
                },
              ),
          ),*/
          /*Container(
            decoration: BoxDecoration(
              //color: HotelAppTheme.buildLightTheme().primaryColor,
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
            child:
            ElevatedButton(
              child: Text('Register'),
              style: ElevatedButton.styleFrom(
                //primary: Color(0xFF383EDB)
                  primary: Colors.pink
              ),
              onPressed: () {
                FocusScope.of(context).requestFocus(FocusNode());
                //profileFunctions.showAlertDialogRegister(context, this, emailTextField,
                //    passwordTextField,"FOOD_RUNNER");
                FiltersScreen filterScreen = new FiltersScreen();
                filterScreen.setLoginState(this);
                filterScreen.setEmailField(emailTextField);
                filterScreen.setPasswordField(passwordTextField);
                Navigator.push<dynamic>(
                  context,
                  MaterialPageRoute<dynamic>(
                      builder: (BuildContext context) => filterScreen,
                      fullscreenDialog: true),
                );

                AlertDialog dialog = AlertDialog(
                  title: Text('Reset settings?'),
                  content: Text('This will reset your device to its default factory settings.'),
                  actions: [
                    FlatButton(
                      textColor: Color(0xFF6200EE),
                      onPressed: () {},
                      child: Text('CANCEL'),
                    ),
                    FlatButton(
                      textColor: Color(0xFF6200EE),
                      onPressed: () {
                        FocusScope.of(context).requestFocus(FocusNode());
                        profileFunctions.showAlertDialogRegister(context, this, emailTextField,
                        passwordTextField,"FOOD_RUNNER");
                      },
                      child: Text('ACCEPT'),
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
              },
            ),
          ),*/
        ],
      ),
    );
  }
}