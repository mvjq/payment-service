package org.example.paymentservice.domain.port.out;

import org.example.paymentservice.domain.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {
    Payment save(Payment Payment);
    Optional<Payment> findByUUID(UUID id);
    List<Payment> findAll();
}
