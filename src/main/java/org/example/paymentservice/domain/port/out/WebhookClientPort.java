package org.example.paymentservice.domain.port.out;

import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.infrastructure.adapter.external.WebhookClientResponse;
import org.example.paymentservice.presentation.dto.WebhookResponse;

public interface WebhookClientPort {
    WebhookClientResponse sendWebhook(String url, String payload, String secret);
}
