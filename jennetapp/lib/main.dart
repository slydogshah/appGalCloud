import 'dart:io';
import 'package:app/src/background/locationUpdater.dart';
import 'package:app/src/context/activeSession.dart';
import 'package:app/src/context/securityToken.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunnerLocation.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/activeNetworkRestClient.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:app/src/ui/app.dart';
import 'package:app/src/ui/foodRunner.dart';
import 'package:app/src/ui/tasksNotFound.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:timezone/data/latest.dart' as tz;
import 'package:timezone/timezone.dart' as tz;
import 'notifications.dart';

class MyHttpOverrides extends HttpOverrides{
  @override
  HttpClient createHttpClient(SecurityContext context){
    HttpClient client = super.createHttpClient(context)
      ..badCertificateCallback = (X509Certificate cert, String host, int port)=> true;

    return client;
  }
}

/*Future<void> main() async {
  //Launch the App
  HttpOverrides.global = new MyHttpOverrides();

  WidgetsFlutterBinding.ensureInitialized();

  print("********BHENCHOD:20*******");
  await Future.delayed(Duration(seconds: 20));

  String env = "dev";
  Future<Map<String,dynamic>> config = UrlFunctions.getConfig(env);
  config.then((jsonMap) {
    UrlFunctions.getInstance().androidApiUrl = jsonMap['androidApiUrl'];
    UrlFunctions.getInstance().iosApiUrl = jsonMap['iosApiUrl'];
    //print("APP_URL: "+UrlFunctions.getInstance().iosApiUrl);
    launchApp();
  });
}*/


void main(String env) {
  //Launch the App
  HttpOverrides.global = new MyHttpOverrides();

  WidgetsFlutterBinding.ensureInitialized();

  //await Future.delayed(Duration(seconds: 10));

  Future<Map<String,dynamic>> config = UrlFunctions.getConfig(env);
  config.then((jsonMap) {
    UrlFunctions.getInstance().androidApiUrl = jsonMap['androidApiUrl'];
    UrlFunctions.getInstance().iosApiUrl = jsonMap['iosApiUrl'];
    //print("APP_URL: "+UrlFunctions.getInstance().iosApiUrl);
    launchApp();
  });
}


Future<void> _configureLocalTimeZone() async {
  tz.initializeTimeZones();
  final String timeZoneName =
  await platform.invokeMethod<String>('getTimeZoneName');
  tz.setLocalLocation(tz.getLocation(timeZoneName));
}

void launchApp()
{
  Future<Map<String,dynamic>> future = ActiveSession.getInstance().readCredentials();
  future.then((credentials){
    String email = credentials['email'];
    String password = credentials['password'];

    if(email == null || password == null) {
      runApp(new JenNetworkApp());
      return;
    }

    Future<FoodRunnerLocation> locationFuture = LocationUpdater.getLocation();
    locationFuture.then((location){
      double latitude = credentials['latitude'];
      double longitude = credentials['longitude'];
      //print(email);
      //print(password);
      //print(latitude);
      //print(longitude);
      if(email == null || password == null) {
        runApp(new JenNetworkApp());
      }
      else
      {
        autoLogin(email,password,latitude,longitude);
      }
    });
  });
}

//TODO
void autoLogin(String email,String password,double latitude,double longitude) {
  AuthCredentials credentials = new AuthCredentials();
  credentials.email = email;
  credentials.password = password;
  credentials.latitude = latitude;
  credentials.longitude = longitude;
  ProfileRestClient profileRestClient = new ProfileRestClient();
  Future<Map<String, dynamic>> future = profileRestClient.login(credentials);
  future.then((json) {
    if(json['statusCode'] != 200)
    {
      runApp(new JenNetworkApp());
    }
    else {
      Profile foodRunner = Profile.fromJson(json);

      ActiveSession activeSession = ActiveSession.getInstance();
      activeSession.setProfile(foodRunner);
      activeSession.foodRunner.offlineCommunitySupport =
      json["offlineCommunitySupport"];
      ActiveSession.getInstance().securityToken = new SecurityToken(foodRunner.email, foodRunner.bearerToken);

      ActiveNetworkRestClient client = new ActiveNetworkRestClient();
      Future<Map<String, List<FoodRecoveryTransaction>>> future = client
          .getFoodRecoveryTransaction(foodRunner.email);
      future.then((txs) {
        if(!txs['pending'].isEmpty || !txs['inProgress'].isEmpty) {
          runApp(new FoodRunnerApp(txs));
        }else{
          runApp(TasksNotFound());
        }
      }).catchError((e) {
        runApp(new JenNetworkApp());
      });
    }
  });
}




/*import 'package:flutter/material.dart';

Future<void> main() async {
  print("********BHENCHOD:5*******");
  await Future.delayed(Duration(seconds: 2));

  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  void _incrementCounter() {
    setState(() {
      // This call to setState tells the Flutter framework that something has
      // changed in this State, which causes it to rerun the build method below
      // so that the display can reflect the updated values. If we changed
      // _counter without calling setState(), then the build method would not be
      // called again, and so nothing would appear to happen.
      _counter++;
    });
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: Column(
          // Column is also a layout widget. It takes a list of children and
          // arranges them vertically. By default, it sizes itself to fit its
          // children horizontally, and tries to be as tall as its parent.
          //
          // Invoke "debug painting" (press "p" in the console, choose the
          // "Toggle Debug Paint" action from the Flutter Inspector in Android
          // Studio, or the "Toggle Debug Paint" command in Visual Studio Code)
          // to see the wireframe for each widget.
          //
          // Column has various properties to control how it sizes itself and
          // how it positions its children. Here we use mainAxisAlignment to
          // center the children vertically; the main axis here is the vertical
          // axis because Columns are vertical (the cross axis would be
          // horizontal).
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}*/
