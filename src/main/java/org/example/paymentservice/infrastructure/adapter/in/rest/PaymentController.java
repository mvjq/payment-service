package org.example.paymentservice.infrastructure.adapter.in.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.port.in.PaymentUseCase;
import org.example.paymentservice.infrastructure.adapter.in.rest.dto.PaymentRequest;
import org.example.paymentservice.infrastructure.adapter.in.rest.dto.PaymentResponse;
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

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Validated @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentUseCase.createPayment(request.toCommand());

        return ResponseEntity.created(URI.create("/api/v1/payments")).body(response);
    }
}
