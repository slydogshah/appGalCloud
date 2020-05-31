class CloudBusinessException implements Exception
{
  int statusCode;
  String message;

  CloudBusinessException(int statusCode, String message)
  {
    this.statusCode = statusCode;
    this.message = message;
  }
}