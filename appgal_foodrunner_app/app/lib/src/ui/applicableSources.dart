// Copyright 2020 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/ui/pickupSource.dart';
import 'package:flutter/material.dart';

import 'driveToDestination.dart';

const String _kGalleryAssetsPackage = 'flutter_gallery_assets';

class ApplicableSources extends StatefulWidget {
  @override
  ApplicableSourcesState createState() => ApplicableSourcesState();
}

class ApplicableSourcesState extends State<ApplicableSources> {
  ApplicableSources()
  {
  }

  List<Card> getCards()
  {
    List<Card> cards = new List();
    //for(Map<String, dynamic> json in this.json)
    //{
      Location location = new Location(0.0, 0.0);
      SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com",location);
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
                          labelText: 'Food Runner Id: '+sourceOrg.orgId,
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Email: '+sourceOrg.orgContactEmail,
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Mobile: '+sourceOrg.orgName,
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
                                  handlePickupSource(context, sourceOrg);
                                },
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  );
        cards.add(card);
    //}
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
      bottomNavigationBar: BottomNavigationBar(
          currentIndex: 0, // this will be set when a new tab is tapped
          items: [
            BottomNavigationBarItem(
              icon: new Icon(Icons.home),
              title: new Text('Home')
            ),
            BottomNavigationBarItem(
              icon: new Icon(Icons.mail),
              title: new Text('PickUp Request'),
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.mail),
              title: Text('Send Food Request')
            )
          ],
          onTap: (index){
            if(index == 0)
            {
              print("Home");
            }
            else if(index == 1)
            {
              print("PickUp Request");
              /*ActiveNetworkRestClient client = ActiveNetworkRestClient();
              SourceOrg sourceOrg = new SourceOrg("test", "TEST", "testing@test.com", null);
              PickupRequest pickupRequest = new PickupRequest(sourceOrg);
              Future<Iterable> future = client.sendPickupRequest(pickupRequest);
              future.then((profiles){
                for(Map<String, dynamic> json in profiles)
                {
                  Profile profile = Profile.fromJson(json);
                  print(profile.toString());
                }
              });*/
              //TODO:REMOVE_MOCK_DATA
              SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", null);
              List<SourceOrg> sourceOrgs = new List();
              sourceOrgs.add(sourceOrg);
              Navigator.push(context,MaterialPageRoute(builder: (context) => PickupSource(sourceOrgs))); 
            }
            else if(index == 2)
            {
              print("Send Food Request");
              ActiveNetworkRestClient client = ActiveNetworkRestClient();
              //TODO:REMOVE_MOCK_DATA
              SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", null);
              FoodRequest foodRequest = new FoodRequest("id", "VEG", sourceOrg);
              Future<String> future = client.sendFoodRequest(foodRequest);
              future.then((jsonString){
                    print(jsonString);
                    Navigator.push(context,MaterialPageRoute(builder: (context) => ApplicableSources()));
              });
            }
          },
        )
    );
    return scaffold;
  }
}