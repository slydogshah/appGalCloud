import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/profile.dart';
import 'package:flutter/material.dart';

import 'package:app/src/ui/uiFunctions.dart';
import 'package:app/src/navigation/embeddedNavigation.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';

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
      //FoodRunnerLocation location = new FoodRunnerLocation(0.0, 0.0);
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
                          labelText: 'DropOff: '+tx.schedulePickupNotification.dropOffOrg.orgName,
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
                            Profile profile = ActiveSession.getInstance().getProfile();
                            handleAccept(context,profile.email,tx.schedulePickupNotification.dropOffOrg.orgId, tx);
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

  void handleAccept(BuildContext context,String email, String dropOffOrgId, FoodRecoveryTransaction tx)
  {
    ActiveNetworkRestClient client = new ActiveNetworkRestClient();
    Future<int> future = client.accept(email, dropOffOrgId, tx);
    future.then((statusCode) {
      if(statusCode == 200) {
        EmbeddedNavigation embeddedNavigation = new EmbeddedNavigation();
        embeddedNavigation.start();
      }
      else {
          //TODO
      }
    });
  }
}