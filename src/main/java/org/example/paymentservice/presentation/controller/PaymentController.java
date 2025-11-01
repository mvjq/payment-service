package org.example.paymentservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.application.command.PaymentCommand;
import org.example.paymentservice.application.port.in.PaymentUseCase;
import org.example.paymentservice.presentation.CommandMapper;
import org.example.paymentservice.presentation.dto.PaymentRequest;
import org.example.paymentservice.presentation.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Processing", description = "APIs for processing payment transactions")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;
    private final CommandMapper mapper;

    @Operation(summary = "Create a payment", description = "Processes a new payment transaction and triggers webhook notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment successfully created",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payment request",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Webhook not found",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Payment processing failed",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Validated @RequestBody PaymentRequest request) {

        PaymentCommand command = mapper.toCommand(request);
        PaymentResponse response = paymentUseCase.execute(command);

        return ResponseEntity.created(URI.create("/api/v1/payments")).body(response);
    }
}
