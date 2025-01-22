package org.baps.api.vtms;

import org.baps.api.vtms.constants.GeneralConstant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
//@EnableCaching
@EnableJpaAuditing
@EnableScheduling
@SecurityScheme(name = HttpHeaders.AUTHORIZATION,
    description = "Please enter token from SSO token",
    paramName = HttpHeaders.AUTHORIZATION,
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER)
@SecurityScheme(name = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY,
    description = "Please enter public token",
    paramName = GeneralConstant.X_APP_PUBLIC_TOKEN_KEY,
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER)
public class VTMSApplication {

    public static void main(final String[] args) {
        SpringApplication.run(VTMSApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {

        final String docLogoPath = "/vtms/internal/assets/logo.png";

        final Map<String, String> logo = new HashMap<>();
        logo.put("url", docLogoPath);
        logo.put("backgroundColor", "#FFFFFF");
        logo.put("altText", "VTMS Logo");

        final Map<String, Object> infoExtensions = new HashMap<>();
        infoExtensions.put("x-logo", logo);

        return new OpenAPI()
            .info(
                new Info()
                    .title("Visit Tour Management System API")
                    .extensions(infoExtensions)
            );
    }
}
