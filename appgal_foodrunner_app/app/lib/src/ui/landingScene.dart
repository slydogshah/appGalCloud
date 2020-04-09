import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class LandingScene extends StatelessWidget {
   @override
   Widget build(BuildContext context) {
     return Scaffold(
       appBar: AppBar(
         title: Text('First Route'),
       ),
       body: Center(
         child: RaisedButton(
           child: Text('Open route'),
           onPressed: () {
             // Navigate to second route when tapped.
           },
         ),
       ),
     );
   }
 }