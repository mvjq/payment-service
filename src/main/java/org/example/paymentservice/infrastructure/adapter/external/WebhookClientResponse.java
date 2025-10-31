package org.example.paymentservice.infrastructure.adapter.external;

import lombok.Builder;
import lombok.Data;

@Builder
public record WebhookClientResponse(int statusCode, String body, boolean success, String errorMessage) {}
