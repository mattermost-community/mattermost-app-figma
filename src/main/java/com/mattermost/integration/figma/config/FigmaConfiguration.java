package com.mattermost.integration.figma.config;

import com.mattermost.integration.figma.config.exception.handler.FigmaRestErrorHandler;
import com.mattermost.integration.figma.config.exception.handler.MMRestErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

@Configuration
public class FigmaConfiguration {

    @Value("${encryption.key}")
    private String encryptionKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = "mmRestTemplate")
    public RestTemplate mmRestTemplate() {
        return new RestTemplateBuilder().errorHandler(new MMRestErrorHandler()).build();
    }

    @Bean(name = "figmaRestTemplate")
    public RestTemplate figmaRestTemplate() {
        return new RestTemplateBuilder().errorHandler(new FigmaRestErrorHandler()).build();
    }

    @Bean
    public SecretKeySpec generateKey() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] key = encryptionKey.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        key = messageDigest.digest(key);
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, "AES");
    }

    //configuring default locale
    @Bean
    public LocaleResolver localeResolver()
    {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }
    //configuring ResourceBundle
    @Bean
    public ResourceBundleMessageSource messageSource()
    {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
