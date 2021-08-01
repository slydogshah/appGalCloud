import UIKit
import Flutter
import UserNotifications

import Firebase

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    let gcmMessageIDKey = "gcm.message_id"
    var email = ""
    
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    if #available(iOS 10.0, *) {
          UNUserNotificationCenter.current().delegate = self as UNUserNotificationCenterDelegate
        }

        let controller : FlutterViewController = window?.rootViewController as! FlutterViewController


        let channel = FlutterMethodChannel(name: "dexterx.dev/flutter_local_notifications_example",
                                            binaryMessenger: controller.binaryMessenger)
        channel.setMethodCallHandler({
            (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
            if ("getTimeZoneName" == call.method) {
                result(TimeZone.current.identifier)
            }
        })


        //Initialize Firebase
        FirebaseApp.configure()
        let pushNotificationChannel = FlutterMethodChannel(name:"appgallabs.io/push_notifications",binaryMessenger: controller.binaryMessenger)
        pushNotificationChannel.setMethodCallHandler({
                    (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
                    if ("setEmail" == call.method) {
                        self.email = call.arguments as! String
                        print("*******DELEGATE_EMAIL********")
                        print(self.email);
                        

                        // [START set_messaging_delegate]
                        Messaging.messaging().delegate = self
                        // [END set_messaging_delegate]
                        
                        // Register for remote notifications. This shows a permission dialog on first run, to
                        // show the dialog at a more appropriate time move this registration accordingly.
                        // [START register_for_notifications]
                        if #available(iOS 10.0, *) {
                          // For iOS 10 display notification (sent via APNS)
                          UNUserNotificationCenter.current().delegate = self

                          let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
                          UNUserNotificationCenter.current().requestAuthorization(
                            options: authOptions,
                            completionHandler: { _, _ in }
                          )
                        } else {
                          let settings: UIUserNotificationSettings =
                            UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
                          application.registerUserNotificationSettings(settings)
                        }

                        application.registerForRemoteNotifications()
                        
                        self.subscribe()
                    }
        })


    GeneratedPluginRegistrant.register(with: self)
    NSLog("LAUNCH_COMPLETE");

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
    
    override func application(_ application: UIApplication,
                     didReceiveRemoteNotification userInfo: [AnyHashable: Any]) {
      // If you are receiving a notification message while your app is in the background,
      // this callback will not be fired till the user taps on the notification launching the application.
      // TODO: Handle data of notification

      // With swizzling disabled you must let Messaging know about the message, for Analytics
      //Messaging.messaging().appDidReceiveMessage(userInfo)

      // Print message ID.
      if let messageID = userInfo[gcmMessageIDKey] {
        print("Message ID: \(messageID)")
      }

      // Print full message.
      print(userInfo)
    }
    
    // [START receive_message]
    override func application(_ application: UIApplication,
                     didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult)
                       -> Void) {
      // If you are receiving a notification message while your app is in the background,
      // this callback will not be fired till the user taps on the notification launching the application.
      // TODO: Handle data of notification

      // With swizzling disabled you must let Messaging know about the message, for Analytics
      // Messaging.messaging().appDidReceiveMessage(userInfo)

      // Print message ID.
      if let messageID = userInfo[gcmMessageIDKey] {
        print("Message ID: \(messageID)")
      }

      // Print full message.
      print(userInfo)

      completionHandler(UIBackgroundFetchResult.newData)
    }
    
    override func application(_ application: UIApplication,
                     didFailToRegisterForRemoteNotificationsWithError error: Error) {
      print("Unable to register for remote notifications: \(error.localizedDescription)")
    }

    // This function is added here only for debugging purposes, and can be removed if swizzling is enabled.
    // If swizzling is disabled then this function must be implemented so that the APNs token can be paired to
    // the FCM registration token.
    override func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
      print("APNs token retrieved: \(deviceToken)")

      // With swizzling disabled you must set the APNs token here.
      Messaging.messaging().apnsToken = deviceToken
    }
    
    // Receive displayed notifications for iOS 10 devices.
      override func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions)
                                  -> Void) {
      let userInfo = notification.request.content.userInfo

      // With swizzling disabled you must let Messaging know about the message, for Analytics
      // Messaging.messaging().appDidReceiveMessage(userInfo)

      // [START_EXCLUDE]
      // Print message ID.
      if let messageID = userInfo[gcmMessageIDKey] {
        print("Message ID: \(messageID)")
      }
      // [END_EXCLUDE]

      // Print full message.
      print(userInfo)

      // Change this to your preferred presentation option
      completionHandler([[.alert, .sound]])
    }

      override func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
      let userInfo = response.notification.request.content.userInfo

      // [START_EXCLUDE]
      // Print message ID.
      if let messageID = userInfo[gcmMessageIDKey] {
        print("Message ID: \(messageID)")
      }
      // [END_EXCLUDE]

      // With swizzling disabled you must let Messaging know about the message, for Analytics
      // Messaging.messaging().appDidReceiveMessage(userInfo)

      // Print full message.
      print(userInfo)

      completionHandler()
    }
    
    func subscribe() {
        /*NotificationCenter.default.addObserver(
          self,
          selector: #selector(displayFCMToken(notification:)),
          name: Notification.Name("FCMToken"),
          object: nil
        )*/
        
        let token = Messaging.messaging().fcmToken
        print("FCM token: \(token ?? "")")
        // [END log_fcm_reg_token

        // [START log_iid_reg_token]
        Messaging.messaging().token { token, error in
          if let error = error {
            print("Error fetching remote FCM registration token: \(error)")
          } else if let token = token {
            print("Remote instance ID token: \(token)")
            print("Remote FCM registration token: \(token)")
            self.displayFCMToken(token:token)
          }
        }
        
        
      // [START subscribe_topic]
      Messaging.messaging().subscribe(toTopic: "weather") { error in
        print("Subscribed to weather topic")
      }
      // [END subscribe_topic]
    }
    
    @objc func displayFCMToken(token: String) {
        //TODO clean
        print("***********************************************")
        print("Received FCM token: \(token)")
        print(self.email);
        print("***********************************************")
        
        if(self.email.isEmpty)
        {
            print("EMAIL_NOT_SET_YET")
            return
        }
        
        struct RegisterPushModel: Codable {
            var pushToken: String
            var email: String
        }
        
        
        //TODO
        // Prepare URL
        let url = URL(string: "https://appgal-cloud-do2cwgwhja-uc.a.run.app/activeNetwork/registerPush")
        guard let requestUrl = url else { fatalError() }

        // Prepare URL Request Object
        var request = URLRequest(url: requestUrl)
        request.httpMethod = "POST"
        
        // Set HTTP Request Header
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
         
        
        let json = RegisterPushModel(pushToken: token, email: self.email)
        do{
            let jsonData = try JSONEncoder().encode(json)
            request.httpBody = jsonData
        }
        catch{
            print("ERROR_DURING_JSON_ENCODE")
            return
        }

        // Perform HTTP Request
        let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
                
                // Check for Error
                if let error = error {
                    print("Error took place \(error)")
                    return
                }
         
                // Convert HTTP Response Data to a String
                if let data = data, let dataString = String(data: data, encoding: .utf8) {
                    print("Token Registration data:\n \(dataString)")
                }
        }
        task.resume()
      }
}

// [END ios_10_message_handling]

extension AppDelegate: MessagingDelegate {
  // [START refresh_token]
  func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
    print("Firebase registration token: \(String(describing: fcmToken))")

    let dataDict: [String: String] = ["token": fcmToken ?? ""]
    NotificationCenter.default.post(
      name: Notification.Name("FCMToken"),
      object: nil,
      userInfo: dataDict
    )
    // TODO: If necessary send token to application server.
    // Note: This callback is fired at each app startup and whenever a new token is generated.
  }

  // [END refresh_token]
}

