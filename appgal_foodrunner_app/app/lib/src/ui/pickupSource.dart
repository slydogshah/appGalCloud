// Copyright 2020 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:flutter/material.dart';

import 'foodRunnerDestinations.dart';

class PickupSource extends StatefulWidget {
  SourceOrg sourceOrg;
  PickupSource(SourceOrg sourceOrg)
  {
    this.sourceOrg = sourceOrg;
  }

  @override
  _PickupSourceState createState() => _PickupSourceState(this.sourceOrg);
}

class _PickupSourceState extends State<PickupSource> {
  SourceOrg sourceOrg;
  _PickupSourceState(SourceOrg sourceOrg)
  {
    this.sourceOrg = sourceOrg;
  }

  List<Card> getCard()
  {
    List<Card> cards = new List();
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
                          labelText: 'Pickup Organization: '+sourceOrg.orgId,
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Pickup Organization: '+sourceOrg.orgName,
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
                                child: const Text('Edit', style: TextStyle(color: Colors.white)),
                                onPressed: () {
                                    handleClick(context);
                                },
                              ),
                              FlatButton(
                                child: const Text('Delete', style: TextStyle(color: Colors.white)),
                                onPressed: () {},
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  );
    cards.add(card);
    return cards;
  }

  void handleClick(BuildContext context)
  {
    ActiveNetworkRestClient activeNetworkRestClient = new ActiveNetworkRestClient();
    Future<Iterable> futureP = activeNetworkRestClient.findBestDestination(new FoodRunner(new Profile("id","email","mobile","phone","password"), new Location(0.0, 0.0)));
    futureP.then((sourceOrgs){
      Navigator.push(context,
      MaterialPageRoute(builder: (context) => new FoodRunnerDestination(sourceOrgs)));
      
      //also send a notification I am on my way
      Profile profile = ActiveSession.getInstance().getProfile();
      Location location = new Location(profile.getLatitude(), profile.getLongitude());
      FoodRunner foodRunner = new FoodRunner(profile, location);
      Map<String, dynamic> json = sourceOrgs.elementAt(0);
      SourceOrg sourceOrg = SourceOrg.fromJson(json);
      DropOffNotification dropOffNotification = DropOffNotification(sourceOrg, location, foodRunner);
      activeNetworkRestClient.sendDeliveryNotification(dropOffNotification);
    });
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
          children: this.getCard(),
        ),
      ),
    );
    return scaffold;
  }
}