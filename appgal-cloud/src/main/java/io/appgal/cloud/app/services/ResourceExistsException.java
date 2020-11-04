package io.appgal.cloud.app.services;

public class ResourceExistsException extends Exception
{
    public ResourceExistsException(String message)
    {
        super(message);
    }
}