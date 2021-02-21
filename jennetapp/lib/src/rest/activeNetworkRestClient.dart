import 'package:app/src/model/foodRecoveryTransaction.dart';
import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/location.dart';
import 'package:app/src/model/pickupRequest.dart';
import 'package:app/src/model/sourceOrg.dart';
import 'package:app/src/rest/urlFunctions.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:app/src/model/activeView.dart' show ActiveView;
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/dropOffNotification.dart';
import 'package:app/src/model/schedulePickupNotification.dart';
import 'package:app/src/model/foodRecoveryTransaction.dart';

import '../model/schedulePickupNotification.dart';

class ActiveNetworkRestClient
{
  Future<ActiveView> getActiveView() async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/activeNetwork/activeView/';
    var response = await http.get(remoteUrl);
    String activeViewJson = response.body;
    ActiveView activeView = ActiveView.fromJson(jsonDecode(activeViewJson));
    return activeView;
  }

  Future<String> sendDeliveryNotification(DropOffNotification dropOffNotification) async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/activeNetwork/sendDeliveryNotification/';
    String jsonBody = dropOffNotification.toString();
    var response = await http.post(remoteUrl, body: jsonBody);
    return response.body;
  }

  Future<List<SourceOrg>> findBestDestination(FoodRunner foodRunner) async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/activeNetwork/findBestDestination/';
    String inputJson = foodRunner.toString();
    var response = await http.post(remoteUrl, body: inputJson);
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
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/activeNetwork/pickUpRequest/send/';
    String jsonBody = pickupRequest.toJson().toString();
    var response = await http.post(remoteUrl, body: jsonBody);
    String responseJson = response.body;
    Iterable l = json.decode(responseJson);
    return l;
  }

  Future<String> sendFoodRequest(FoodRequest foodRequest) async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/activeNetwork/sendFoodRequest/';
    String jsonBody = foodRequest.toJson().toString();
    var response = await http.post(remoteUrl, body: jsonBody);
    String responseJson = response.body;
    return responseJson;
  }

  Future<List<SourceOrg>> getSourceOrgs() async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/activeNetwork/sourceOrgs/';
    var response = await http.get(remoteUrl);
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
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/notification/pickup/notifications/?email='+email;
    var response = await http.get(remoteUrl);
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
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/activeNetwork/schedulePickUp/';
    String jsonBody = notification.toJson().toString();
    var response = await http.post(remoteUrl, body: jsonBody);
    return response.statusCode;
  }

  Future<List<FoodRecoveryTransaction>> getFoodRecoveryTransaction() async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/tx/recovery';
    var response = await http.get(remoteUrl);
    String responseJson = response.body;
    Iterable l = json.decode(responseJson);
    List<FoodRecoveryTransaction> txs = new List();
    for(Map<String, dynamic> tx in l)
    {
        FoodRecoveryTransaction local = FoodRecoveryTransaction.fromJson(tx);
        txs.add(local);
    }
    return txs;
  }

  Future<String> sendLocationUpdate(Location location) async
  {
    String remoteUrl = 'http://'+UrlFunctions.resolveHost()+':8080/location/update/';
    String jsonBody = location.toString();
    var response = await http.post(remoteUrl, body: jsonBody);
    return response.body;
  }
}