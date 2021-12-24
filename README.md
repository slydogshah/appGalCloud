#Jen Network: Open Source Food Recovery Network, by AppGal Labs

![This is an image](https://jen.appgallabs.io/images/networkLogo.png)


## Introduction
Jen Network is an Open Source Software Community and a socially aware Community focused on assistance with food insecurity.

## Solution
Jen Network is an Uber-like Network for volunteer FoodRunners with an IOS or Android App

FoodRunners pick up surplus food from restaurants, cafeterias, parties, etc.
They deliver it to a participating Organization such as a Church, a Food Pantry, etc.

The Network is designed to assist those with food insecurity.

## Cloud - located under the directory 'appgal-cloud'
The project uses Quarkus,the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using 
````
./mvnw package -DskipTests
````

It produces the `appgal-cloud-v1-runner.jar` file in the `/target` directory.

The application is now runnable using

````
java -jar target/appgal-cloud-v1-runner.jar
````

Application will be available at
````
http://localhost:8080
````

## Mobile App - located under the directory 'jennetapp'

The mobile app is a Flutter application that runs natively both
on Android and IOS. To build this app use the following command

IOS
```
flutter clean && flutter build ios -t lib/main_dev.dart
```

Android
```
flutter clean && flutter build apk -t lib/main_dev.dart
```

To run the app
```
flutter clean && flutter run
```

## Web App - located under the directory 'appgal-cloud/src/main/webapp'

This is a React App 

To run the web application

```
npm install && npm start
```

Application will be available at

```
http://localhost:3000/
```