package org.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


@Configuration
public class EmailConfig {

    @Bean
    JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender=new JavaMailSenderImpl();
        mailSender.setUsername("yourmail@gmail.com");
        mailSender.setPassword("your-app-pwd");
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        Properties props= mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable",true);
//        props.put("maail")
        return mailSender;
    }

}
