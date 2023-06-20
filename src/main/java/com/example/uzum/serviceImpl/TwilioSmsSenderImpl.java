package com.example.uzum.serviceImpl;

import com.example.uzum.config.TwilioConfiguration;
import com.example.uzum.service.TwilioSmsSender;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsSenderImpl implements TwilioSmsSender {

    @Autowired
    private TwilioConfiguration twilioConfiguration;

    @Override
    public void sendSms(String phoneNumber, String message) {
        Twilio.init(twilioConfiguration.getAccountSid(), twilioConfiguration.getAuthToken());
        PhoneNumber to = new PhoneNumber(phoneNumber);
        PhoneNumber from = new PhoneNumber(twilioConfiguration.getTrialNumber());
        MessageCreator creator = Message.creator(to, from, message);
        creator.create();
    }
}
