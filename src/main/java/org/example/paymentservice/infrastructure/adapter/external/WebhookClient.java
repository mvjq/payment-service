package org.example.paymentservice.infrastructure.adapter.external;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.port.out.WebhookClientPort;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.awt.*;


@Slf4j
@Component
public class WebhookClient implements WebhookClientPort {

    private final RestTemplate restTemplate;

    public WebhookClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public WebhookClientResponse sendWebhook(String url, String payload, String secret) {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (secret != null) {
            String signature = createSignature(secret);
            httpHeaders.set("X-Webhook-Signature", signature);
        }

        HttpEntity<String> request = new HttpEntity<>(payload, httpHeaders);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            return WebhookClientResponse.builder()
                    .statusCode(response.getStatusCode().value())
                    .body(response.getBody())
                    .success(true)
                    .build();

        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            log.error("Webhook failed to delived error [{}] status [{}]", exception.getMessage(), exception.getStatusCode());
            return WebhookClientResponse.builder()
                    .statusCode(exception.getStatusCode().value())
                    .body(exception.getResponseBodyAsString())
                    .errorMessage(exception.getMessage())
                    .success(false)
                    .build();

        } catch (Exception exception) {
            log.error("Webhook failed to delived error [{}]", exception.getMessage());
            return WebhookClientResponse.builder()
                    .errorMessage(exception.getMessage())
                    .success(false)
                    .build();

        }
    }

    // TODO implements this signature (find better algorithm)
    private String createSignature(String secret) {
        return null;
    }
}
