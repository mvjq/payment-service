package org.example.paymentservice.infrastructure.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.model.WebhookHistory;
import org.example.paymentservice.domain.port.in.WebhookCommand;
import org.example.paymentservice.domain.port.in.WebhookUseCase;
import org.example.paymentservice.infrastructure.adapter.in.rest.dto.WebhookRequest;
import org.example.paymentservice.infrastructure.adapter.in.rest.dto.WebhookResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookUseCase webhookUseCase;

    @PostMapping
    public ResponseEntity<WebhookResponse> registerWebhook(@RequestBody WebhookRequest request) {
        log.info("Registering webhook: {}", request.getUrl());
        
        WebhookCommand command = WebhookCommand.builder()
                .url(request.getUrl())
                .secret(request.getSecret())
                .description(request.getDescription())
                .build();
        
        Webhook webhook = webhookUseCase.execute(command);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebhookResponse.fromDomain(webhook));
    }

    @GetMapping
    public ResponseEntity<List<WebhookResponse>> findAllWebhooks() {
        List<Webhook> webhooks = webhookUseCase.findAllWebhooks();
        List<WebhookResponse> response = webhooks.stream()
                .map(WebhookResponse::fromDomain)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebhookResponse> findWebhook(@PathVariable UUID id) {
        Webhook webhook = webhookUseCase.findWebhook(id);
        
        return ResponseEntity.status(HttpStatus.FOUND).body(WebhookResponse.fromDomain(webhook));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebhookResponse> updateWebhook(
            @PathVariable UUID id,
            @RequestBody WebhookRequest request) {

        Webhook webhookUpdate = Webhook.builder()
                .url(request.getUrl())
                .secret(request.getSecret())
                .isActive(request.getActive())
                .build();
        
        Webhook webhook = webhookUseCase.update(id, webhookUpdate);
        
        return ResponseEntity.status(HttpStatus.OK).body(WebhookResponse.fromDomain(webhook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWebhook(@PathVariable UUID id) {
        webhookUseCase.deleteWebhook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<Void> disableWebhook(@PathVariable UUID id) {
        webhookUseCase.disableWebhook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<WebhookHistory>> findWebhookHistory(@PathVariable UUID id) {

        List<WebhookHistory> history = webhookUseCase.findWebhookHistory(id);
        
        return ResponseEntity.status(HttpStatus.FOUND).body(history);
    }

    @GetMapping("/payments/{paymentId}/history")
    public ResponseEntity<List<WebhookHistory>> findPaymentWebhookHistory(@PathVariable UUID paymentId) {

        List<WebhookHistory> history = webhookUseCase.findPaymentWebhookHistory(paymentId);
        
        return ResponseEntity.status(HttpStatus.FOUND).body(history);
    }
}
