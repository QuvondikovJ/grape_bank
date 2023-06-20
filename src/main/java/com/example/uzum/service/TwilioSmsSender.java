package com.example.uzum.service;

public interface TwilioSmsSender {

    void sendSms(String phoneNumber, String message);

}
