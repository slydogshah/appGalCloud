import 'package:app/hotel_booking/calendar_popup_view.dart';
import 'package:app/hotel_booking/filters_screen.dart';
import 'package:app/hotel_booking/hotel_app_theme.dart';
import 'package:app/hotel_booking/hotel_home_screen.dart';
import 'package:app/hotel_booking/model/hotel_list_data.dart';
import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/profile.dart';
import 'package:flutter/material.dart';

import 'package:app/src/ui/uiFunctions.dart';
import 'package:app/src/navigation/embeddedNavigation.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:smooth_star_rating/smooth_star_rating.dart';

class FoodRunnerMainScene extends StatefulWidget {
  List<FoodRecoveryTransaction> recoveryTxs;

  FoodRunnerMainScene(List<FoodRecoveryTransaction> recoveryTxs)
  {
    this.recoveryTxs = recoveryTxs;
  }

  @override
  _FoodRunnerMainState createState() => _FoodRunnerMainState(this.recoveryTxs);
}

class _FoodRunnerMainState extends State<FoodRunnerMainScene> with TickerProviderStateMixin {

  AnimationController animationController;
  List<HotelListData> hotelList = HotelListData.hotelList;

  List<FoodRecoveryTransaction> recoveryTxs;

  final ScrollController _scrollController = ScrollController();

  DateTime startDate = DateTime.now();
  DateTime endDate = DateTime.now().add(const Duration(days: 5));

  _FoodRunnerMainState(List<FoodRecoveryTransaction> recoveryTxs) {
    this.recoveryTxs = recoveryTxs;
  }

