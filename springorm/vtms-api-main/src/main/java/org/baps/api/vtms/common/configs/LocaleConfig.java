package org.baps.api.vtms.common.configs;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    private static final int MESSAGE_SOURCE_CACHE_SECONDS = 10;

    /**
     * Configures and returns a {@link LocaleResolver} bean that determines the user's locale based
     * on the "Accept-Language" header and defaults to the US locale.
     *
     * @return The configured locale resolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        final AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    /**
     * Configures and returns a {@link MessageSource} bean that reads internationalized messages from
     * property files on the classpath. Messages are cached for a specified duration to improve performance.
     *
     * @return The configured message source
     */
    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
        resource.setBasename("classpath:messages");
        resource.setDefaultEncoding("UTF-8");
        resource.setUseCodeAsDefaultMessage(true);
        resource.setDefaultLocale(Locale.US);
        resource.setCacheSeconds(MESSAGE_SOURCE_CACHE_SECONDS);
        return resource;
    }

    /**
     * Configures and returns a {@link Validator} bean that uses the message source for validation messages.
     *
     * @return The configured validator
     */
    @Bean
    public Validator getValidator() {
        final LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}
