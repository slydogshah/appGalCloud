import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:app/src/model/schedulePickupNotification.dart';
import 'package:location/location.dart';

import '../model/schedulePickupNotification.dart';
import 'cloudBusinessException.dart';

class ActiveNetworkRestClient
{
  Future<String> getSchedulePickUpNotification(String email) async
  {
    var response;

    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'notification/pickup/notifications/?email='+email;
    try {
      response = await http.get(Uri.parse(remoteUrl));
    }
    catch (e) {
      print(e);
      Map<String, dynamic> json = UrlFunctions.handleError(e, response);
      return jsonEncode(json);
    }
    return response.body;
  }

  Future<List<FoodRecoveryTransaction>> getFoodRecoveryTransaction(String email) async
  {
    List<FoodRecoveryTransaction> txs = new List();
    var response;
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'tx/recovery/foodRunner/?email='+email;
    try {
      response = await http.get(Uri.parse(remoteUrl));
    }
    catch (e) {
      print(e);
      return txs;
    }

    Map<String,dynamic> object = json.decode(response.body);
    Iterable l = object['pending'];
    for(Map<String, dynamic> tx in l)
    {
        FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
        txs.add(local);
    }
    return txs;
  }

  Future<List<FoodRecoveryTransaction>> getFoodRecoveryPush(String email) async
  {
    List<FoodRecoveryTransaction> txs = new List();
    var response;
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'tx/push/recovery/?email='+email;
    try {
      response = await http.get(Uri.parse(remoteUrl));
    }
    catch (e) {
      print(e);
      return txs;
    }

    Map<String,dynamic> object = json.decode(response.body);
    Iterable l = object['pending'];
    for(Map<String, dynamic> tx in l)
    {
      FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
      txs.add(local);
    }
    return txs;
  }

  Future<String> sendLocationUpdate(LocationData locationData) async
  {
    var response;
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'location/update/';
    Map<String,dynamic> jsonMap = new Map();
    jsonMap['latitude'] = locationData.latitude;
    jsonMap['longitude'] = locationData.longitude;
    String jsonBody = jsonEncode(jsonMap);
    try {
      response = await http.post(Uri.parse(remoteUrl), body: jsonBody);
    }
    catch (e) {
      print(e);
    }
    return response.body;
  }

  Future<String> notifyOfflineAvailability(String foodRunnerId) async
  {
    var response;
    
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'offline/notification/';
    Map<String, dynamic> json = new Map();
    json['foodRunnerId'] = foodRunnerId;
    String jsonBody = jsonEncode(json);
    try {
      response = await http.post(Uri.parse(remoteUrl), body: jsonBody);
    }
    catch (e) {
      print(e);
    }
    return response.body;
  }

  Future<int> accept(String email, String dropOffOrgId,FoodRecoveryTransaction tx) async
  {
    var json;
    Map<String,dynamic> payload = new Map();
    payload["email"] = email;
    payload["dropOffOrgId"] = dropOffOrgId;
    payload["accepted"] = tx;
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'/activeNetwork/accept/';
    var response;
    try {
      response = await http.post(Uri.parse(remoteUrl), body: payload.toString()).
      timeout(Duration(seconds: 30),onTimeout: () {
        throw new CloudBusinessException(500, "NETWORK_TIME_OUT");
      });
    }
    catch (e) {
      print(e);
      json = UrlFunctions.handleError(e, response);
      return json["statusCode"];
    }

    json = UrlFunctions.handleError(null, response);
    if(json != null)
    {
      return json["statusCode"];
    }

    return response.statusCode;
  }
}