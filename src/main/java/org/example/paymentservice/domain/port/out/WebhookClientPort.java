package org.example.paymentservice.domain.port.out;

import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.presentation.dto.WebhookResponse;

public interface WebhookClientPort {
    WebhookResponse sendWebhook(String url, Webhook payload);
}
