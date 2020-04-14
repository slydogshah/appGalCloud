import 'package:app/src/model/activeView.dart';
import 'package:app/src/model/authCredentials.dart';
import 'package:app/src/model/profile.dart';
import 'package:app/src/rest/profileRestClient.dart';
import 'package:test/test.dart';

void main() {
  test('getProfile', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<Profile> profileFuture = profileRestClient.getProfile("blah@blah.com");
    profileFuture.then((profile){
      expect(profile.email, "blah@blah.com");
    });
  });

  test('getActiveView', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    Future<ActiveView> activeViewFuture = profileRestClient.getActiveView();
    activeViewFuture.then((activeView){
      print(activeView);
    });
  });

  test('login', () {
    ProfileRestClient profileRestClient = new ProfileRestClient();
    AuthCredentials credentials = new AuthCredentials();
    credentials.email = "blah@blah.com";
    credentials.password = "blahblah";
    Future<AuthCredentials> future = profileRestClient.login(credentials);
    future.then((authCredentials){
      print(authCredentials.toString());
    });
  });

  test('String.trim() removes surrounding whitespace', () {
    var string = '  foo ';
    expect(string.trim(), equals('foo'));
  });

  /*Future<int> future = Future.delayed(
    const Duration(seconds: 3),
    () => 100,
  );
  future.then((value) {
    print('The value is $value.'); // Prints later, after 3 seconds.
  });
  print('Waiting for a value...'); 

  ProfileRestClient profileRestClient = new ProfileRestClient();
  Future<Profile> profileFuture = profileRestClient.getProfile("blah@blah.com");
  profileFuture.then((value){
    print(value.toString());
  });*/
}