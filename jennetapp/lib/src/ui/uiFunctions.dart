import 'package:flutter/material.dart';

class UiFunctions{
  static BottomNavigationBar bottomNavigationBar(BuildContext context)
  {
    BottomNavigationBar bottomNavigationBar = new BottomNavigationBar(
          currentIndex: 0, // this will be set when a new tab is tapped
          items: [
            BottomNavigationBarItem(
              icon: new Icon(Icons.home),
              title: new Text('Home'),
            ),
            BottomNavigationBarItem(
              icon: new Icon(Icons.mail),
              title: new Text('Offline Support'),
            )
          ],
          onTap: (index){
            if(index == 1)
            {
              print("ANNOUNCING_OFFLINE_AVAILABILITY");
            }
          },
        );
    return bottomNavigationBar;
  }
}