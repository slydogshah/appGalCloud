import 'dart:convert';

class ActiveView
{
  List<dynamic> activeFoodRunners;
  List<dynamic> activeFoodRunnerQueue;
  List<dynamic> finderResults;

  ActiveView.fromJson(Map<String, dynamic> json)
  : activeFoodRunners = json['activeFoodRunners'],
  activeFoodRunnerQueue = json['activeFoodRunners'],
  finderResults = json['finderResults'];

  Map<String, dynamic> toJson() =>
  {
    'activeFoodRunners': this.activeFoodRunners,
    'activeFoodRunnerQueue': this.activeFoodRunnerQueue,
    'finderResults': this.finderResults
  };

  String toString()
  {
    String json = jsonEncode(this.toJson());
    return json;
  }
}