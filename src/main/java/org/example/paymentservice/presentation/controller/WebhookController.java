package org.example.paymentservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Webhook Management", description = "APIs for managing webhooks for payment notifications")
public class WebhookController {

    private final WebhookUseCase webhookUseCase;
    private final CommandMapper mapper;

    @Operation(summary = "Register a new webhook", description = "Creates a new webhook endpoint to receive payment notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Webhook successfully registered",
                    content = @Content(schema = @Schema(implementation = WebhookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Webhook URL already exists",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<WebhookResponse> registerWebhook(@Valid @RequestBody WebhookRequest request) {
        log.info("Registering webhook: {}", request.getUrl());


        WebhookCommand command = mapper.toCommand(request);


        Webhook webhook = webhookUseCase.create(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebhookResponse.fromDomain(webhook));
    }

    @Operation(summary = "Get all webhooks", description = "Retrieves a list of all registered webhooks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Successfully retrieved webhooks list",
                    content = @Content(schema = @Schema(implementation = WebhookResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<WebhookResponse>> findAllWebhooks() {
        List<Webhook> webhookEntities = webhookUseCase.findAllWebhooks();
        List<WebhookResponse> response = webhookEntities.stream()
                .map(WebhookResponse::fromDomain)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @Operation(summary = "Update a webhook", description = "Updates an existing webhook configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook successfully updated",
                    content = @Content(schema = @Schema(implementation = WebhookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Webhook not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<WebhookResponse> updateWebhook(
            @Parameter(description = "Webhook ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody WebhookRequest request) {

        WebhookEntity webhookEntityUpdate = WebhookEntity.builder()
                .url(request.getUrl())
                .secret(request.getSecret())
                .isActive(request.getActive())
                .build();

         Webhook webhook = webhookUseCase.update(id, webhookEntityUpdate);
        
        return ResponseEntity.status(HttpStatus.OK).body(WebhookResponse.fromDomain(webhook));
    }

    @Operation(summary = "Delete a webhook", description = "Permanently removes a webhook from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Webhook successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Webhook not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWebhook(
            @Parameter(description = "Webhook ID", required = true) @PathVariable UUID id) {
        webhookUseCase.deleteWebhook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Disable a webhook", description = "Disables a webhook without deleting it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Webhook successfully disabled"),
            @ApiResponse(responseCode = "404", description = "Webhook not found",
                    content = @Content)
    })
    @PostMapping("/{id}/disable")
    public ResponseEntity<Void> disableWebhook(
            @Parameter(description = "Webhook ID", required = true) @PathVariable UUID id) {
        webhookUseCase.disableWebhook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
