// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// ignore_for_file: public_member_api_docs
// ignore_for_file: unawaited_futures

import 'package:app/src/ui/uiFunctions.dart';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';

import 'page.dart';


import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/uiFunctions.dart';
import 'package:flutter/material.dart';

import 'applicableSources.dart';
import 'driveToDestination.dart';
import 'foodRunnerDestinations.dart';

class MarkerIconsPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return const MarkerIconsBody();
  }
}

class MarkerIconsBody extends StatefulWidget {
  const MarkerIconsBody();

  @override
  State<StatefulWidget> createState() => MarkerIconsBodyState();
}

const LatLng _kMapCenter = LatLng(52.4478, -3.5402);

class MarkerIconsBodyState extends State<MarkerIconsBody> {
  GoogleMapController controller;
  BitmapDescriptor _markerIcon;

  @override
  Widget build(BuildContext context) {
    _createMarkerImageFromAsset(context);

    /*return Column(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: <Widget>[
        Center(
          child: SizedBox(
            width: 350.0,
            height: 300.0,
            child: GoogleMap(
              initialCameraPosition: const CameraPosition(
                target: _kMapCenter,
                zoom: 7.0,
              ),
              markers: _createMarker(),
              onMapCreated: _onMapCreated,
            ),
          ),
        )
      ],
    );*/

    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('Maps Sample App'),
          backgroundColor: Colors.green[700],
        ),
        body: GoogleMap(
              initialCameraPosition: const CameraPosition(
                target: _kMapCenter,
                zoom: 7.0,
              ),
              markers: _createMarker(),
              onMapCreated: _onMapCreated,
        ),
        bottomNavigationBar: UiFunctions.bottomNavigationBar(context)
      ),
    );
  }

  Set<Marker> _createMarker() {
    // TODO(iskakaushik): Remove this when collection literals makes it to stable.
    // https://github.com/flutter/flutter/issues/28312
    // ignore: prefer_collection_literals
    return <Marker>[
      Marker(
        markerId: MarkerId("marker_1"),
        position: _kMapCenter,
        icon: _markerIcon,
      ),
    ].toSet();
  }

  Future<void> _createMarkerImageFromAsset(BuildContext context) async {
    if (_markerIcon == null) {
      final ImageConfiguration imageConfiguration =
          createLocalImageConfiguration(context);
      BitmapDescriptor.fromAssetImage(
              imageConfiguration, 'assets/maps/icon.png')
          .then(_updateBitmap);
    }
  }

  void _updateBitmap(BitmapDescriptor bitmap) {
    setState(() {
      _markerIcon = bitmap;
    });
  }

  void _onMapCreated(GoogleMapController controllerParam) {
    setState(() {
      controller = controllerParam;
    });
  }
}


class FoodRunnerMainScene extends StatefulWidget {
  List<SourceOrg> sourceOrgs;
  FoodRunnerMainScene(List<SourceOrg> sourceOrgs)
  {
    this.sourceOrgs = sourceOrgs;
  }

  @override
  _FoodRunnerMainState createState() => _FoodRunnerMainState(this.sourceOrgs);
}

class _FoodRunnerMainState extends State<FoodRunnerMainScene> {
  List<SourceOrg> sourceOrgs;
  _FoodRunnerMainState(List<SourceOrg> sourceOrgs)
  {
    this.sourceOrgs = sourceOrgs;
  }

  List<Card> getCards()
  {
    List<Card> cards = new List();
    for(SourceOrg sourceOrg in this.sourceOrgs)
    {
      Location location = new Location(0.0, 0.0);
      sourceOrg.location = location;
      Card card = Card(shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15.0),
                    ),
                    color: Colors.pink,
                    elevation: 10,
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: <Widget>[
                        ListTile(
                          leading: Icon(Icons.album, size: 70),
                          title: Text('Organization', style: TextStyle(color: Colors.white)),
                          subtitle: Text(sourceOrg.orgName, style: TextStyle(color: Colors.white)),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Organization Id: '+sourceOrg.orgId,
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Email: '+sourceOrg.orgContactEmail,
                          ),
                        ),
                        ButtonTheme.bar(
                          child: ButtonBar(
                            children: <Widget>[
                              FlatButton(
                                child: const Text('Drop Off', style: TextStyle(color: Colors.white)),
                                onPressed: () {
                                    handleDriveToDestination(context);
                                },
                              ),
                              FlatButton(
                                child: const Text('Pick Up', style: TextStyle(color: Colors.white)),
                                onPressed: () {
                                  handlePickupSource(context,sourceOrg);
                                },
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  );
        cards.add(card);
    }
    return cards;
  }

  void handleClick(BuildContext context)
  {
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Profile profile = ActiveSession.getInstance().getProfile();
    FoodRunner foodRunner = new FoodRunner(profile);
    Future<Iterable> futureP = activeNetworkRestClient.findBestDestination(foodRunner);
    futureP.then((sourceOrgs){
      if(sourceOrgs.length == 0)
      {
        return;
      }

      Navigator.push(context,
      MaterialPageRoute(builder: (context) => new FoodRunnerDestination(sourceOrgs)));
      
      //also send a notification I am on my way
      Map<String, dynamic> json = sourceOrgs.elementAt(0);
      SourceOrg sourceOrg = SourceOrg.fromJson(json);
      DropOffNotification dropOffNotification = DropOffNotification(sourceOrg, foodRunner);
      activeNetworkRestClient.sendDeliveryNotification(dropOffNotification);
    });
  }

  void handleDriveToDestination(BuildContext context)
  {
    Navigator.push(context,MaterialPageRoute(builder: (context) => DriveToDestinationScene()));
  }

  void handlePickupSource(BuildContext context, SourceOrg sourceOrg)
  {
    List<SourceOrg> sourceOrgs = new List();
    sourceOrgs.add(sourceOrg);
    //Navigator.push(context,MaterialPageRoute(builder: (context) => PickupSource(sourceOrgs))); 
  }
  
  @override
  Widget build(BuildContext context) {
    Scaffold scaffold = Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false,
        title: Text("title"),
      ),
      body: Scrollbar(
        child: ListView(
          padding: const EdgeInsets.only(top: 8, left: 8, right: 8),
          children: this.getCards(),
        ),
      ),
      bottomNavigationBar: UiFunctions.bottomNavigationBar(context)
    );
    return scaffold;
  }
}
