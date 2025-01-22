package org.baps.api.vtms.services;

import org.baps.api.vtms.models.notification.NotificationCampaignModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import reactor.core.publisher.Mono;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClientService implements NotificationService {
    private static final String NOTIFICATION_CLIENT_KEY_HEADER = "x-app-auth-id";
    private static final String NOTIFICATION_CLIENT_SECRET_HEADER = "x-app-auth-secret";
    private static final String INBOX_CAMPAIGN_SEND_ENDPOINT = "/inbox/campaigns/send";

    @Value("${notification.client.key}")
    private String notificationClientKey;

    @Value("${notification.client.secret}")
    private String notificationClientSecret;

    @Value("${notification.enable}")
    private boolean notificationEnable;

    private final Supplier<HttpHeaders> notificationClientHeader = () -> {
        final MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        headersMap.add(NOTIFICATION_CLIENT_KEY_HEADER, notificationClientKey);
        headersMap.add(NOTIFICATION_CLIENT_SECRET_HEADER, notificationClientSecret);
        return HttpHeaders.readOnlyHttpHeaders(headersMap);
    };

    private final WebClient notificationWebClient;

    /**
     * Asynchronously sends a notification campaign if notification enabling is active.
     *
     * @param notificationCampaignModel The model containing the notification campaign details to be sent.
     */
    @Override
    public void sendCampaign(final NotificationCampaignModel notificationCampaignModel) {

        if (notificationEnable) {

            try {
                final Mono<ResponseEntity<Object>> responseEntityMono = notificationWebClient
                    .post()
                    .uri(INBOX_CAMPAIGN_SEND_ENDPOINT)
                    .headers(httpHeaders -> httpHeaders.addAll(notificationClientHeader.get()))
                    .bodyValue(notificationCampaignModel)
                    .retrieve().toEntity(Object.class);

                responseEntityMono.subscribe(
                    responseEntity -> log.info("Campaign sent successfully. Campaign run ID: {}", responseEntity.getBody()),
                    throwable -> {
                        if (throwable instanceof WebClientResponseException webClientResponseException) {
                            log.error("Campaign send failed with status code: {} and response body: {}",
                                webClientResponseException.getStatusCode(),
                                webClientResponseException.getResponseBodyAsString());
                        } else {
                            log.error("Campaign send failed {}", throwable.getMessage());
                        }
                    }
                );
            } catch (final Exception e) {
                log.error("Error sending campaign: {}", e.getMessage());
            }

        } else {
            log.info("Notification disable. {}", notificationCampaignModel);
        }
    }
}
