import 'dart:convert';
import 'dart:typed_data';

import 'package:app/hotel_booking/hotel_app_theme.dart';
import 'package:app/src/messaging/polling/cloudDataPoller.dart';

import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'package:app/src/navigation/embeddedNavigation.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:flutter/services.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:smooth_star_rating/smooth_star_rating.dart';

import 'inProgress.dart';

class FoodRunnerApp extends StatelessWidget {
  Map<String,List<FoodRecoveryTransaction>> txs;
  FoodRunnerApp(Map<String,List<FoodRecoveryTransaction>> txs)
  {
    LocationUpdater.getLocation();
    this.txs = txs;
  }

  @override
  Widget build(BuildContext context) {
    Color primaryColor = Color(0xFF383EDB);
    Color backgroundColor = Color(0xFF383EDB);
    if(txs["inProgress"].length > 0) {
      MaterialApp materialApp = new MaterialApp(
          home: InProgressMainScene(txs),
          theme: ThemeData(
              primaryColor: primaryColor,
              backgroundColor: backgroundColor,
              accentColor: backgroundColor,
              accentColorBrightness: Brightness.dark
          )
      );
      return materialApp;
    }
    else{
      MaterialApp materialApp = new MaterialApp(
          home: FoodRunnerMainScene(txs),
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
}


class FoodRunnerMainScene extends StatefulWidget {
  List<FoodRecoveryTransaction> recoveryTxs;
  List<FoodRecoveryTransaction> inProgressTxs;
  Map<String,List<FoodRecoveryTransaction>> txs;

  FoodRunnerMainScene(Map<String,List<FoodRecoveryTransaction>> txs)
  {
    this.recoveryTxs = txs['pending'];
    this.inProgressTxs = txs['inProgress'];
    this.txs = txs;
  }

  @override
  _FoodRunnerMainState createState() => _FoodRunnerMainState(this.txs,this.recoveryTxs,this.inProgressTxs);
}

class _FoodRunnerMainState extends State<FoodRunnerMainScene> with TickerProviderStateMixin {

  AnimationController animationController;
  List<FoodRecoveryTransaction> recoveryTxs;
  List<FoodRecoveryTransaction> inProgressTxs;
  Map<String,List<FoodRecoveryTransaction>> txs;

  final ScrollController _scrollController = ScrollController();

  DateTime startDate = DateTime.now();
  DateTime endDate = DateTime.now().add(const Duration(days: 5));

  _FoodRunnerMainState(Map<String,List<FoodRecoveryTransaction>> txs,List<FoodRecoveryTransaction> recoveryTxs,List<FoodRecoveryTransaction> inProgressTxs) {
    this.recoveryTxs = recoveryTxs;
    this.inProgressTxs = inProgressTxs;
    this.txs = txs;
  }

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

  /*@override
  void didChangeDependencies() {
    super.didChangeDependencies();
  }*/

  @override
  Widget build(BuildContext context) {
    Color primaryColor = Color(0xFF383EDB);
    Color backgroundColor = Color(0xFF383EDB);
    Profile profile = ActiveSession.getInstance().getProfile();
    CloudDataPoller.startPolling(context,profile);
    LocationUpdater.startPolling(profile);
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
                          ];
                        },
                        body: Container(
                          color: primaryColor,
                          //color: Colors.pink,
                          child: getPickUpList(),
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

  Widget getPickUpList()
  {
    Widget widget = ListView.builder(
      itemCount: recoveryTxs.length,
      padding: const EdgeInsets.only(top: 8),
      scrollDirection: Axis.vertical,
      itemBuilder: (BuildContext context, int index) {
        final int count =
        recoveryTxs.length > 4 ? 4 : recoveryTxs.length;
        final Animation<double> animation =
        Tween<double>(begin: 0.0, end: 1.0).animate(
            CurvedAnimation(
                parent: animationController,
                curve: Interval(
                    (1 / count) * index, 1.0,
                    curve: Curves.fastOutSlowIn)));
        animationController.forward();
        return PickUpListView(
          animation: animation,
          animationController: animationController,
          tx: this.recoveryTxs[index],
          txs: this.recoveryTxs,
        );
      },
    ).build(context);
    return widget;
  }

  Widget getAppBarUI(BuildContext context) {
    Icon icon = new Icon(Icons.thumb_up_rounded);
    Icon inProgress = new Icon(Icons.view_list);
    Color offline = Colors.white;
    FoodRunner foodRunner = ActiveSession.getInstance().foodRunner;
    if(foodRunner.offlineCommunitySupport){
      offline = Colors.green;
    }
    Color progress = Colors.white;
    if(this.inProgressTxs.length > 0) {
      progress = Colors.red;
    }
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
                  },
                ),
              ),
            ),
            Expanded(
              child: Center(
                child: Text(
                  'Requests',
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
                  Tooltip(
                    message: "DropOffs In Progress",
                    child: Material(
                      color: progress,
                      child: InkWell(
                        borderRadius: const BorderRadius.all(
                          Radius.circular(32.0),
                        ),
                        onTap: () {
                          if(this.inProgressTxs.length > 0) {
                            Navigator.push(context, MaterialPageRoute(
                                builder: (context) =>
                                    InProgressMainScene(this.txs)));
                          }
                        },
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: inProgress,
                        ),
                      ),
                    ),
                  ),
                  Tooltip(
                    message: "Notify Availability",
                    child: Material(
                      color: offline,
                      child: InkWell(
                        borderRadius: const BorderRadius.all(
                          Radius.circular(32.0),
                        ),
                        onTap: () {
                          if(foodRunner.offlineCommunitySupport){
                            foodRunner.offlineCommunitySupport = false;
                          }
                          else{
                            foodRunner.offlineCommunitySupport = true;
                          }
                          Profile profile = ActiveSession.getInstance().getProfile();
                          // set up the SimpleDialog
                          SimpleDialog dialog = SimpleDialog(
                              children: [CupertinoActivityIndicator()]
                          );

                          // show the dialog
                          showDialog(
                            context: this.context,
                            builder: (BuildContext context) {
                              return dialog;
                            },
                          );
                          ActiveNetworkRestClient activeNetworkClient = new ActiveNetworkRestClient();
                          Future<String> response = activeNetworkClient.notifyOfflineAvailability(profile.email);
                          response.then((response){
                            Navigator.of(context, rootNavigator: true).pop();
                            setState(() {
                              if(foodRunner.offlineCommunitySupport){
                                offline = Colors.green;
                              }
                              else{
                                offline = Colors.white;
                              }
                            });
                          });
                        },
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: icon,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            )
          ],
        ),
      ),
    );
  }

  Future<bool> getData() async {
    await Future<dynamic>.delayed(const Duration(milliseconds: 200));
    return true;
  }
}

