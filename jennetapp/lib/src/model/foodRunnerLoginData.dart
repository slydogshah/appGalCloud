import 'package:app/src/model/sourceOrg.dart';
import 'authCredentials.dart';

class FoodRunnerLoginData
{
  AuthCredentials authCredentials;
  List<SourceOrg> sourceOrgs;

  Map<String,dynamic> authFailure;

  FoodRunnerLoginData();

  AuthCredentials getAuthCredentials()
  {
    return this.authCredentials;
  }

  setAuthCredentials(AuthCredentials authCredentials)
  {
    this.authCredentials = authCredentials;
  }

  List<SourceOrg> getSourceOrgs()
  {
    return this.sourceOrgs;
  }

  setSourceOrgs(List<SourceOrg> sourceOrgs)
  {
    this.sourceOrgs = sourceOrgs;
  }
}