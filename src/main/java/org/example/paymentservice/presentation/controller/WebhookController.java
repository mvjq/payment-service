package org.example.paymentservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;
import org.example.paymentservice.application.command.WebhookCommand;
import org.example.paymentservice.application.port.in.WebhookUseCase;
import org.example.paymentservice.presentation.CommandMapper;
import org.example.paymentservice.presentation.dto.WebhookRequest;
import org.example.paymentservice.presentation.dto.WebhookResponse;
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
    private final CommandMapper mapper;

    @PostMapping
    public ResponseEntity<WebhookResponse> registerWebhook(@Valid @RequestBody WebhookRequest request) {
        log.info("Registering webhook: {}", request.getUrl());


        WebhookCommand command = mapper.toCommand(request);


        Webhook webhook = webhookUseCase.create(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebhookResponse.fromDomain(webhook));
    }

    @GetMapping
    public ResponseEntity<List<WebhookResponse>> findAllWebhooks() {
        List<Webhook> webhookEntities = webhookUseCase.findAllWebhooks();
        List<WebhookResponse> response = webhookEntities.stream()
                .map(WebhookResponse::fromDomain)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateWebhook(
            @PathVariable UUID id,
            @Valid @RequestBody WebhookRequest request) {

        WebhookEntity webhookEntityUpdate = WebhookEntity.builder()
                .url(request.getUrl())
                .secret(request.getSecret())
                .isActive(request.getActive())
                .build();

         Webhook webhook = webhookUseCase.update(id, webhookEntityUpdate);
        
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
}
