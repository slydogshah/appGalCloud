import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:flutter/material.dart';

class UiFunctions{
  BottomNavigationBarItem offlineSupport;

  UiFunctions()
  {
    offlineSupport = new BottomNavigationBarItem(
      icon: new Icon(Icons.mail),
      title: new Text('Offline Support'),
    );
  }

  static BottomNavigationBar bottomNavigationBar(BuildContext context)
  {
    UiFunctions uiFunctions = new UiFunctions();
    BottomNavigationBar bottomNavigationBar = new BottomNavigationBar(
          currentIndex: 0, // this will be set when a new tab is tapped
          items: [
            BottomNavigationBarItem(
              icon: new Icon(Icons.home),
              title: new Text('Home'),
            ),
            uiFunctions.offlineSupport
          ],
          onTap: (index){
            if(index == 1)
            {
              print("ANNOUNCING_OFFLINE_AVAILABILITY");
              ActiveNetworkRestClient activeNetworkClient = new ActiveNetworkRestClient();
              Future<String> response = activeNetworkClient.notifyOfflineAvailability("123");
              response.then((response){
                print("RESPONSE: "+response.toString());

                //TODO: Change icon to hot
                uiFunctions.offlineSupport = new BottomNavigationBarItem(
                  icon: new Icon(Icons.home),
                  title: new Text('Offline Support'),
                );
              });
            }
          },
        );
    return bottomNavigationBar;
  }
}