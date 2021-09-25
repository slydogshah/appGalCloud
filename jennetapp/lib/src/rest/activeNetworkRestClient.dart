import 'dart:io';

import 'package:app/src/context/activeSession.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/model/scheduleDropOffNotification.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:app/src/model/schedulePickupNotification.dart';
import 'package:location/location.dart';

import '../model/schedulePickupNotification.dart';
import 'cloudBusinessException.dart';

class ActiveNetworkRestClient
{
  Future<Map<String,List<FoodRecoveryTransaction>>> getFoodRecoveryTransaction(String email) async
  {
    Map<String,List<FoodRecoveryTransaction>> txs = new Map();
    var response;
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"tx/recovery/foodRunner/?email="+email;


    try {
      response = await http.get(Uri.parse(remoteUrl),headers: {
        "Principal":ActiveSession.getInstance().securityToken.email,
        "Bearer": ActiveSession.getInstance().securityToken.bearerToken,
      },);

      //print(response.body);
    }
    catch (e) {
      //print(e);
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    Map<String,dynamic> error = UrlFunctions.handleError(null, response);
    if(error != null){
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    print(response.body);

    Map<String,dynamic> object = jsonDecode(response.body);

    if(object['pending'] != null) {
      Iterable l = object['pending'];
      List<FoodRecoveryTransaction> pending = [];
      for (Map<String, dynamic> tx in l) {
        FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
        pending.add(local);
      }
      txs['pending'] = pending;
    }
    else{
      txs ['pending'] = [];
    }


    if(object['inProgress'] != null) {
      Iterable l = object['inProgress'];
      List<FoodRecoveryTransaction> inProgress = [];
      for (Map<String, dynamic> tx in l) {
        FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
        inProgress.add(local);
      }
      txs['inProgress'] = inProgress;
    }
    else{
      txs ['inProgress'] = [];
    }

    return txs;
  }

  Future<String> sendLocationUpdate(LocationData locationData) async
  {
    var response;
    Profile profile = ActiveSession.getInstance().getProfile();
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"location/update/";
    Map<String,dynamic> jsonMap = new Map();
    jsonMap['email'] = profile.email;
    jsonMap['latitude'] = locationData.latitude;
    jsonMap['longitude'] = locationData.longitude;
    String jsonBody = jsonEncode(jsonMap);

    //print("********LOCATION_UPDATE*************");
    //print(jsonBody);

    try {
      response = await http.post(Uri.parse(remoteUrl),headers: {
        "Principal":ActiveSession.getInstance().securityToken.email,
        "Bearer": ActiveSession.getInstance().securityToken.bearerToken,
      }, body: jsonBody);
    }
    catch (e) {
      //print("********LOCATION_UPDATE*************");
      //print("LOCATION_POST_SYSTEM_ERROR");
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    Map<String,dynamic> error = UrlFunctions.handleError(null, response);
    if(error != null){
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    //print("********LOCATION_UPDATE*************");
    //print(response.body);
    return response.body;
  }

  Future<String> accept(String email, FoodRecoveryTransaction tx) async
  {
    Map<String,dynamic> payload = new Map();
    payload["email"] = email;
    payload["accepted"] = tx.getId();
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"activeNetwork/accept/";
    var response;
    try {
      response = await http.post(Uri.parse(remoteUrl),headers: {
        "Principal":ActiveSession.getInstance().securityToken.email,
        "Bearer": ActiveSession.getInstance().securityToken.bearerToken,
      }, body: jsonEncode(payload)).
      timeout(Duration(seconds: 30),onTimeout: () {
        throw new CloudBusinessException(500, "NETWORK_TIME_OUT");
      });
    }
    catch (e) {
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    Map<String,dynamic> error = UrlFunctions.handleError(null, response);
    if(error != null){
      throw new CloudBusinessException(error["statusCode"], error["exception"]);
    }

    //print("ACCEPT: "+response.body);

    return response.body;
  }
  //---------------NOT_IN_USE_YET---------------------------------------------------------------------------------------------
  Future<String> notifyOfflineAvailability(String foodRunnerEmail) async
  {
    var response;

    FoodRunner foodRunner = ActiveSession.getInstance().foodRunner;
    
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"offline/notification/";
    Map<String, dynamic> json = new Map();
    json['foodRunnerEmail'] = foodRunnerEmail;
    json['available'] = foodRunner.offlineCommunitySupport;
    String jsonBody = jsonEncode(json);
    try {
      response = await http.post(Uri.parse(remoteUrl),headers: {
        "Principal":ActiveSession.getInstance().securityToken.email,
        "Bearer": ActiveSession.getInstance().securityToken.bearerToken,
      }, body: jsonBody);
    }
    catch (e) {
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    Map<String,dynamic> error = UrlFunctions.handleError(null, response);
    if(error != null){
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }
    return response.body;
  }

  Future<Map<String,List<FoodRecoveryTransaction>>> notifyDelivery(FoodRecoveryTransaction tx) async
  {
    Map<String,List<FoodRecoveryTransaction>> txs = new Map();

    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"activeNetwork/notifyDelivery/";
    Map<String,String> payload = new Map();
    payload["email"] = ActiveSession.getInstance().profile.email;
    payload["txId"] = tx.getId();
    String jsonBody = jsonEncode(payload);
    var response;
    try {
      response = await http.post(Uri.parse(remoteUrl),headers: {
        "Principal":ActiveSession.getInstance().securityToken.email,
        "Bearer": ActiveSession.getInstance().securityToken.bearerToken,
      }, body: jsonBody).
      timeout(Duration(seconds: 30),onTimeout: () {
        throw new CloudBusinessException(500, "NETWORK_TIME_OUT");
      });
    }
    catch (e) {
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    Map<String,dynamic> error = UrlFunctions.handleError(null, response);
    if(error != null){
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    Map<String,dynamic> object = jsonDecode(response.body);

    if(object['pending'] != null) {
      Iterable l = object['pending'];
      List<FoodRecoveryTransaction> pending = [];
      for (Map<String, dynamic> tx in l) {
        FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
        pending.add(local);
      }
      txs['pending'] = pending;
    }
    else{
      txs ['pending'] = [];
    }


    if(object['inProgress'] != null) {
      Iterable l = object['inProgress'];
      List<FoodRecoveryTransaction> inProgress = [];
      for (Map<String, dynamic> tx in l) {
        FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
        inProgress.add(local);
      }
      txs['inProgress'] = inProgress;
    }
    else{
      txs ['inProgress'] = [];
    }

    return txs;
  }

  Future<Map<String,List<FoodRecoveryTransaction>>> acceptCommunityDropOff(String email, FoodRecoveryTransaction tx) async
  {
    Map<String,dynamic> payload = new Map();
    payload["email"] = email;
    payload["accepted"] = tx.getId();
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+"activeNetwork/accept/";
    var response;
    try {
      response = await http.post(Uri.parse(remoteUrl),headers: {
        "Principal":ActiveSession.getInstance().securityToken.email,
        "Bearer": ActiveSession.getInstance().securityToken.bearerToken,
      }, body: jsonEncode(payload)).
      timeout(Duration(seconds: 30),onTimeout: () {
        throw new CloudBusinessException(500, "NETWORK_TIME_OUT");
      });
    }
    catch (e) {
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    Map<String,dynamic> error = UrlFunctions.handleError(null, response);
    if(error != null){
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    Map<String,List<FoodRecoveryTransaction>> txs = new Map();
    remoteUrl = UrlFunctions.getInstance().resolveHost()+"tx/recovery/foodRunner/?email="+email;
    try {
      response = await http.get(Uri.parse(remoteUrl));
    }
    catch (e) {
      //print(e);
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    print(response.body);

    Map<String,dynamic> object = jsonDecode(response.body);

    if(object['pending'] != null) {
      Iterable l = object['pending'];
      List<FoodRecoveryTransaction> pending = [];
      for (Map<String, dynamic> tx in l) {
        FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
        pending.add(local);
      }
      txs['pending'] = pending;
    }
    else{
      txs ['pending'] = [];
    }


    if(object['inProgress'] != null) {
      Iterable l = object['inProgress'];
      List<FoodRecoveryTransaction> inProgress = [];
      for (Map<String, dynamic> tx in l) {
        FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
        inProgress.add(local);
      }
      txs['inProgress'] = inProgress;
    }
    else{
      txs ['inProgress'] = [];
    }

    return txs;
  }
}






/*Future<List<FoodRecoveryTransaction>> getFoodRecoveryPush(String email) async
  {
    List<FoodRecoveryTransaction> txs = [];
    var response;

    String host = UrlFunctions.getInstance().resolveHost();

    String remoteUrl = host+"tx/push/recovery/?email="+email;
    try {
      response = await http.get(Uri.parse(remoteUrl),headers: {
        "Principal":ActiveSession.getInstance().securityToken.email,
        "Bearer": ActiveSession.getInstance().securityToken.bearerToken,
      },);
    }
    catch (e) {
      throw new CloudBusinessException(500, "UNKNOWN_SYSTEM_ERROR");
    }

    Map<String,dynamic> object = jsonDecode(response.body);
    Iterable l = object['pending'];
    for(Map<String, dynamic> tx in l)
    {
      FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
      txs.add(local);
    }

    return txs;
  }*/