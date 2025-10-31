package org.example.paymentservice.presentation;

import jakarta.validation.Valid;
import org.example.paymentservice.application.command.PaymentCommand;
import org.example.paymentservice.application.command.WebhookCommand;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.presentation.dto.PaymentRequest;
import org.example.paymentservice.presentation.dto.WebhookRequest;
import org.springframework.stereotype.Component;

@Component
public class CommandMapper {

    public PaymentCommand toCommand(PaymentRequest request) {
        return PaymentCommand.builder()
                .webhookUUID(request.webhokUUID())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .zipCode(request.zipCode())
                .cardNumber(request.cardNumber())
                .build();
    }

    public WebhookCommand toCommand(WebhookRequest request) {
        return WebhookCommand.builder()
                .url(request.getUrl())
                .secret(request.getSecret())
                .description(request.getDescription())
                .build();
    }

}
