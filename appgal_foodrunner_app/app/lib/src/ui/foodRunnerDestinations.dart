// Copyright 2020 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/pickupSource.dart';
import 'package:app/src/ui/uiFunctions.dart';
import 'package:flutter/material.dart';

import 'applicableSources.dart';
import 'driveToDestination.dart';

const String _kGalleryAssetsPackage = 'flutter_gallery_assets';

class FoodRunnerDestination extends StatefulWidget {
  Iterable json;
  FoodRunnerDestination(Iterable json)
  {
    this.json = json;
  }

  @override
  _FoodRunnerDestinationState createState() => _FoodRunnerDestinationState(this.json);
}

class _FoodRunnerDestinationState extends State<FoodRunnerDestination> {
  Iterable json;
  _FoodRunnerDestinationState(Iterable json)
  {
    this.json = json;
  }

  List<Card> getCards()
  {
    List<Card> cards = new List();
    for(Map<String, dynamic> json in this.json)
    {
      SourceOrg sourceOrg = SourceOrg.fromJson(json);
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
                        const ListTile(
                          leading: Icon(Icons.album, size: 70),
                          title: Text('Food Runner', style: TextStyle(color: Colors.white)),
                          subtitle: Text('214 Barton Sprinngs Road', style: TextStyle(color: Colors.white)),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Food Runner Id: '+json['orgId'],
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Email: '+json['orgName'],
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Mobile: '+json['orgContactEmail'],
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

  void handleDriveToDestination(BuildContext context)
  {
    Navigator.push(context,MaterialPageRoute(builder: (context) => DriveToDestinationScene()));
  }

  void handlePickupSource(BuildContext context, SourceOrg sourceOrg)
  {
    List<SourceOrg> sourceOrgs = new List();
    sourceOrgs.add(sourceOrg);
    Navigator.push(context,MaterialPageRoute(builder: (context) => PickupSource(sourceOrgs)));  
  }
  
  @override
  Widget build(BuildContext context) {
    SourceOrg sourceOrg;
    for(Map<String, dynamic> json in this.json)
    {
      sourceOrg = SourceOrg.fromJson(json);
      Location location = new Location(0.0, 0.0);
      sourceOrg.location = location;
      //print(sourceOrg.toString());
    }
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