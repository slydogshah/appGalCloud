import 'dart:convert';
import 'dart:io';
import 'package:app/src/model/schedulePickupNotification.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('Load a file', () async {
    final file = new File('test_resources/schedulePickupNotification.json');
    SchedulePickupNotification schedulePickupNotification = SchedulePickupNotification.fromJson(jsonDecode(await file.readAsString()));
    print(schedulePickupNotification);
    
    /*final contacts = json['contacts'];

    final seth = contacts.first;
    expect(seth['id'], 1);
    expect(seth['name'], 'Seth Ladd');

    final eric = contacts.last;
    expect(eric['id'], 2);
    expect(eric['name'], 'Eric Seidel');*/
  });
}