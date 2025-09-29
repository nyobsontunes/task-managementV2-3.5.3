package com.adacorp.task_managementV2.configuration;

// import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Properties;



/**
 * Class nécessaire à la configuration du service d'envoi de mail
 * @author ethouvenot
 *
 */
@Configuration
@PropertySource("classpath:mail.properties")
public class MailConfig {

    @Value("${mail.protocol}")
    private String protocol;
    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.smtp.auth}")
    private boolean auth;
    @Value("${mail.smtp.starttls.enable}")
    private boolean starttls;
    @Value("${mail.from}")
    private String from;
    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.debug:false}")
    private String debug;
    @Value("${mail.smtp.ssl.trust}")
    private String ssltrust;
    @Value("${mail.smtp.ssl.protocols}")
    private String sslprotocol;
    /**
     * Configuration du service
     *
     * @return mailSender
     */
    /*
    @Bean
    public JavaMailSender javaMailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        final Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", auth);
        mailProperties.put("mail.smtp.starttls.enable", starttls);
        mailProperties.put("mail.debug", debug);
        if(StringUtils.isNotEmpty(ssltrust)) {
        	mailProperties.put("mail.smtp.ssl.trust", ssltrust);
        }
        mailProperties.put("mail.smtp.ssl.protocols", sslprotocol);
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setProtocol(protocol);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        return mailSender;
    }
    */
    /**
     * Permet d'utiliser les templates HTML avec Thymeleaf pour l'envoi de mail
     *
     * @return templateResolver
     */
    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver(){
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);

        return templateResolver;
    }
}