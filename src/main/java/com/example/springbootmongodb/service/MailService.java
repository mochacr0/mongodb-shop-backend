package com.example.springbootmongodb.service;

public interface MailService {
    public void sendTemplateMail();
    public void sendMail(String mailFrom, String mailTo, String subject, String message);
    public void sendMailAsync(String mailFrom, String mailTo, String subject, String message);
    public void sendActivationMail(String mailTo, String activateLink);
    public void sendPasswordResetMail(String mailTo, String passwordResetLink);
    public void sendRefundProcessingMail(String mailTo);
    public void sendRefundConfirmationEmail(String mailTo, long refundedAmount);
    public void sendAcceptedReturnEmail(String mailTo);
}
