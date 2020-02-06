
<script type="text/javascript" src="https://platform.linkedin.com/badges/js/profile.js" async defer></script>

#Food Recovery Optimization Repository, by AppGal Labs
----
<div class="LI-profile-badge"  data-version="v1" data-size="medium" data-locale="en_US" data-type="horizontal" data-theme="light" data-vanity="sohil-shah-28052659"><a class="LI-simple-link" href='https://www.linkedin.com/in/sohil-shah-28052659?trk=profile-badge'>Sohil Shah</a></div>

<div class="LI-profile-badge"  data-version="v1" data-size="medium" data-locale="en_US" data-type="horizontal" data-theme="light" data-vanity="catherine-balcom-667742187"><a class="LI-simple-link" href='https://www.linkedin.com/in/catherine-balcom-667742187?trk=profile-badge'>Catherine Balcom</a></div>


# appGalCloud
The Cloud App for the AppGal Food Recovery Optimization Network


Getting Started
----

> git clone https://github.com/slydogshah/appGalCloud.git
>
>cd appGalCloud/appgal-cloud
>
>mvn clean package && java -jar target/*-runner.jar
>
>curl http://localhost:8080/microservice/
>
>Server Output
>----
>2020-02-01 12:51:39,713 WARN  [io.qua.net.run.NettyRecorder] (Thread-1) Localhost lookup took more than one second, you need to add a /etc/hosts entry to improve Quarkus startup time. See https://thoeni.io/post/macos-sierra-java/ for details.
 2020-02-01 12:51:39,750 INFO  [io.quarkus] (main) appgal-cloud 1.0.0-SNAPSHOT (running on Quarkus 1.1.1.Final) started in 15.415s. Listening on: http://0.0.0.0:8080
 2020-02-01 12:51:39,753 INFO  [io.quarkus] (main) Profile prod activated. 
 2020-02-01 12:51:39,753 INFO  [io.quarkus] (main) Installed features: [cdi, resteasy]

>
>Microservice Response
>----
>babyboy@Babys-MacBook-Pro appgal-cloud % curl http://localhost:8080/microservice/
 {"oid":"93692874-268f-49ad-aaf3-c8f3884bce61","message":"HELLO_TO_HUMANITY"}%                                                                                                                                                                          babyboy@Babys-MacBook-Pro appgal-cloud %
>

Getting Started with Food Runner App based on Dart/Flutter
----

> cd appGalCloud/appgal-foodrunner-app
>
>Run the App
>----
> dart microserviceClient.dart
>
>Output
>----
> babyboy@Babys-MBP appgal-foodrunner-app % dart microserviceClient.dart 
  {"oid":"89b9b5b4-4be0-42c1-848a-47282dc1fbfe","message":"HELLO_TO_HUMANITY"}
