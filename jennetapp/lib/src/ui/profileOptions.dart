import 'package:app/src/ui/registration.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';

class ProfileOptions{
  static BottomNavigationBar bottomNavigationBar(BuildContext context)
  {
    BottomNavigationBar bottomNavigationBar = new BottomNavigationBar(
          currentIndex: 0, // this will be set when a new tab is tapped
          items: [
            BottomNavigationBarItem(
              icon: new Icon(Icons.apps),
              title: new Text('')
            ),
            BottomNavigationBarItem(
              icon: new Icon(Icons.mail),
              title: new Text('Register'),
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.apps),
              title: Text('')
            )
          ],
          onTap: (index){
            if(index == 1)
            {
              Navigator.push(context,MaterialPageRoute(builder: (context) => new Registration()));
            }
          },
        );
    return bottomNavigationBar;
  }
}