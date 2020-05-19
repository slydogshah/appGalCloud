import 'package:app/src/messaging/polling/cloudDataPoller.dart';
import 'package:test/test.dart';

void main()
{
  test('startPolling', () 
  {
      CloudDataPoller cloudDataPoller = new CloudDataPoller();
      cloudDataPoller.startPolling();
  });
}