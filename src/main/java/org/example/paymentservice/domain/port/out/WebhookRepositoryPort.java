package org.example.paymentservice.domain.port.out;

import org.example.paymentservice.domain.model.Webhook;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookRepositoryPort {
    Webhook save(Webhook webhook);

    Optional<Webhook> findById(UUID id);
    List<Webhook> findAll();

    void deleteById(UUID id);
}
