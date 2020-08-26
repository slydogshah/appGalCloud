package io.appgal.cloud.services;

public class ResourceExistsException extends Exception
{
    public ResourceExistsException(String message)
    {
        super(message);
    }
}