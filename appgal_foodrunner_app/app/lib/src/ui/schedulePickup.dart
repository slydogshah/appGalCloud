import 'package:flutter/cupertino.dart';
import 'package:intl/intl.dart';
import 'dart:io';

import '../model/foodRunner.dart';
import '../model/sourceOrg.dart';

import 'package:flutter/material.dart';

import 'package:app/src/model/sourceOrg.dart';

import '../rest/activeNetworkRestClient.dart';
import 'package:app/src/model/schedulePickupNotification.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/foodRunner.dart';

import 'package:app/data/gallery_options.dart';

class SchedulePickup extends StatefulWidget {
  SourceOrg sourceOrg;
  SchedulePickup(SourceOrg sourceOrg)
  {
      this.sourceOrg = sourceOrg;
  }

  @override
  SchedulePickupState createState() => SchedulePickupState(sourceOrg);
}

class SchedulePickupState extends State<SchedulePickup> {
  SourceOrg sourceOrg;
 SchedulePickupState(SourceOrg sourceOrg)
  {
    this.sourceOrg = sourceOrg;
  }


  Duration timer = const Duration();

  // Value that is shown in the date picker in date mode.
  DateTime date = DateTime.now();

  // Value that is shown in the date picker in time mode.
  DateTime time = DateTime.now();

  // Value that is shown in the date picker in dateAndTime mode.
  DateTime dateTime = DateTime.now();

  void _showDemoPicker({
    @required BuildContext context,
    @required Widget child,
  }) {
    final themeData = CupertinoTheme.of(context);
    final dialogBody = CupertinoTheme(
      data: themeData,
      child: child,
    );

    showCupertinoModalPopup<void>(
      context: context,
      builder: (context) => dialogBody,
    );
  }

  Widget _buildDatePicker(BuildContext context) {
    return GestureDetector(
      onTap: () {
        _showDemoPicker(
          context: context,
          child: _BottomPicker(
            child: CupertinoDatePicker(
              backgroundColor:
                  CupertinoColors.systemBackground.resolveFrom(context),
              mode: CupertinoDatePickerMode.date,
              initialDateTime: date,
              onDateTimeChanged: (newDateTime) {
                setState(() => date = newDateTime);
              },
            ),
          ),
        );
      },
      child: _Menu(children: [
        Text("Date"),
        Text(
          DateFormat.yMMMMd().format(date),
          style: const TextStyle(color: CupertinoColors.inactiveGray),
        ),
      ]),
    );
  }

  Widget _buildTimePicker(BuildContext context) {
    return GestureDetector(
      onTap: () {
        _showDemoPicker(
          context: context,
          child: _BottomPicker(
            child: CupertinoDatePicker(
              backgroundColor:
                  CupertinoColors.systemBackground.resolveFrom(context),
              mode: CupertinoDatePickerMode.time,
              initialDateTime: time,
              onDateTimeChanged: (newDateTime) {
                setState(() => time = newDateTime);
              },
            ),
          ),
        );
      },
      child: _Menu(
        children: [
          Text("Time"),
          Text(
            DateFormat.jm().format(time),
            style: const TextStyle(color: CupertinoColors.inactiveGray),
          ),
        ],
      ),
    );
  }