class PickUpListView extends StatelessWidget {
  const PickUpListView(
      {Key key,
        this.animationController,
        this.animation,
        this.tx,
        this.txs,
      })
      : super(key: key);

  final AnimationController animationController;
  final Animation<dynamic> animation;
  final FoodRecoveryTransaction tx;
  final List<FoodRecoveryTransaction> txs;


  @override
  Widget build(BuildContext context) {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"tx/recovery/transaction/foodPic/?id="+tx.getId();
    Widget dropoff = null;
    if(tx.getPickupNotification().getDropOffOrg() != null)
    {
      dropoff = Text(
        tx.getPickupNotification().getDropOffOrg().orgName,
        textAlign: TextAlign.left,
        style: TextStyle(
          fontWeight: FontWeight.w600,
          fontSize: 22,
        ),
      );
    }
    else
    {
      dropoff = Tooltip(
          message: "Offline Community Support",
          child: Text(
            "Community",
            textAlign: TextAlign.left,
            style: TextStyle(
              fontWeight: FontWeight.w600,
              fontSize: 22,
            ),
          )
      );
    }
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
                            AspectRatio(
                              aspectRatio: 2,
                              child:
                              Image.network(remoteUrl),
                            ),
                            Padding(
                              padding: const EdgeInsets.only(
                                  right: 16, top: 8),
                              child: Column(
                                mainAxisAlignment:
                                MainAxisAlignment.center,
                                crossAxisAlignment:
                                CrossAxisAlignment.end,
                                children: <Widget>[
                                  ElevatedButton(
                                    child: Text("Accept"),
                                    style: ElevatedButton.styleFrom(
                                      //primary: Color(0xFF383EDB)
                                        primary: Colors.pink
                                    ),
                                    onPressed: () {
                                      Profile profile = ActiveSession.getInstance().getProfile();
                                      handleAccept(context,profile.email,
                                          tx);
                                    },
                                  ),
                                ],
                              ),
                            ),
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
                                            Text(
                                              tx.getPickupNotification().getSourceOrg().orgName,
                                              textAlign: TextAlign.left,
                                              style: TextStyle(
                                                fontWeight: FontWeight.w600,
                                                fontSize: 22,
                                              ),
                                            ),
                                            Row(
                                              crossAxisAlignment:
                                              CrossAxisAlignment.center,
                                              mainAxisAlignment:
                                              MainAxisAlignment.start,
                                              children: <Widget>[
                                                Text(
                                                  "Pickup: 10 minutes",
                                                  style: TextStyle(
                                                      fontSize: 14,
                                                      color: Colors.grey
                                                          .withOpacity(0.8)),
                                                ),
                                                const SizedBox(
                                                  width: 4,
                                                ),
                                                Icon(
                                                  FontAwesomeIcons.mapMarkerAlt,
                                                  size: 12,
                                                  color: HotelAppTheme
                                                      .buildLightTheme()
                                                      .primaryColor,
                                                ),
                                              ],
                                            ),
                                            Padding(
                                              padding:
                                              const EdgeInsets.only(top: 4),
                                              child: Row(
                                                children: <Widget>[
                                                ],
                                              ),
                                            ),
                                          ],
                                        ),
                                      ),
                                    ),
                                  ),
                                  Padding(
                                    padding: const EdgeInsets.only(
                                        right: 16, top: 8),
                                    child: Column(
                                      mainAxisAlignment:
                                      MainAxisAlignment.center,
                                      crossAxisAlignment:
                                      CrossAxisAlignment.end,
                                      children: <Widget>[
                                        dropoff,
                                        Text(
                                          'DropOff: 15 minutes',
                                          style: TextStyle(
                                              fontSize: 14,
                                              color:
                                              Colors.grey.withOpacity(0.8)),
                                        ),
                                      ],
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
                                child: Icon(
                                  Icons.favorite_border,
                                  color: HotelAppTheme.buildLightTheme()
                                      .primaryColor,
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
            ),
          ),
        );
      },
    );
  }

  void handleAccept(BuildContext context,String email, FoodRecoveryTransaction tx) {
    SourceOrg dropOffOrg = tx.getPickupNotification().getDropOffOrg();
    AlertDialog dialog = AlertDialog(
      title: Text('Accept Food Pickup and DropOff'),
      content: Text(
        dropOffOrg.orgName+": "+dropOffOrg.street+","+dropOffOrg.zip,
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
          child: Text('CANCEL'),
        ),
        FlatButton(
          textColor: Color(0xFF6200EE),
          onPressed: () {
            Navigator.pop(context);
            FocusScope.of(context).requestFocus(FocusNode());

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

            ActiveNetworkRestClient client = new ActiveNetworkRestClient();
            Future<String> future = client.accept(email, tx);
            future.then((fuckyou) {
              Navigator.of(context, rootNavigator: true).pop();


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
              ActiveNetworkRestClient client = new ActiveNetworkRestClient();
              Future<Map<String,List<FoodRecoveryTransaction>>> future = client
                  .getFoodRecoveryTransaction(email);
              future.then((txs) {
                Navigator.of(context, rootNavigator: true).pop();
                Navigator.push(context, MaterialPageRoute(
                    builder: (context) => InProgressMainScene(txs)));
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
            });
          },
          child: Text('ACCEPT without NAVIGATION'),
        ),
        FlatButton(
          textColor: Color(0xFF6200EE),
          onPressed: () {
            Navigator.pop(context);
            FocusScope.of(context).requestFocus(FocusNode());
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
            ActiveNetworkRestClient client = new ActiveNetworkRestClient();
            Future<String> future = client.accept(email, tx);
            //donot rename this variable. It is symbolic
            future.then((fuckyou) {
              Navigator.of(context, rootNavigator: true).pop();


              Future<FoodRunnerLocation> locationFuture = LocationUpdater.getLocation();
              locationFuture.then((foodRunnerLocation){
                //print("**********");
                //print(foodRunnerLocation);
                EmbeddedNavigation embeddedNavigation = new EmbeddedNavigation(context,
                    tx.getPickupNotification().getSourceOrg(),foodRunnerLocation);
                embeddedNavigation.start(tx);
              });
            });
          },
          child: Text('ACCEPT with NAVIGATION'),
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
  }
}


