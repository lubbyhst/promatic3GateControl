package com.github.lubbyhst.promatic3control.api.components;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfiguraiton {

    @Bean
    public JavaMailSender getJavaMailSender(
            @Value("${spring.mail.host}")
            final String mailHost,
            @Value("${spring.mail.port}")
            final int mailPort,
            @Value("${spring.mail.username}")
            final String username,
            @Value("${spring.mail.password}")
            final String password) {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        final Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "false");

        return mailSender;
    }
}
