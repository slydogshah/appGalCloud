import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:flutter/material.dart';

class UiFunctions{
  static BottomNavigationBar bottomNavigationBar(BuildContext context)
  {
    BottomNavigationBar bottomNavigationBar = new BottomNavigationBar(
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
              SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", null, true);
              List<SourceOrg> sourceOrgs = new List();
              sourceOrgs.add(sourceOrg);
              //Navigator.push(context,MaterialPageRoute(builder: (context) => PickupSource(sourceOrgs))); 
            }
            else if(index == 2)
            {
              print("Send Food Request");
              ActiveNetworkRestClient client = ActiveNetworkRestClient();
              //TODO:REMOVE_MOCK_DATA
              SourceOrg sourceOrg = new SourceOrg("microsoft", "Microsoft", "melinda_gates@microsoft.com", null, true);
              FoodRequest foodRequest = new FoodRequest("id", "VEG", sourceOrg);
              Future<String> future = client.sendFoodRequest(foodRequest);
              future.then((jsonString){
                    print(jsonString);
                    //Navigator.push(context,MaterialPageRoute(builder: (context) => ApplicableSources()));
              });
            }
          },
        );
    return bottomNavigationBar;
  }
}