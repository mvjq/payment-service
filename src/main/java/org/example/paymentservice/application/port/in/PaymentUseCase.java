package org.example.paymentservice.application.port.in;

import org.example.paymentservice.application.command.PaymentCommand;
import org.example.paymentservice.presentation.dto.PaymentResponse;

public interface PaymentUseCase {
    PaymentResponse execute(PaymentCommand command);
}
