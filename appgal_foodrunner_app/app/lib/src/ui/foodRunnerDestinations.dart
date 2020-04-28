// Copyright 2020 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';

import 'package:app/src/model/location.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
//import './l10n/gallery_localizations.dart';

const String _kGalleryAssetsPackage = 'flutter_gallery_assets';

class FoodRunnerDestination extends StatefulWidget {
  Iterable json;
  FoodRunnerDestination(Iterable json)
  {
    this.json = json;
  }

  @override
  _FoodRunnerDestinationState createState() => _FoodRunnerDestinationState(this.json);
}

class _FoodRunnerDestinationState extends State<FoodRunnerDestination> {
  Iterable json;
  _FoodRunnerDestinationState(Iterable json)
  {
    this.json = json;
  }

  List<Card> getCard()
  {
    List<Card> cards = new List();
    for(Map<String, dynamic> json in this.json)
    {
      SourceOrg sourceOrg = SourceOrg.fromJson(json);
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
                          labelText: 'Food Runner Id: '+json['orgId'],
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Email: '+json['orgName'],
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Mobile: '+json['orgContactEmail'],
                          ),
                        ),
                        ButtonTheme.bar(
                          child: ButtonBar(
                            children: <Widget>[
                              FlatButton(
                                child: const Text('Edit', style: TextStyle(color: Colors.white)),
                                onPressed: () {},
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
    }
    return cards;
  }
  
  @override
  Widget build(BuildContext context) {
    SourceOrg sourceOrg = null;
    for(Map<String, dynamic> json in this.json)
    {
      sourceOrg = SourceOrg.fromJson(json);
      Location location = new Location(0.0, 0.0);
      sourceOrg.location = location;
      print(sourceOrg.toString());
    }
    String id = sourceOrg.orgId;
    String email = sourceOrg.orgName;
    String mobile = sourceOrg.orgContactEmail;
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
// BEGIN cardsDemo

enum CardDemoType {
  standard,
  tappable,
  selectable,
}

class TravelDestination {
  const TravelDestination({
    this.assetName,
    this.assetPackage,
    @required this.title,
    @required this.description,
    @required this.city,
    @required this.location,
    this.type = CardDemoType.standard,
  })  : assert(title != null),
        assert(description != null),
        assert(city != null),
        assert(location != null);

  final String assetName;
  final String assetPackage;
  final String title;
  final String description;
  final String city;
  final String location;
  final CardDemoType type;
}

List<TravelDestination> destinations(BuildContext context) {
    List<TravelDestination> destinations = [
      TravelDestination(
        assetName: 'places/india_thanjavur_market.png',
        assetPackage: _kGalleryAssetsPackage,
        title:
            "title",
        description: "description",
        city: "city",
        location: "location",
      ),
    ];
    return destinations;
}

class TravelDestinationItem extends StatelessWidget {
  const TravelDestinationItem({Key key, @required this.destination, this.shape})
      : assert(destination != null),
        super(key: key);

  // This height will allow for all the Card's content to fit comfortably within the card.
  static const height = 338.0;
  final TravelDestination destination;
  final ShapeBorder shape;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: false,
      bottom: false,
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: Column(
          children: [
            SizedBox(
              height: height,
              child: Card(
                // This ensures that the Card's children are clipped correctly.
                clipBehavior: Clip.antiAlias,
                shape: shape,
                child: TravelDestinationContent(destination: destination),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class TappableTravelDestinationItem extends StatelessWidget {
  const TappableTravelDestinationItem(
      {Key key, @required this.destination, this.shape})
      : assert(destination != null),
        super(key: key);

  // This height will allow for all the Card's content to fit comfortably within the card.
  static const height = 298.0;
  final TravelDestination destination;
  final ShapeBorder shape;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: false,
      bottom: false,
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: Column(
          children: [
            SizedBox(
              height: height,
              child: Card(
                // This ensures that the Card's children (including the ink splash) are clipped correctly.
                clipBehavior: Clip.antiAlias,
                shape: shape,
                child: InkWell(
                  onTap: () {
                    print('Card was tapped');
                  },
                  // Generally, material cards use onSurface with 12% opacity for the pressed state.
                  splashColor:
                      Theme.of(context).colorScheme.onSurface.withOpacity(0.12),
                  // Generally, material cards do not have a highlight overlay.
                  highlightColor: Colors.transparent,
                  child: TravelDestinationContent(destination: destination),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class SelectableTravelDestinationItem extends StatefulWidget {
  const SelectableTravelDestinationItem(
      {Key key, @required this.destination, this.shape})
      : assert(destination != null),
        super(key: key);

  final TravelDestination destination;
  final ShapeBorder shape;

  @override
  _SelectableTravelDestinationItemState createState() =>
      _SelectableTravelDestinationItemState();
}

class _SelectableTravelDestinationItemState
    extends State<SelectableTravelDestinationItem> {
  // This height will allow for all the Card's content to fit comfortably within the card.
  static const height = 298.0;
  var _isSelected = false;

  @override
  Widget build(BuildContext context) {
    final ColorScheme colorScheme = Theme.of(context).colorScheme;

    return SafeArea(
      top: false,
      bottom: false,
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: Column(
          children: [
            SizedBox(
              height: height,
              child: Card(
                // This ensures that the Card's children (including the ink splash) are clipped correctly.
                clipBehavior: Clip.antiAlias,
                shape: widget.shape,
                child: InkWell(
                  onLongPress: () {
                    print('Selectable card state changed');
                    setState(() {
                      _isSelected = !_isSelected;
                    });
                  },
                  // Generally, material cards use onSurface with 12% opacity for the pressed state.
                  splashColor: colorScheme.onSurface.withOpacity(0.12),
                  // Generally, material cards do not have a highlight overlay.
                  highlightColor: Colors.transparent,
                  child: Stack(
                    children: [
                      Container(
                        color: _isSelected
                            // Generally, material cards use primary with 8% opacity for the selected state.
                            // See: https://material.io/design/interaction/states.html#anatomy
                            ? colorScheme.primary.withOpacity(0.08)
                            : Colors.transparent,
                      ),
                      TravelDestinationContent(destination: widget.destination),
                      Align(
                        alignment: Alignment.topRight,
                        child: Padding(
                          padding: const EdgeInsets.all(8),
                          child: Icon(
                            Icons.check_circle,
                            color: _isSelected
                                ? colorScheme.primary
                                : Colors.transparent,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class TravelDestinationContent extends StatelessWidget {
  const TravelDestinationContent({Key key, @required this.destination})
      : assert(destination != null),
        super(key: key);

  final TravelDestination destination;

  @override
  Widget build(BuildContext context) {
    final ThemeData theme = Theme.of(context);
    //final TextStyle titleStyle =
     //   theme.textTheme.headline5.copyWith(color: Colors.white);
    //final TextStyle descriptionStyle = theme.textTheme.subtitle1;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          height: 184,
          child: Stack(
            children: [
              Positioned.fill(
                // In order to have the ink splash appear above the image, you
                // must use Ink.image. This allows the image to be painted as
                // part of the Material and display ink effects above it. Using
                // a standard Image will obscure the ink splash.
                child: Ink.image(
                  image: AssetImage(
                    destination.assetName,
                    package: destination.assetPackage,
                  ),
                  fit: BoxFit.cover,
                  child: Container(),
                ),
              ),
              Positioned(
                bottom: 16,
                left: 16,
                right: 16,
                child: FittedBox(
                  fit: BoxFit.scaleDown,
                  alignment: Alignment.centerLeft,
                  child: Text(
                    destination.title
                  ),
                ),
              ),
            ],
          ),
        ),
        // Description and share/explore buttons.
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
          child: DefaultTextStyle(
            style: TextStyle(fontSize: 36, color: Colors.blue),
            softWrap: false,
            overflow: TextOverflow.ellipsis,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // This array contains the three line description on each card
                // demo.
                Padding(
                  padding: const EdgeInsets.only(bottom: 8),
                  child: Text(
                    destination.description
                  ),
          ),
                Text(destination.city),
                Text(destination.location),
              ],
            ),
          ),
        ),
      ],
    );
  }
}
// END



/*
Card(
                    shape: RoundedRectangleBorder(
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
                          labelText: 'Food Runner Id: $id',
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Email: $email',
                          ),
                        ),
                        TextField(
                          decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Mobile: $mobile',
                          ),
                        ),
                        ButtonTheme.bar(
                          child: ButtonBar(
                            children: <Widget>[
                              FlatButton(
                                child: const Text('Edit', style: TextStyle(color: Colors.white)),
                                onPressed: () {},
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
                  )

                  [
                Container(
                    width: 200,
                    child:
                )
          ]

                  */