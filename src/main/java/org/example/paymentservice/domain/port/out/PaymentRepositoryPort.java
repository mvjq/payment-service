package org.example.paymentservice.domain.port.out;

import org.example.paymentservice.domain.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepositoryPort {
    Payment save(Payment Payment);
    Optional<Payment> findById(Long id);
    List<Payment> findAll();
}