  Widget _buildDateAndTimePicker(BuildContext context) {
    return GestureDetector(
      onTap: () {
        _showDemoPicker(
          context: context,
          child: _BottomPicker(
            child: CupertinoDatePicker(
              backgroundColor:
                  CupertinoColors.systemBackground.resolveFrom(context),
              mode: CupertinoDatePickerMode.dateAndTime,
              initialDateTime: dateTime,
              onDateTimeChanged: (newDateTime) {
                setState(() => dateTime = newDateTime);
              },
            ),
          ),
        );
      },
      child: _Menu(
        children: [
          Text("Date"),
          Flexible(
            child: Text(
              DateFormat.yMMMd().add_jm().format(dateTime),
              style: const TextStyle(color: CupertinoColors.inactiveGray),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCountdownTimerPicker(BuildContext context) {
    return GestureDetector(
      onTap: () {
        _showDemoPicker(
          context: context,
          child: _BottomPicker(
            child: CupertinoTimerPicker(
              backgroundColor:
                  CupertinoColors.systemBackground.resolveFrom(context),
              initialTimerDuration: timer,
              onTimerDurationChanged: (newTimer) {
                setState(() => timer = newTimer);
              },
            ),
          ),
        );
      },
      child: _Menu(
        children: [
          Text("Time"),
          Text(
            '${timer.inHours}:'
            '${(timer.inMinutes % 60).toString().padLeft(2, '0')}:'
            '${(timer.inSeconds % 60).toString().padLeft(2, '0')}',
            style: const TextStyle(color: CupertinoColors.inactiveGray),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
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
                      ],
                    ),
                  );
    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        automaticallyImplyLeading: false,
        middle: Text("Schedule Pickup"),
      ), 
      child: DefaultTextStyle(
        style: CupertinoTheme.of(context).textTheme.textStyle,
        child: ListView(
          children: [
            const SizedBox(height: 32),
            card,
            _buildDatePicker(context),
            _buildTimePicker(context),
            _buildDateAndTimePicker(context),
            _buildCountdownTimerPicker(context),
            Center(
                  child: RaisedButton(
                    child: Text("Schedule Pickup"),
                    onPressed: () 
                    {
                      ActiveSession activeSession = ActiveSession.getInstance();
                      FoodRunner foodRunner = new FoodRunner(activeSession.profile);
                      SchedulePickupNotification schedulePickupNotification = new SchedulePickupNotification(sourceOrg, foodRunner, null);
                      schedulePickup(context, schedulePickupNotification);
                    }
                  )
                ),
          ],
        ),
      ),
    );
  }

  Future<void> _showDemoDialog<T>({BuildContext context, Widget child}) async {
    child = ApplyTextOptions(
      child: Theme(
        data: Theme.of(context),
        child: child,
      ),
    );
    final value = await showDialog<T>(
      context: context,
      builder: (context) => child,
    );
    // The value passed to Navigator.pop() or null.
    /*if (value != null && value is String) {
      _scaffoldKey.currentState.hideCurrentSnackBar();
      _scaffoldKey.currentState.showSnackBar(SnackBar(
        content:
            Text(GalleryLocalizations.of(context).dialogSelectedOption(value)),
      ));
    }*/
  }

  void _showAlertDialog(BuildContext context) {
    final theme = Theme.of(context);
    final dialogTextStyle = theme.textTheme.subtitle1
        .copyWith(color: theme.textTheme.caption.color);
    _showDemoDialog<String>(
      context: context,
      child: AlertDialog(
        content: Text(
          "Discard",
          style: dialogTextStyle,
        ),
        actions: [
          //_DialogButton(text: "Cancel",
          //_DialogButton(text: "Discard",
        ],
      ),
    );
  }

  void schedulePickup (BuildContext context, SchedulePickupNotification notification) {
    // set up the SimpleDialog
    SimpleDialog dialog = SimpleDialog(
      children: [CupertinoActivityIndicator()]
    );

    // show the dialog
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return dialog;
      },
    );

    ActiveNetworkRestClient activeNetworkRestClient = ActiveNetworkRestClient();
    Future<String> future = activeNetworkRestClient.sendSchedulePickupNotification(notification);
    future.then((response){
      print("*********RESPONSE****************");
      print(response);
      print("*************************");

      Navigator.of(context, rootNavigator: true).pop();

      _showAlertDialog(context);
    });
  }
}

class _BottomPicker extends StatelessWidget {
  const _BottomPicker({
    Key key,
    @required this.child,
  })  : assert(child != null),
        super(key: key);

  final Widget child;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 216,
      padding: const EdgeInsets.only(top: 6),
      margin: EdgeInsets.only(
        bottom: MediaQuery.of(context).viewInsets.bottom,
      ),
      color: CupertinoColors.systemBackground.resolveFrom(context),
      child: DefaultTextStyle(
        style: TextStyle(
          color: CupertinoColors.label.resolveFrom(context),
          fontSize: 22,
        ),
        child: GestureDetector(
          // Blocks taps from propagating to the modal sheet and popping.
          onTap: () {},
          child: SafeArea(
            top: false,
            child: child,
          ),
        ),
      ),
    );
  }
}

class _Menu extends StatelessWidget {
  const _Menu({
    Key key,
    @required this.children,
  })  : assert(children != null),
        super(key: key);

  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        border: Border(
          top: BorderSide(color: CupertinoColors.inactiveGray, width: 0),
          bottom: BorderSide(color: CupertinoColors.inactiveGray, width: 0),
        ),
      ),
      height: 44,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: children,
        ),
      ),
    );
  }
}

