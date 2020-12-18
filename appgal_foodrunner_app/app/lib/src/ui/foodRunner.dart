import 'package:app/src/ui/uiFunctions.dart';
import 'package:flutter/material.dart';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

import 'schedulePickup.dart';
import 'foodRunnerDestinations.dart';

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
                          labelText: 'Email: '+sourceOrg.orgContactEmail,
                          ),
                        ),
                        ButtonTheme.bar(
                          child: ButtonBar(
                            children: <Widget>[
                              FlatButton(
                                child: const Text('Schedule Pickup', style: TextStyle(color: Colors.white)),
                                onPressed: () {
                                    handleSchedulePickup(context, sourceOrg);
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

  void handleSchedulePickup(BuildContext context, SourceOrg sourceOrg)
  {
    Navigator.push(context,MaterialPageRoute(builder: (context) => SchedulePickup(sourceOrg)));
  }
}