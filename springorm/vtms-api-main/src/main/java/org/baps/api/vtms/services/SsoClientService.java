package org.baps.api.vtms.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SsoClientService {

    private static final String VALIDATE_TOKEN_ENDPOINT = "/user/validate/token";

    @Autowired
    @Qualifier("ssoWebClient")
    private WebClient ssoWebClient;

    public boolean isValidateToken(final String token) {
        try {
            final ResponseEntity<Void> responseEntity = ssoWebClient
                .post()
                .uri(VALIDATE_TOKEN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toBodilessEntity()
                .block();

            return responseEntity != null && responseEntity.getStatusCode().equals(HttpStatus.OK);

        } catch (final WebClientResponseException e) {
            logException(e);
            if (HttpStatus.FORBIDDEN == e.getStatusCode()) {
                return false;
            }
            throw e;
        } catch (final Exception e) {
            logException(e);
            throw e;
        }
    }

    private void logException(final WebClientResponseException e) {
        log.error("WebClientResponseException >> Response code: {}, Response body: {}", e.getStatusCode(), e.getResponseBodyAsString());
    }

    private void logException(final Exception e) {
        log.error("Exception", e);
    }
}
