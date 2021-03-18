import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/pickupRequest.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:app/src/model/activeView.dart' show ActiveView;
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/schedulePickupNotification.dart';
import 'package:location/location.dart';
//import 'package:location/location.dart';

import '../model/schedulePickupNotification.dart';

class ActiveNetworkRestClient
{
  Future<ActiveView> getActiveView() async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'activeNetwork/activeView/';
    var response = await http.get(Uri.parse(remoteUrl));
    String activeViewJson = response.body;
    ActiveView activeView = ActiveView.fromJson(jsonDecode(activeViewJson));
    return activeView;
  }

  Future<String> sendDeliveryNotification(DropOffNotification dropOffNotification) async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'activeNetwork/sendDeliveryNotification/';
    String jsonBody = dropOffNotification.toString();
    var response = await http.post(Uri.parse(remoteUrl), body: jsonBody);
    return response.body;
  }

  Future<List<SourceOrg>> findBestDestination(FoodRunner foodRunner) async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'activeNetwork/findBestDestination/';
    String inputJson = foodRunner.toString();
    var response = await http.post(Uri.parse(remoteUrl), body: inputJson);
    String responseJson = response.body;
  
    Iterable l = json.decode(responseJson);

    List<SourceOrg> sourceOrgs = new List();
    for(Map<String, dynamic> sourceOrgJson in l)
    {
        SourceOrg sourceOrg = SourceOrg.fromJson(sourceOrgJson);
        sourceOrgs.add(sourceOrg);
    }
    return sourceOrgs;
  }

  Future<Iterable> sendPickupRequest(PickupRequest pickupRequest) async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'activeNetwork/pickUpRequest/send/';
    String jsonBody = pickupRequest.toJson().toString();
    var response = await http.post(Uri.parse(remoteUrl), body: jsonBody);
    String responseJson = response.body;
    Iterable l = json.decode(responseJson);
    return l;
  }

  Future<String> sendFoodRequest(FoodRequest foodRequest) async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'activeNetwork/sendFoodRequest/';
    String jsonBody = foodRequest.toJson().toString();
    var response = await http.post(Uri.parse(remoteUrl), body: jsonBody);
    String responseJson = response.body;
    return responseJson;
  }

  Future<List<SourceOrg>> getSourceOrgs() async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'activeNetwork/sourceOrgs/';
    var response = await http.get(Uri.parse(remoteUrl));
    String responseJson = response.body;
    Iterable l = json.decode(responseJson);
    List<SourceOrg> sourceOrgs = new List();
    for(Map<String, dynamic> sourceOrgJson in l)
    {
        SourceOrg sourceOrg = SourceOrg.fromJson(sourceOrgJson);
        sourceOrgs.add(sourceOrg);
    }
    return sourceOrgs;
  }

  Future<String> getSchedulePickUpNotification(String email) async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'notification/pickup/notifications/?email='+email;
    var response = await http.get(Uri.parse(remoteUrl));
    String responseJson = response.body;
    /*Iterable l = json.decode(responseJson);
    List<SourceOrg> sourceOrgs = new List();
    for(Map<String, dynamic> sourceOrgJson in l)
    {
        SourceOrg sourceOrg = SourceOrg.fromJson(sourceOrgJson);
        sourceOrgs.add(sourceOrg);
    }
    return sourceOrgs;*/
    return responseJson;
  }

  Future<int> sendSchedulePickupNotification(SchedulePickupNotification notification) async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'activeNetwork/schedulePickUp/';
    String jsonBody = notification.toJson().toString();
    var response = await http.post(Uri.parse(remoteUrl), body: jsonBody);
    return response.statusCode;
  }

  Future<List<FoodRecoveryTransaction>> getFoodRecoveryTransaction() async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'tx/recovery';
    var response = await http.get(Uri.parse(remoteUrl));
    String responseJson = response.body;
    Map<String,dynamic> object = json.decode(responseJson);
    Iterable l = object['pending'];
    List<FoodRecoveryTransaction> txs = new List();
    for(Map<String, dynamic> tx in l)
    {
        FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
        txs.add(local);
    }
    return txs;
  }

  Future<String> sendLocationUpdate(LocationData locationData) async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'location/update/';
    Map<String,dynamic> jsonMap = new Map();
    jsonMap['latitude'] = locationData.latitude;
    jsonMap['longitude'] = locationData.longitude;
    String jsonBody = jsonEncode(jsonMap);
    var response = await http.post(Uri.parse(remoteUrl), body: jsonBody);
    return response.body;
  }

  Future<String> notifyOfflineAvailability(String foodRunnerId) async
  {
    String remoteUrl = UrlFunctions.getInstance().resolveHost()+'offline/notification/';
    Map<String, dynamic> json = new Map();
    json['foodRunnerId'] = foodRunnerId;
    String jsonBody = jsonEncode(json);
    var response = await http.post(Uri.parse(remoteUrl), body: jsonBody);
    return response.body;
  }
}