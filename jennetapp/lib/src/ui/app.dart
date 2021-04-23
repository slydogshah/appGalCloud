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
  AnimationController animationController;

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

class LoginState extends State<Login> with TickerProviderStateMixin{
  String email;
  String password;
  bool authenticationFailed = false;
  Map<String,dynamic> authFailure;

  AnimationController animationController;
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    animationController = AnimationController(
        duration: const Duration(milliseconds: 1000), vsync: this);
    super.initState();
  }

  @override
  void dispose() {
    animationController.dispose();
    super.dispose();
  }

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
    /*final cursorColor = Theme.of(context).cursorColor;
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
    return scaffold;*/

    Color primaryColor = Color(0xFF383EDB);
    Color backgroundColor = Color(0xFF383EDB);
    return Theme(
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
                    getAppBarUI(context),
                    Expanded(
                      child: NestedScrollView(
                        controller: _scrollController,
                        headerSliverBuilder:
                            (BuildContext context, bool innerBoxIsScrolled) {
                          return <Widget>[
                            /*SliverList(
                              delegate: SliverChildBuilderDelegate(
                                      (BuildContext context, int index) {
                                    return Column(
                                      children: <Widget>[
                                        getSearchBarUI(),
                                        getTimeDateUI(),
                                      ],
                                    );
                                  }, childCount: 1),
                            ),*/
                            /*SliverPersistentHeader(
                              pinned: true,
                              floating: true,
                              delegate: ContestTabHeader(
                                getFilterBarUI(),
                              ),
                            ),*/
                          ];
                        },
                        body: Container(
                          color: primaryColor,
                          //color: Colors.pink,
                          child: ListView.builder(
                            itemCount: 1,
                            padding: const EdgeInsets.only(top: 8),
                            scrollDirection: Axis.vertical,
                            itemBuilder: (BuildContext context, int index) {
                              final int count = 1;
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
  }

  Widget getAppBarUI(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: HotelAppTheme.buildLightTheme().backgroundColor,
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
                  /*child: Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: Icon(Icons.arrow_back),
                  ),*/
                ),
              ),
            ),
            Expanded(
              child: Center(
                child: Text(
                  'Login',
                  style: TextStyle(
                    fontWeight: FontWeight.w600,
                    fontSize: 22,
                  ),
                ),
              ),
            ),
            Container(
              width: AppBar().preferredSize.height + 40,
              height: AppBar().preferredSize.height,
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.end,
                children: <Widget>[
                  Material(
                    color: Colors.transparent,
                    child: InkWell(
                      borderRadius: const BorderRadius.all(
                        Radius.circular(32.0),
                      ),
                      onTap: () => Navigator.push(context,MaterialPageRoute(builder: (context) => new Registration())),
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Icon(
                          Icons.person_add,
                          color: Theme.of(context).accentColor,
                        ),
                      ),
                    ),
                  ),
                  /*Material(
                    color: Colors.transparent,
                    child: InkWell(
                      borderRadius: const BorderRadius.all(
                        Radius.circular(32.0),
                      ),
                      onTap: () {},
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Icon(FontAwesomeIcons.mapMarkerAlt),
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
        this.callback})
      : super(key: key);

  final VoidCallback callback;
  final AnimationController animationController;
  final Animation<dynamic> animation;

  @override
  Widget build(BuildContext context) {
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
    ProfileFunctions profileFunctions = new ProfileFunctions();
    Widget password = this.getPasswordUIBar(context,profileFunctions,emailTextField,passwordTextField);

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
                    borderRadius: const BorderRadius.all(Radius.circular(16.0)),
                    boxShadow: <BoxShadow>[
                      BoxShadow(
                        color: Colors.grey.withOpacity(0.6),
                        offset: const Offset(4, 4),
                        blurRadius: 16,
                      ),
                    ],
                  ),
                  child: ClipRRect(
                    borderRadius: const BorderRadius.all(Radius.circular(16.0)),
                    child: Stack(
                      children: <Widget>[
                        Column(
                          children: <Widget>[
                            Container(
                              color: HotelAppTheme.buildLightTheme()
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
                                            /*Padding(
                                              padding:
                                              const EdgeInsets.only(top: 4),
                                              child: Row(
                                                crossAxisAlignment:
                                                CrossAxisAlignment.center,
                                                mainAxisAlignment:
                                                MainAxisAlignment.start,
                                                children: <Widget>[
                                                  ElevatedButton(
                                                    child: Text('Login'),
                                                    style: ElevatedButton.styleFrom(
                                                      //primary: Color(0xFF383EDB)
                                                        primary: Colors.pink
                                                    ),
                                                    onPressed: () {
                                                    },
                                                  ),
                                                ],
                                              ),
                                            ),*/
                                          ],
                                        ),
                                      ),
                                    ),
                                  ),
                                  /*Padding(
                                    padding: const EdgeInsets.only(
                                        right: 16, top: 8),
                                    child: Column(
                                      mainAxisAlignment:
                                      MainAxisAlignment.center,
                                      crossAxisAlignment:
                                      CrossAxisAlignment.end,
                                      children: <Widget>[
                                        ElevatedButton(
                                          child: Text('Login'),
                                          style: ElevatedButton.styleFrom(
                                            //primary: Color(0xFF383EDB)
                                              primary: Colors.pink
                                          ),
                                          onPressed: () {
                                          },
                                        ),
                                        ElevatedButton(
                                          child: Text('Register'),
                                          style: ElevatedButton.styleFrom(
                                            //primary: Color(0xFF383EDB)
                                              primary: Colors.pink
                                          ),
                                          onPressed: () {
                                          },
                                        ),
                                      ],
                                    ),
                                  ),*/
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
                                /*child: Icon(
                                  Icons.favorite_border,
                                  color: HotelAppTheme.buildLightTheme()
                                      .primaryColor,
                                ),*/
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

  void handleAccept(BuildContext context) {
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
          ),
        ],
      ),
    );
  }
}