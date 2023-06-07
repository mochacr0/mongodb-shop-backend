package com.example.springbootmongodb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "mailing")
@Data
public class MailConfiguration {
    private String host;
    private int port;
    private String username;
    private String password;
    private long timeout;
    @Bean
    public JavaMailSenderImpl buildJavaMailSenderImpl() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(this.getHost());
        sender.setPort(this.getPort());
        sender.setUsername(this.getUsername());
        sender.setPassword(this.getPassword());
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.timeout", String.valueOf(this.getTimeout()));
        props.put("mail.debug", "true");
        sender.setJavaMailProperties(props);
        return sender;
    }

}
