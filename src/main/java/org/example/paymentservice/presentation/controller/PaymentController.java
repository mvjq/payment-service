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
import org.example.paymentservice.presentation.exception.GlobalExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs for processing payment transactions")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;
    private final CommandMapper mapper;

    @PostMapping
    @Operation(
            summary = "Create a new payment",
            description = "Process a new payment transaction with customer and card details. The card number will be encrypted and masked in the response."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<PaymentResponse> createPayment(@Validated @RequestBody PaymentRequest request) {

        PaymentCommand command = mapper.toCommand(request);
        PaymentResponse response = paymentUseCase.execute(command);

        return ResponseEntity.created(URI.create("/api/v1/payments")).body(response);
    }
}
