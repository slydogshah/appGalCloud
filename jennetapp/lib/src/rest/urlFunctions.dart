import 'dart:io';

class UrlFunctions
{
  static String resolveHost()
  {
    if(Platform.isAndroid)
    {
        return "10.0.2.2";
    }
    else if(Platform.isIOS)
    {
        return "localhost";
    }
    return "localhost";
  }
}