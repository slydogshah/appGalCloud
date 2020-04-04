// Copyright 2019 The Flutter team. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:io';
import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart' show timeDilation;
import 'package:flutter_localized_locales/flutter_localized_locales.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:app/constants.dart';
import 'package:app/data/gallery_options.dart';
import 'package:app/l10n/gallery_localizations.dart';
import 'package:app/pages/backdrop.dart';
import 'package:app/pages/splash.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/themes/gallery_theme_data.dart';

import './src/context/activeSession.dart';

void main() {
  print("I_LOVE_YOU_DHONI_FAMILY_YOU_ARE_ALWAYS_MY_CAPTAIN_BLESS_YOU_AND_YOUR_SWEET_WIFE_AND_DAUGHTER_ARE_ALWAYS_HAPPY_HAPPY");
  ActiveSession session = ActiveSession.getInstance();
  session.activate();

  setOverrideForDesktop();
  GoogleFonts.config.allowHttp = false;
  ActiveSession.getInstance();
  runApp(GalleryApp());
}

/*class ThreadDemo
{
  ThreadDemo()
  {

  }

  Future runThreads(final ActiveSession session) async {
    print("Threads (interleaved execution)");
    print("----------------");
    var threads = <Thread>[];
    var numOfThreads = 3;
    var count = 100;
    for (var i = 0; i < numOfThreads; i++) {
      var name = new String.fromCharCode(65 + i);
      var thread = new Thread(() async {
        for (var j = 0; j < count; j++) {
          //await new Future.value();
          //print("$name: $j");
          Profile profile = session.getProfile();
          print("***HERE****");
          print(profile.toString());
          print("****HERE***");
        }
      });

      threads.add(thread);
      await thread.start();
    }

    for (var i = 0; i < numOfThreads; i++) {
      await threads[i].join();
    }
  }
}*/

void setOverrideForDesktop() {
  if (kIsWeb) return;

  if (Platform.isMacOS) {
    debugDefaultTargetPlatformOverride = TargetPlatform.iOS;
  } else if (Platform.isLinux || Platform.isWindows) {
    debugDefaultTargetPlatformOverride = TargetPlatform.android;
  } else if (Platform.isFuchsia) {
    debugDefaultTargetPlatformOverride = TargetPlatform.fuchsia;
  }
}

class GalleryApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ModelBinding(
      initialModel: GalleryOptions(
        themeMode: ThemeMode.system,
        textScaleFactor: systemTextScaleFactorOption,
        customTextDirection: CustomTextDirection.localeBased,
        locale: null,
        timeDilation: timeDilation,
        platform: defaultTargetPlatform,
      ),
      child: Builder(
        builder: (context) {
          return MaterialApp(
            title: 'Gallery',
            debugShowCheckedModeBanner: false,
            themeMode: GalleryOptions.of(context).themeMode,
            theme: GalleryThemeData.lightThemeData.copyWith(
              platform: GalleryOptions.of(context).platform,
            ),
            darkTheme: GalleryThemeData.darkThemeData.copyWith(
              platform: GalleryOptions.of(context).platform,
            ),
            localizationsDelegates: [
              ...GalleryLocalizations.localizationsDelegates,
              LocaleNamesLocalizationsDelegate()
            ],
            supportedLocales: GalleryLocalizations.supportedLocales,
            locale: GalleryOptions.of(context).locale,
            localeResolutionCallback: (locale, supportedLocales) {
              deviceLocale = locale;
              return locale;
            },
            home: ApplyTextOptions(
              child: SplashPage(
                child: AnimatedBackdrop(),
              ),
            ),
          );
        },
      ),
    );
  }
}
