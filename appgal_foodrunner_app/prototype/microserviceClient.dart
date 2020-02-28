import 'package:http/http.dart' as http;

void main(List<String> args) {
  http.get('http://localhost:8080/microservice/').then((response) {
    print(response.body);
  });
}