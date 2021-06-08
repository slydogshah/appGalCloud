import 'package:app/hotel_booking/hotel_app_theme.dart';
import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/navigation/embeddedNavigation.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

import 'foodRunner.dart';

class InProgressMainScene extends StatefulWidget {
  List<FoodRecoveryTransaction> recoveryTxs;
  List<FoodRecoveryTransaction> inProgressTxs;
  Map<String,List<FoodRecoveryTransaction>> txs;

  InProgressMainScene(Map<String,List<FoodRecoveryTransaction>> txs)
  {
    this.txs = txs;
    this.recoveryTxs = txs['pending'];
    this.inProgressTxs = txs['inProgress'];
  }

  @override
  _InProgressMainState createState() => _InProgressMainState(this.txs,this.recoveryTxs,this.inProgressTxs);
}

class _InProgressMainState extends State<InProgressMainScene> with TickerProviderStateMixin {
  List<FoodRecoveryTransaction> recoveryTxs;
  List<FoodRecoveryTransaction> inProgressTxs;
  Map<String,List<FoodRecoveryTransaction>> txs;

  AnimationController animationController;

  final ScrollController _scrollController = ScrollController();

  DateTime startDate = DateTime.now();
  DateTime endDate = DateTime.now().add(const Duration(days: 5));

  _InProgressMainState(Map<String,List<FoodRecoveryTransaction>> txs,List<FoodRecoveryTransaction> recoveryTxs,List<FoodRecoveryTransaction> inProgressTxs) {
    this.txs = txs;
    this.recoveryTxs = recoveryTxs;
    this.inProgressTxs = inProgressTxs;
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

  @override
  Widget build(BuildContext context) {
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
                          ];
                        },
                        body: Container(
                          color: primaryColor,
                          //color: Colors.pink,
                          child: getInProgressList(),
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

  Widget getInProgressList()
  {
    Widget widget = ListView.builder(
      itemCount: inProgressTxs.length,
      padding: const EdgeInsets.only(top: 8),
      scrollDirection: Axis.vertical,
      itemBuilder: (BuildContext context, int index) {
        final int count =
        inProgressTxs.length > 4 ? 4 : inProgressTxs.length;
        final Animation<double> animation =
        Tween<double>(begin: 0.0, end: 1.0).animate(
            CurvedAnimation(
                parent: animationController,
                curve: Interval(
                    (1 / count) * index, 1.0,
                    curve: Curves.fastOutSlowIn)));
        animationController.forward();
        return InProgressListView(
          animation: animation,
          animationController: animationController,
          tx: this.inProgressTxs[index],
          txs: this.inProgressTxs,
        );
      },
    ).build(context);
    return widget;
  }

  Widget getAppBarUI(BuildContext context) {
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
                    //  Navigator.pop(context);
                  },
                  /*child: Padding(
                  //  padding: const EdgeInsets.all(8.0),
                  //  child: Icon(Icons.arrow_back),
                  ),*/
                ),
              ),
            ),
            Expanded(
              child: Center(
                child: Text(
                  'In-Progress',
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
                  /*Material(
                    color: Colors.transparent,
                    child: InkWell(
                      borderRadius: const BorderRadius.all(
                        Radius.circular(32.0),
                      ),
                      onTap: () {},
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Icon(Icons.favorite_border),
                      ),
                    ),
                  ),*/
                  Material(
                    color: Colors.transparent,
                    child: InkWell(
                      borderRadius: const BorderRadius.all(
                        Radius.circular(32.0),
                      ),
                      onTap: () {
                        Navigator.push(context, MaterialPageRoute(
                            builder: (context) => FoodRunnerMainScene(this.txs)));
                      },
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Icon(FontAwesomeIcons.mapMarkerAlt),
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
}

class InProgressListView extends StatelessWidget {
  const InProgressListView({Key key,
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
    String remoteUrl = UrlFunctions.getInstance().resolveHost() +
        "tx/recovery/transaction/foodPic/?id=" + tx.getId();
    Widget buttons = null;
    Text dropOffOrgName = null;
    if(tx.getPickupNotification().getDropOffOrg() != null)
    {
      buttons = this.getButton(context, tx);
      dropOffOrgName = Text(
        tx
            .getPickupNotification()
            .getDropOffOrg()
            .orgName,
        textAlign: TextAlign.left,
        style: TextStyle(
          fontWeight: FontWeight.w600,
          fontSize: 22,
        ),
      );
    }
    else
    {
      buttons = this.getButton(context, tx);
      dropOffOrgName = Text(
        "Community",
        textAlign: TextAlign.left,
        style: TextStyle(
          fontWeight: FontWeight.w600,
          fontSize: 22,
        ),
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
                onTap: () {},
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
                            buttons,
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
                                            Text(
                                              tx
                                                  .getPickupNotification()
                                                  .getSourceOrg()
                                                  .orgName,
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
                                        dropOffOrgName,
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
                                  color: HotelAppTheme
                                      .buildLightTheme()
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

  Widget getButton(BuildContext context,FoodRecoveryTransaction tx)
  {
    Widget padding = null;
    if(tx.getPickupNotification().getDropOffOrg() != null) {
      padding = Padding(
        padding: const EdgeInsets.only(
            right: 16, top: 8),
        child: Column(
          mainAxisAlignment:
          MainAxisAlignment.center,
          crossAxisAlignment:
          CrossAxisAlignment.end,
          children: <Widget>[
            ElevatedButton(
              child: Text('DropOff'),
              style: ElevatedButton.styleFrom(
                //primary: Color(0xFF383EDB)
                  primary: Colors.pink
              ),
              onPressed: () {
                Profile profile = ActiveSession
                    .getInstance().getProfile();
                handleDropOff(context, profile.email,
                    tx.schedulePickupNotification.dropOffOrg.orgId,
                    tx);
              },
            ),
          ],
        ),
      );
    }
    else{
      padding = Padding(
        padding: const EdgeInsets.only(
            right: 16, top: 8),
        child: Column(
          mainAxisAlignment:
          MainAxisAlignment.center,
          crossAxisAlignment:
          CrossAxisAlignment.end,
          children: <Widget>[
            ElevatedButton(
              child: Text('Community DropOff'),
              style: ElevatedButton.styleFrom(
                //primary: Color(0xFF383EDB)
                  primary: Colors.pink
              ),
              onPressed: () {
                print(tx);
                ActiveNetworkRestClient restClient = new ActiveNetworkRestClient();
                Future<int> notifyDelivery = restClient.notifyDelivery(tx);
                //print(tx);
                //print("MAA_CHUDA");
                //print(tx.getPickupNotification().getSourceOrg());
                //print(tx.getPickupNotification().getDropOffOrg());
              },
            ),
          ],
        ),
      );
    }
    return padding;
  }

  void handleDropOff(BuildContext context,String email, String dropOffOrgId, FoodRecoveryTransaction tx) {
    //print("TX: "+tx.getPickupNotification().getSourceOrg().orgName);

    //TODO
    AlertDialog dialog = AlertDialog(
      title: Text('Start DropOff'),
      content: Text(
        tx.getPickupNotification().getDropOffOrg().orgName+": "+"506 West Avenue"+","+"78701",
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
            LocationUpdater.getLocation();
            EmbeddedNavigation navigation = EmbeddedNavigation(context,
                tx.getPickupNotification().getDropOffOrg());
            navigation.start(tx);
          },
          child: Text('START NAVIGATION'),
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