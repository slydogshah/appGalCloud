import 'dart:async';

class CloudDataPoller
{
  static void startPolling()
  {
    const oneSec = const Duration(seconds:1);
    new Timer.periodic(oneSec, (Timer t) {
      print(new DateTime.now().millisecondsSinceEpoch);
    } );
  }
}