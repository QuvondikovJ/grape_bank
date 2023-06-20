package com.example.uzum.serviceImpl;

import com.example.uzum.service.EmailService;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);
    private static final String emailApiKey = "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";
    private final String verificationLinkBaseForEmployee = "http://localhost:8080/api/employee/confirm-email?token=";
    private final String verificationLinkBaseForBuyer = "http://localhost:8080/api/buyer/confirm-email?token=";

    public void sendEmail(String emailContent, String phoneNumber, String causeOfSending, String sentEmail) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, emailContent);
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .addHeader("api-key", emailApiKey)
                .url("https://api.brevo.com/v3/smtp/email")
                .method("POST", body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() < 400)
            logger.info("Email successfully sent to {} for {}. Sent email: {} ", phoneNumber, causeOfSending, sentEmail);
        else
            logger.error("Email didn't send to {} for {}, Didn't send email: {}. Please check cause of error.", phoneNumber, causeOfSending, sentEmail);
    }


    @Override
    public String buildEmailForAuthorization(String receiverName, String receiverEmail, String subject, String uuid, boolean isBuyer) {
        String verificationLink;
        if (isBuyer) verificationLink = verificationLinkBaseForBuyer.concat(uuid);
        else verificationLink = verificationLinkBaseForEmployee.concat(uuid);
        String htmlContent = "<html><head>" +
                "    <meta charset='UTF-8'>" +
                "    <title>Title</title>" +
                "</head>" +
                "<body style='font-family: Arial; font-size:16px; color:#333;'>" +
                "<h5>Hi ." + receiverName + "</h5>" +
                "<br>" +
                "<p>Thank you for registering. Please, click on the below link to confirm your email.</p>" +
                "<h6><a href='" + verificationLink + "' style='color: red; font-size:16px'>" + verificationLink + "</a></h6>" +
                "<br>" +
                "<p>Verification link will expire in 5 minutes.</p>" +
                "<br> <br>" +
                "<p>Cheers GrapeBank.</p>" +
                "</body>" +
                "</html>";

        return "{" +
                " \"sender\":{" +
                "\"name\":\"Jack Kuvondikov\"," +
                "\"email\":\"quvondikovj6@gmail.com\"" +
                "}," +
                " \"to\":    [{" +
                "\"name\":\"" + receiverName + "\"," +
                "\"email\":\"" + receiverEmail + "\"" +
                "}]," +
                "\"subject\":\"" + subject + "\"," +
                "\"htmlContent\":\"" + htmlContent + "\"" +
                "}";
    }

    @Override
    public String buildEmailForChangingEmail(String receiverName, String receiverEmail, String subject, String uuid, boolean isBuyer) {
        String verificationLink;
        if (isBuyer) verificationLink = verificationLinkBaseForBuyer.concat(uuid);
        else verificationLink = verificationLinkBaseForEmployee.concat(uuid);
        String htmlContent = "<html><head>" +
                "    <meta charset='UTF-8'>" +
                "    <title>Title</title>" +
                "</head>" +
                "<body style='font-family: Arial; font-size:16px; color:#333;'>" +
                "<h5>Hi ." + receiverName + "</h5>" +
                "<br>" +
                "<p>Do you really wanna change your email? if that is yes, then click on the below link to confirm your new email.</p>" +
                "<h6><a href='" + verificationLink + "' style='color: red; font-size:16px'>" + verificationLink + "</a></h6>" +
                "<br>" +
                "<p>Verification link will expire in 5 minutes. Hurry up.</p>" +
                "<br> <br>" +
                "<p>Cheers GrapeBank.</p>" +
                "</body>" +
                "</html>";

        return "{" +
                " \"sender\":{" +
                "\"name\":\"Jack Kuvondikov\"," +
                "\"email\":\"quvondikovj6@gmail.com\"" +
                "}," +
                " \"to\":    [{" +
                "\"name\":\"" + receiverName + "\"," +
                "\"email\":\"" + receiverEmail + "\"" +
                "}]," +
                "\"subject\":\"" + subject + "\"," +
                "\"htmlContent\":\"" + htmlContent + "\"" +
                "}";
    }

    @Override
    public String buildEmailForWarningAboutPaySalaries(String receiverName, String receiverEmail, String subject, Integer amountOfEmployeesThatArentPaidSalaries, Integer amountOfMoneyToBePaid, int year, String month, boolean isDirector) {
        String htmlContent;
        if (isDirector)
            htmlContent = "<html><head>" +
                    "    <meta charset='UTF-8'>" +
                    "    <title>Title</title>" +
                    "</head>" +
                    "<body style='font-family: Arial; font-size:16px; color:#333;'>" +
                    "<h5>Hi ." + receiverName + "</h5>" +
                    "<br>" +
                    "<p>Dear " + receiverName + ", Money isn't enough to pay employees salaries for " + month + " - " + year + ". Please, right now connect with Admins to resolve this problem. Amount of employees that aren't paid salaries is " +
                    amountOfEmployeesThatArentPaidSalaries + ". Amount of money to be paid is " + amountOfMoneyToBePaid + " </p>" +
                    "<br>" +
                    "<br> <br>" +
                    "<p>Cheers GrapeBank.</p>" +
                    "</body>" +
                    "</html>";
        else
            htmlContent = "<html><head>" +
                    "    <meta charset='UTF-8'>" +
                    "    <title>Title</title>" +
                    "</head>" +
                    "<body style='font-family: Arial; font-size:16px; color:#333;'>" +
                    "<h5>Hi ." + receiverName + "</h5>" +
                    "<br>" +
                    "<p>Dear " + receiverName + ", Money isn't enough to pay employees salaries for " + month + " - " + year + ". Please, right now connect with Director to resolve this problem. Amount of employees that aren't paid salaries is " +
                    amountOfEmployeesThatArentPaidSalaries + ". Amount of money to be paid is " + amountOfMoneyToBePaid + " </p>" +
                    "<br>" +
                    "<br> <br>" +
                    "<p>Cheers GrapeBank.</p>" +
                    "</body>" +
                    "</html>";
        return "{" +
                " \"sender\":{" +
                "\"name\":\"Jack Kuvondikov\"," +
                "\"email\":\"quvondikovj6@gmail.com\"" +
                "}," +
                " \"to\":    [{" +
                "\"name\":\"" + receiverName + "\"," +
                "\"email\":\"" + receiverEmail + "\"" +
                "}]," +
                "\"subject\":\"" + subject + "\"," +
                "\"htmlContent\":\"" + htmlContent + "\"" +
                "}";
    }

    public String buildEmailForWarningAboutOrder(String receiverName, String receiverEmail, String subject) {

        return null;
    }

}
