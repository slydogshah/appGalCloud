import UIKit
import Flutter
import GoogleMaps
import UserNotifications

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
    GMSServices.provideAPIKey("AIzaSyAAGiFi2Vm2__zWssAL6sgghY5UEPTdlDs")

    let calendar = Calendar(identifier: .gregorian)
    let components = calendar.dateComponents(in: .current, from: Date())
    let newComponents = DateComponents(calendar: calendar, timeZone: .current, 
    month: components.month, day: components.day, hour: components.hour, minute: components.minute)
 
    let trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: false)

    let content = UNMutableNotificationContent()
    content.title = "Tutorial Reminder"
    content.body = "Just a reminder to read your tutorial over at appcoda.com!"
    content.sound = UNNotificationSound.default()

    let request = UNNotificationRequest(identifier: "textNotification", content: content, trigger: trigger)

    UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
    UNUserNotificationCenter.current().add(request) {(error) in
      if let error = error {
          print("Uh oh! We had an error: \(error)")
      }
    }

    let selectedDate = Date()
    let delegate = UIApplication.shared.delegate as? AppDelegate
    delegate?.scheduleNotification(at: selectedDate)

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

  func scheduleNotification(at date: Date) 
  {
    //let calendar = Calendar(identifier: .gregorian)
    //let components = calendar.dateComponents(in: .current, from: date)
    //let newComponents = DateComponents(calendar: calendar, timeZone: .current, 
    //month: components.month, day: components.day, hour: components.hour, minute: components.minute)
  }
}
