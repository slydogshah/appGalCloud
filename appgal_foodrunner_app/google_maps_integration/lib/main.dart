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
import 'package:google_maps_integration/constants.dart';
import 'package:google_maps_integration/data/gallery_options.dart';
import 'package:google_maps_integration/l10n/gallery_localizations.dart';
import 'package:google_maps_integration/pages/backdrop.dart';
import 'package:google_maps_integration/pages/splash.dart';
import 'package:google_maps_integration/src/model/profile.dart';
import 'package:google_maps_integration/themes/gallery_theme_data.dart';

import './src/context/activeSession.dart';

void main() {
  /*setOverrideForDesktop();
  GoogleFonts.config.allowHttp = false;
  ActiveSession.getInstance();
  runApp(GalleryApp());*/
  ActiveSession session = ActiveSession.getInstance();
  Profile profile = session.getProfile();
  do{
    print("DIE_GOD_DIE");
  }while(profile == null);

  print("*bull*");
  print(profile.toString());
  print("*bull*");
}

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
