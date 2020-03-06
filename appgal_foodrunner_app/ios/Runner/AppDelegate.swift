import UIKit
import Flutter
import GoogleMaps

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)

    do {
      // Add the following line, with your API key
      //[GMSServices providerAPIKey: @"AIzaSyAAGiFi2Vm2__zWssAL6sgghY5UEPTdlDs"];
      GMSServices.provideAPIKey("AIzaSyAAGiFi2Vm2__zWssAL6sgghY5UEPTdlDs");
    } catch error {
         print("BLAH.....BLAH...");   
    }

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
