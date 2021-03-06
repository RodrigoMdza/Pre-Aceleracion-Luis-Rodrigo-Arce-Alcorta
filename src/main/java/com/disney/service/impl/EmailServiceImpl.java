package com.disney.service.impl;

import com.disney.exception.EmailException;
import com.disney.service.EmailService;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private Environment env;

    @Value("${disney.email.sender}")
    private String emailSender;

    @Override
    public void sendWelcomeEmailTo(String to) throws IOException{
        String apiKey = env.getProperty("EMAIL_API_KEY");
        Email fromEmail = new Email(emailSender);
        Email toEmail = new Email(to);
        Content content = new Content(
                "text/plain",
                "Thanks for being part of us");
        String subject = "Welcome to Disney_Api";
        Mail mail = new Mail(fromEmail, subject, toEmail, content);
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try{
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() != 200 &&
                    response.getStatusCode() != 201 &&
                    response.getStatusCode() != 202) throw new EmailException("Please veriry the configue");
        } catch (IOException ex) {
            System.out.println("Invalid Mail");
        }
    }
}
