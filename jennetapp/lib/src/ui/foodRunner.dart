import 'dart:io';
import 'dart:convert';

import 'package:flutter/material.dart';

import 'package:app/src/ui/uiFunctions.dart';
import 'package:app/src/navigation/embeddedNavigation.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';

import 'schedulePickup.dart';

class FoodRunnerMainScene extends StatefulWidget {
  List<FoodRecoveryTransaction> recoveryTxs;

  FoodRunnerMainScene(List<FoodRecoveryTransaction> recoveryTxs)
  {
    this.recoveryTxs = recoveryTxs;
  }

  @override
  _FoodRunnerMainState createState() => _FoodRunnerMainState(this.recoveryTxs);
}

class _FoodRunnerMainState extends State<FoodRunnerMainScene> {
    List<FoodRecoveryTransaction> recoveryTxs;
  _FoodRunnerMainState(List<FoodRecoveryTransaction> recoveryTxs)
  {
    this.recoveryTxs = recoveryTxs;
  }

  List<Card> getCards()
  {
    List<Card> cards = new List();
    for(FoodRecoveryTransaction tx in this.recoveryTxs)
    {
      if(tx.dropOffNotification == null)
      {
        continue;
      }

      Location location = new Location(0.0, 0.0);
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
                          title: Text('Pickup Request', style: TextStyle(color: Colors.white)),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Pickup: '+tx.schedulePickupNotification.sourceOrg.orgName,
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'DropOff: '+tx.dropOffNotification.sourceOrg.orgName,
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Est Pickup Trip: 10 minutes',
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Est DropOff Trip: 15 minutes',
                          ),
                        ),
                        ElevatedButton(
                          child: Text('Accept'),
                          style: ElevatedButton.styleFrom(
                            primary: Color(0xFF383EDB)
                          ),
                          onPressed: () {
                            handleAccept(context, tx);
                          },
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
        title: Text("Pickup Requests"),
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

  void handleAccept(BuildContext context, FoodRecoveryTransaction tx)
  {
    EmbeddedNavigation embeddedNavigation = new EmbeddedNavigation();
    embeddedNavigation.start();
  }
}