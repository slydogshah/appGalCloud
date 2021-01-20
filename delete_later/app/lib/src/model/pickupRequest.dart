import 'package:app/src/model/sourceOrg.dart';

class PickupRequest
{
  SourceOrg sourceOrg;

  PickupRequest(SourceOrg sourceOrg)
  {
    this.sourceOrg = sourceOrg;
  }

  void setSourceOrg(SourceOrg sourceOrg)
  {
    this.sourceOrg = sourceOrg;
  }

  SourceOrg getSourceOrg()
  {
    return this.sourceOrg;
  }

  Map<String, dynamic> toJson() => this.sourceOrg.toJson();
}