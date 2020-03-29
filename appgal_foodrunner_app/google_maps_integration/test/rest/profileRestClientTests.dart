import 'package:test/test.dart';

import '../../lib/src/rest/profileRestClient.dart';
import '../../lib/src/model/profile.dart';

void main(List<String> args) {

  void assertProfile(Profile profile)
  {
      print(profile);
  }

  test("getProfile", () {
      ProfileRestClient restClient = new ProfileRestClient();
      Future<Profile> profileFuture = restClient.getProfile();
      profileFuture.then((profile){
        assertProfile(profile);
      });
    }); 
}

/*group('String', () {
    test('.split() splits the string on the delimiter', () {
      var string = 'foo,bar,baz';
      expect(string.split(','), equals(['foo', 'bar', 'baz']));
    });

    test('.trim() removes surrounding whitespace', () {
      var string = '  foo ';
      expect(string.trim(), equals('foo'));
    });
  });

  group('int', () {
    test('.remainder() returns the remainder of division', () {
      expect(11.remainder(3), equals(2));
    });

    test('.toRadixString() returns a hex string', () {
      expect(11.toRadixString(16), equals('b'));
    });
  });*/