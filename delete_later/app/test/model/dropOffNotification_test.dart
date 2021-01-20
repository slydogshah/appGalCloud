import 'dart:convert';
import 'dart:io';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('Load a file', () async {
    final file = new File('test_resources/dropOffNotification.json');
    DropOffNotification dropOffNotification = DropOffNotification.fromJson(jsonDecode(await file.readAsString()));
    print(dropOffNotification);
    /*final contacts = json['contacts'];

    final seth = contacts.first;
    expect(seth['id'], 1);
    expect(seth['name'], 'Seth Ladd');

    final eric = contacts.last;
    expect(eric['id'], 2);
    expect(eric['name'], 'Eric Seidel');*/
  });
}