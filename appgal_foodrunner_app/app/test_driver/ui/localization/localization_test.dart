import 'dart:convert';

import 'package:flutter/src/material/material_localizations.dart';
import 'package:test/test.dart';

import 'localization.dart';

void main() {
  group('Home Screen Test', () {
  FlutterDriver driver;
    setUpAll(() async {
    // Connects to the app
    driver = await FlutterDriver.connect();
        });
        tearDownAll(() async {
        if (driver != null) {
        // Closes the connection
        driver.close();
            }
            });
              test('verify the text on home screen', () async {
              MaterialLocalizations localization = Localization.of(null);
              print(localization);
            });
            });
          }
          
          mixin FlutterDriver {
          void close() {}
    
      static connect() {}
  }