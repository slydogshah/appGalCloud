import 'dart:async';

class CloudDataPoller
{
  void startPolling()
  {
    const oneSec = const Duration(seconds:1);
    new Timer.periodic(oneSec, (Timer t) => print('hi!'));
  }
}