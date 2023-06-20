package com.example.uzum.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface EmailService {

    void sendEmail(String emailContent, String phoneNumber, String causeOfSending, String sentEmail) throws IOException;

    String buildEmailForAuthorization(String receiverName, String receiverEmail, String subject, String uuid, boolean isBuyer);
    String buildEmailForChangingEmail(String receiverName, String receiverEmail, String subject, String uuid, boolean isBuyer);
    public String buildEmailForWarningAboutPaySalaries(String receiverName, String receiverEmail, String subject, Integer amountOfEmployeesThatArentPaidSalaries, Integer amountOfMoneyToBePaid, int year, String month, boolean isDirector);

}
