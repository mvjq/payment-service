package org.example.paymentservice.domain.port.in;

import org.example.paymentservice.infrastructure.adapter.in.rest.dto.PaymentResponse;

public interface PaymentUseCase {
    PaymentResponse execute(PaymentCommand command);
}
