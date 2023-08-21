package com.example.springbootmongodb.service;

import com.example.springbootmongodb.config.MailConfiguration;
import com.example.springbootmongodb.config.ReturnPolicies;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSenderImpl mailSender;
    private final Configuration freeMarkerConfiguration;
    private final ThreadPoolTaskExecutor mailServiceExecutor;
    private final MailConfiguration mailConfiguration;

    @Override
    public void sendActivationMail(String mailTo, String activateLink) {
        String subject = "Account activation mail";
        String templateLocation = "activation.ftl";
        Map<String, Object> model = new HashMap<>();
        model.put("activationLink", activateLink);
        model.put("targetEmail", mailTo);
        String message = convertTemplateIntoString(templateLocation, model);
        sendMailAsync(mailSender.getUsername(), mailTo, subject, message);
    }

    @Override
    public void sendPasswordResetMail(String mailTo, String passwordResetLink) {
        String subject = "Password reset mail";
        String templateLocation = "reset.password.ftl";
        Map<String, Object> model = new HashMap<>();
        model.put("passwordResetLink", passwordResetLink);
        model.put("targetEmail", mailTo);
        String message = convertTemplateIntoString(templateLocation, model);
        sendMailAsync(mailSender.getUsername(), mailTo, subject, message);
    }

    @Override
    public void sendRefundProcessingMail(String mailTo) {
        String subject = "Thông báo đổi trả hàng LVTN Shop";
        String templateLocation = "return.processing.ftl";
        Map<String, Object> model = new HashMap<>();
        model.put("maxDaysForRefundTransferring", ReturnPolicies.MAX_DAYS_TRANSFERRING_MONEY);
        model.put("targetEmail", mailTo);
        String message = convertTemplateIntoString(templateLocation, model);
        sendMailAsync(mailSender.getUsername(), mailTo, subject, message);
    }

    @Override
    public void sendRefundConfirmationEmail(String mailTo, long refundedAmount) {
        String subject = "Xác nhận hoàn tiền LVTN Shop";
        String templateLocation = "return.refunded.ftl";
        Map<String, Object> model = new HashMap<>();
        model.put("targetEmail", mailTo);
        model.put("refundedAmount", refundedAmount);
        model.put("confirmationLink", "http://localhost:5000");
        model.put("maxDaysWaitingForConfirmation", ReturnPolicies.MAX_DAYS_WAITING_FOR_USER_CONFIRMATION);
        String message = convertTemplateIntoString(templateLocation, model);
        sendMailAsync(mailSender.getUsername(), mailTo, subject, message);
    }

    @Override
    public void sendAcceptedReturnEmail(String mailTo) {
        String subject = "Xác nhận yêu cầu đổi hàng/hoàn tiền LVTN Shop";
        String templateLocation = "return.user.preparation.ftl";
        Map<String, Object> model = new HashMap<>();
        model.put("targetEmail", mailTo);
        model.put("placeReturnShipmentOrderLink", "http://localhost:5000");
        model.put("maxDaysForUserPreparation", ReturnPolicies.MAX_DAYS_USER_PREPARING);
        String message = convertTemplateIntoString(templateLocation, model);
        sendMailAsync(mailSender.getUsername(), mailTo, subject, message);
    }

    @Override
    public void sendTemplateMail() {
        String mailFrom = "mochacr0@gmail.com";
        String mailTo = "nthai2001cr@gmail.com";
        String subject = "Test mail sending";
        String templateLocation = "activation.ftl";
        Map<String, Object> model = new HashMap<>();
        model.put("activationLink", "http://localhost:5000");
        model.put("targetEmail", mailTo);
        String message = convertTemplateIntoString(templateLocation, model);
        sendMail(mailFrom, mailTo, subject, message);
    }

    @Override
    public void sendMail(String mailFrom, String mailTo, String subject, String message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
        try {
            messageHelper.setFrom(mailFrom);
            messageHelper.setTo(mailTo);
            messageHelper.setSubject(subject);
            messageHelper.setText(message, true);
            mailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMailAsync(String mailFrom, String mailTo, String subject, String message) {
            Future<?> sendingResult = mailServiceExecutor.submit(() -> sendMail(mailFrom, mailTo, subject, message));
            mailServiceExecutor.submit(() -> {
                try {
                    sendingResult.get(mailConfiguration.getTimeout(), TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    log.error("Encounter error while sending mail");
                    throw new RuntimeException(e);
                }
            });
    }

    private String convertTemplateIntoString(String templateLocation, Map<String, Object> model) {
        Template template;
        try {
            template = this.freeMarkerConfiguration.getTemplate(templateLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }


}
