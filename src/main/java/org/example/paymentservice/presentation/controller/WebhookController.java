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
import org.example.paymentservice.presentation.exception.GlobalExceptionHandler;
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
@Tag(name = "Webhook", description = "Webhook management APIs for registering and managing webhook endpoints")
public class WebhookController {

    private final WebhookUseCase webhookUseCase;
    private final CommandMapper mapper;

    @PostMapping
    @Operation(
            summary = "Register a new webhook",
            description = "Register a new webhook endpoint to receive notifications about payment events"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Webhook registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebhookResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<WebhookResponse> registerWebhook(@Valid @RequestBody WebhookRequest request) {
        log.info("Registering webhook: {}", request.getUrl());


        WebhookCommand command = mapper.toCommand(request);


        Webhook webhook = webhookUseCase.create(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(WebhookResponse.fromDomain(webhook));
    }

    @GetMapping
    @Operation(
            summary = "Get all webhooks",
            description = "Retrieve a list of all registered webhooks"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Webhooks retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebhookResponse.class)
                    )
            )
    })
    public ResponseEntity<List<WebhookResponse>> findAllWebhooks() {
        List<Webhook> webhookEntities = webhookUseCase.findAllWebhooks();
        List<WebhookResponse> response = webhookEntities.stream()
                .map(WebhookResponse::fromDomain)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a webhook",
            description = "Update an existing webhook configuration"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or webhook not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity updateWebhook(
            @Parameter(description = "Webhook ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody WebhookRequest request) {

        WebhookEntity webhookEntityUpdate = WebhookEntity.builder()
                .url(request.getUrl())
                .secret(request.getSecret())
                .isActive(request.getActive())
                .build();

        // Webho ok webhook = webhookUseCase.update(id, webhookEntityUpdate);
        
//        return ResponseEntity.status(HttpStatus.OK).body(WebhookResponse.fromDomain(webhookEntity));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a webhook",
            description = "Delete a webhook by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Webhook deleted successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Webhook not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> deleteWebhook(
            @Parameter(description = "Webhook ID", required = true) @PathVariable UUID id) {
        webhookUseCase.deleteWebhook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/disable")
    @Operation(
            summary = "Disable a webhook",
            description = "Disable a webhook to stop receiving notifications"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Webhook disabled successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Webhook not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> disableWebhook(
            @Parameter(description = "Webhook ID", required = true) @PathVariable UUID id) {
        webhookUseCase.disableWebhook(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
