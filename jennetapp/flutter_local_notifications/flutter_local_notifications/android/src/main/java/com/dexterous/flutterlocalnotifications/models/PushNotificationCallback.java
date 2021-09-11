package com.dexterous.flutterlocalnotifications.models;

public class PushNotificationCallback{
    public void notifyTokenRefresh(String token){
        System.out.println("UPLOAD_TOKEN: "+token);
    }
}