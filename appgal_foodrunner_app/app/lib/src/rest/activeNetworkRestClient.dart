import 'package:app/src/model/foodRequest.dart';
import 'package:app/src/model/pickupRequest.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:app/src/model/activeView.dart' show ActiveView;
import 'package:app/src/model/foodRunner.dart';
import 'package:app/src/model/dropOffNotification.dart';

class ActiveNetworkRestClient
{
  Future<ActiveView> getActiveView() async
  {
    //String remoteUrl = "http://10.0.2.2:8080/activeNetwork/activeView/";
    String remoteUrl = "http://localhost:8080/activeNetwork/activeView/";
    var response = await http.get(remoteUrl);
    String activeViewJson = response.body;
    ActiveView activeView = ActiveView.fromJson(jsonDecode(activeViewJson));
    return activeView;
  }

  void sendDeliveryNotification(DropOffNotification dropOffNotification) async
  {
    //String remoteUrl = "http://10.0.2.2:8080/activeNetwork/findBestDestination/";
    String remoteUrl = "http://localhost:8080/activeNetwork/sendDeliveryNotification/";
    String jsonBody = dropOffNotification.toString();
    print(jsonBody);
    var response = await http.post(remoteUrl, body: jsonBody);
    String responseJson = response.body;
    print(responseJson);
  }

  Future<Iterable> findBestDestination(FoodRunner foodRunner) async
  {
    //String remoteUrl = "http://10.0.2.2:8080/activeNetwork/findBestDestination/";
    String remoteUrl = "http://localhost:8080/activeNetwork/findBestDestination/";
    var response = await http.post(remoteUrl, body: foodRunner.toString());
    String responseJson = response.body;
  
    Iterable l = json.decode(responseJson);
    
    return l;
  }

  Future<Iterable> sendPickupRequest(PickupRequest pickupRequest) async
  {
    //String remoteUrl = "http://10.0.2.2:8080/activeNetwork/pickUpRequest/send/";
    String remoteUrl = "http://localhost:8080/activeNetwork/pickUpRequest/send/";
    String jsonBody = pickupRequest.toJson().toString();
    //print(jsonBody);
    var response = await http.post(remoteUrl, body: jsonBody);
    String responseJson = response.body;

    print(responseJson);

    Iterable l = json.decode(responseJson);
    return l;
  }

  void sendFoodRequest(FoodRequest foodRequest) async
  {
    //String remoteUrl = "http://10.0.2.2:8080/activeNetwork/sendFoodRequest/";
    String remoteUrl = "http://localhost:8080/activeNetwork/sendFoodRequest/";
    String jsonBody = foodRequest.toJson().toString();
    //print(jsonBody);
    var response = await http.post(remoteUrl, body: jsonBody);
    String responseJson = response.body;

    print(responseJson);
  }
}