package io.appgal.cloud.restclient;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TwilioClient {
    private static Logger logger = LoggerFactory.getLogger(TwilioClient.class);

    public void sendResetCode(String to, String resetCode)
    {
        String account = "ACfc5fb6ca2ca7e05f1af9067d7579418b";
        String secret = "c4bfa1088aa1277ff8dfeba1567a2d56";
        String from = "+17082953630";


        Twilio.init(account,secret);

        Message message = Message.creator(new PhoneNumber(to),
                new PhoneNumber(from),
                "#Jen Network Password Reset Code: "+resetCode).create();
    }
}
