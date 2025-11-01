package org.example.paymentservice.presentation.controller;

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
public class PaymentController {

    private final PaymentUseCase paymentUseCase;
    private final CommandMapper mapper;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Validated @RequestBody PaymentRequest request) {

        PaymentCommand command = mapper.toCommand(request);
        PaymentResponse response = paymentUseCase.execute(command);

        return ResponseEntity.created(URI.create("/api/v1/payments")).body(response);
    }
}
