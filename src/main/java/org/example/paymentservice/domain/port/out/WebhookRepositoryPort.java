package org.example.paymentservice.domain.port.out;

import org.example.paymentservice.domain.model.Webhook;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookRepositoryPort {
    Webhook save(Webhook webhook);
    Optional<Webhook> findByUUID(UUID id);
    Optional<Webhook> findbyPaymentUUID(UUID paymentId);
    List<Webhook> findByActiveTrue();
    List<Webhook> findAll();
    void deleteById(UUID id);
}
