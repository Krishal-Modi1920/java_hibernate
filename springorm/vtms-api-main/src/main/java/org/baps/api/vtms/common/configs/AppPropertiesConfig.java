package org.baps.api.vtms.common.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configuration
@ConditionalOnProperty(name = "external.properties.enabled", havingValue = "true")
@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
public class AppPropertiesConfig {

    // Path to properties (secrets) file injected by hashicorp vault on our cloud env
    private static final String EXTERNAL_CONFIG_FILE_PATH = "/vault/secrets/application.properties";

    /**
     * Reads external properties file at the time of application start up.
     * @return Returns PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        final PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new FileSystemResource(EXTERNAL_CONFIG_FILE_PATH));
        return configurer;
    }
}
