import 'package:test/test.dart';
import 'package:http/http.dart' as http;

void main() {
  test('modelTraffic', () async {
    String remoteUrl = 'http://localhost:8080/dashboard/modelTraffic/';
    var response = await http.get(remoteUrl);
    print(response.body);
  });
}