  @override
  void initState() {
    animationController = AnimationController(
        duration: const Duration(milliseconds: 1000), vsync: this);
    super.initState();
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
                            itemCount: this.recoveryTxs.length,
                            padding: const EdgeInsets.only(top: 8),
                            scrollDirection: Axis.vertical,
                            itemBuilder: (BuildContext context, int index) {
                              final int count = this.recoveryTxs.length;
                              final Animation<double> animation =
                              Tween<double>(begin: 0.0, end: 1.0).animate(
                                  CurvedAnimation(
                                      parent: animationController,
                                      curve: Interval(
                                          (1 / count) * index, 1.0,
                                          curve: Curves.fastOutSlowIn)));
                              animationController.forward();
                              return RequestView(
                                callback: () {},
                                recoveryTransactions: this.recoveryTxs,
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
                  Material(
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
                  ),
                  Material(
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

class RequestView extends StatelessWidget {
  const RequestView(
      {Key key,
        this.recoveryTransactions,
        this.hotelList,
        this.animationController,
        this.animation,
        this.callback})
      : super(key: key);

  final VoidCallback callback;
  final List<FoodRecoveryTransaction> recoveryTransactions;
  final AnimationController animationController;
  final Animation<dynamic> animation;
  final List<HotelListData> hotelList;

  @override
  Widget build(BuildContext context) {
    if(this.recoveryTransactions == null || this.recoveryTransactions.isEmpty)
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
                                child: Image.asset(
                                  HotelListData.hotelList[0].imagePath,
                                  fit: BoxFit.cover,
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
                                              Row(
                                                crossAxisAlignment:
                                                CrossAxisAlignment.center,
                                                mainAxisAlignment:
                                                MainAxisAlignment.start,
                                                children: <Widget>[
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
                                                  Expanded(
                                                    child: Text(
                                                      'Pickup: 10 minutes',
                                                      overflow:
                                                      TextOverflow.ellipsis,
                                                      style: TextStyle(
                                                          fontSize: 14,
                                                          color: Colors.grey
                                                              .withOpacity(0.8)),
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              Padding(
                                                padding:
                                                const EdgeInsets.only(top: 4),
                                                child: Row(
                                                  crossAxisAlignment:
                                                  CrossAxisAlignment.center,
                                                  mainAxisAlignment:
                                                  MainAxisAlignment.start,
                                                  children: <Widget>[
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
                                                    Expanded(
                                                      child: Text(
                                                        'DropOff: 15 minutes',
                                                        overflow:
                                                        TextOverflow.ellipsis,
                                                        style: TextStyle(
                                                            fontSize: 14,
                                                            color: Colors.grey
                                                                .withOpacity(0.8)),
                                                      ),
                                                    ),
                                                  ],
                                                ),
                                              ),
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

    final List<Widget> requests = <Widget>[];

    for (int index = 0; index < this.recoveryTransactions.length; index++) {
      final int count = this.recoveryTransactions.length;
      final Animation<double> animation =
      Tween<double>(begin: 0.0, end: 1.0).animate(
        CurvedAnimation(
          parent: animationController,
          curve: Interval((1 / count) * index, 1.0, curve: Curves.fastOutSlowIn),
        ),
      );

      requests.add(AnimatedBuilder(
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
                              AspectRatio(
                                aspectRatio: 2,
                                child: Image.asset(
                                  HotelListData.hotelList[0].imagePath,
                                  fit: BoxFit.cover,
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
                                                this.recoveryTransactions[index].getPickupNotification().getSourceOrg().orgName,
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
                                                  /*Text(
                                                  HotelListData.hotelList[0].subTxt,
                                                  style: TextStyle(
                                                      fontSize: 14,
                                                      color: Colors.grey
                                                          .withOpacity(0.8)),
                                                ),*/
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
                                                  Expanded(
                                                    child: Text(
                                                      'Pickup: 10 minutes',
                                                      overflow:
                                                      TextOverflow.ellipsis,
                                                      style: TextStyle(
                                                          fontSize: 14,
                                                          color: Colors.grey
                                                              .withOpacity(0.8)),
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              Padding(
                                                padding:
                                                const EdgeInsets.only(top: 4),
                                                child: Row(
                                                  crossAxisAlignment:
                                                  CrossAxisAlignment.center,
                                                  mainAxisAlignment:
                                                  MainAxisAlignment.start,
                                                  children: <Widget>[
                                                    /*SmoothStarRating(
                                                    allowHalfRating: true,
                                                    starCount: 5,
                                                    rating: HotelListData.hotelList[0].rating,
                                                    size: 20,
                                                    color: HotelAppTheme
                                                        .buildLightTheme()
                                                        .primaryColor,
                                                    borderColor: HotelAppTheme
                                                        .buildLightTheme()
                                                        .primaryColor,
                                                  ),
                                                  Text(
                                                    ' ${HotelListData.hotelList[0].reviews} Reviews',
                                                    style: TextStyle(
                                                        fontSize: 14,
                                                        color: Colors.grey
                                                            .withOpacity(0.8)),
                                                  ),*/
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
                                                    Expanded(
                                                      child: Text(
                                                        'DropOff: 15 minutes',
                                                        overflow:
                                                        TextOverflow.ellipsis,
                                                        style: TextStyle(
                                                            fontSize: 14,
                                                            color: Colors.grey
                                                                .withOpacity(0.8)),
                                                      ),
                                                    ),
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
                                          Text(
                                            //'\$${HotelListData.hotelList[0].perNight}',
                                            this.recoveryTransactions[index].getPickupNotification().dropOffOrg.orgName,
                                            textAlign: TextAlign.left,
                                            style: TextStyle(
                                              fontWeight: FontWeight.w600,
                                              fontSize: 22,
                                            ),
                                          ),
                                          /*Text(
                                          '/per night',
                                          style: TextStyle(
                                              fontSize: 14,
                                              color:
                                              Colors.grey.withOpacity(0.8)),
                                        ),*/
                                        ],
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
                                          ElevatedButton(
                                            child: Text('Accept'),
                                            style: ElevatedButton.styleFrom(
                                              //primary: Color(0xFF383EDB)
                                                primary: Colors.pink
                                            ),
                                            onPressed: () {
                                              Profile profile = ActiveSession.getInstance().getProfile();
                                              handleAccept(context,profile.email,
                                                  this.recoveryTransactions[index].schedulePickupNotification.dropOffOrg.orgId,
                                                  this.recoveryTransactions[index]);
                                            },
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
      ));
    }

    animationController.forward();
    return Column(
      children: requests,
    );
    /*return AnimatedBuilder(
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
                            AspectRatio(
                              aspectRatio: 2,
                              child: Image.asset(
                                HotelListData.hotelList[0].imagePath,
                                fit: BoxFit.cover,
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
                                              this.recoveryTransactions[index].getPickupNotification().getSourceOrg().orgName,
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
                                                /*Text(
                                                  HotelListData.hotelList[0].subTxt,
                                                  style: TextStyle(
                                                      fontSize: 14,
                                                      color: Colors.grey
                                                          .withOpacity(0.8)),
                                                ),*/
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
                                                Expanded(
                                                  child: Text(
                                                    'Pickup: 10 minutes',
                                                    overflow:
                                                    TextOverflow.ellipsis,
                                                    style: TextStyle(
                                                        fontSize: 14,
                                                        color: Colors.grey
                                                            .withOpacity(0.8)),
                                                  ),
                                                ),
                                              ],
                                            ),
                                            Padding(
                                              padding:
                                              const EdgeInsets.only(top: 4),
                                              child: Row(
                                                crossAxisAlignment:
                                                CrossAxisAlignment.center,
                                                mainAxisAlignment:
                                                MainAxisAlignment.start,
                                                children: <Widget>[
                                                  /*SmoothStarRating(
                                                    allowHalfRating: true,
                                                    starCount: 5,
                                                    rating: HotelListData.hotelList[0].rating,
                                                    size: 20,
                                                    color: HotelAppTheme
                                                        .buildLightTheme()
                                                        .primaryColor,
                                                    borderColor: HotelAppTheme
                                                        .buildLightTheme()
                                                        .primaryColor,
                                                  ),
                                                  Text(
                                                    ' ${HotelListData.hotelList[0].reviews} Reviews',
                                                    style: TextStyle(
                                                        fontSize: 14,
                                                        color: Colors.grey
                                                            .withOpacity(0.8)),
                                                  ),*/
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
                                                  Expanded(
                                                    child: Text(
                                                      'DropOff: 15 minutes',
                                                      overflow:
                                                      TextOverflow.ellipsis,
                                                      style: TextStyle(
                                                          fontSize: 14,
                                                          color: Colors.grey
                                                              .withOpacity(0.8)),
                                                    ),
                                                  ),
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
                                        Text(
                                          //'\$${HotelListData.hotelList[0].perNight}',
                                          this.recoveryTransactions[index].getPickupNotification().dropOffOrg.orgName,
                                          textAlign: TextAlign.left,
                                          style: TextStyle(
                                            fontWeight: FontWeight.w600,
                                            fontSize: 22,
                                          ),
                                        ),
                                        /*Text(
                                          '/per night',
                                          style: TextStyle(
                                              fontSize: 14,
                                              color:
                                              Colors.grey.withOpacity(0.8)),
                                        ),*/
                                      ],
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
                                        ElevatedButton(
                                          child: Text('Accept'),
                                          style: ElevatedButton.styleFrom(
                                              //primary: Color(0xFF383EDB)
                                              primary: Colors.pink
                                          ),
                                          onPressed: () {
                                            Profile profile = ActiveSession.getInstance().getProfile();
                                            handleAccept(context,profile.email,
                                                this.recoveryTransactions[index].schedulePickupNotification.dropOffOrg.orgId,
                                                this.recoveryTransactions[index]);
                                          },
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
    );*/
  }

  void handleAccept(BuildContext context,String email, String dropOffOrgId, FoodRecoveryTransaction tx) {
    print(tx);
    ActiveNetworkRestClient client = new ActiveNetworkRestClient();
    Future<int> future = client.accept(email, dropOffOrgId, tx);
    future.then((statusCode) {
      if (statusCode == 200) {
        LocationUpdater.getLocation();
        EmbeddedNavigation embeddedNavigation = new EmbeddedNavigation(
            tx.getPickupNotification().getDropOffOrg());
        embeddedNavigation.start();
      }
      else {
        //TODO
      }
    });
  }
}

class NoActiveRequests extends StatelessWidget
{

  const NoActiveRequests(
      {Key key,
        this.animationController,
        this.animation})
      : super(key: key);

  final AnimationController animationController;
  final Animation<dynamic> animation;

  @override
  Widget build(BuildContext context) {
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
                  //callback();
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

}

