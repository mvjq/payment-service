package org.example.paymentservice.infrastructure.adapter.persistence.adapter;

import org.example.paymentservice.domain.DomainMapper;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.port.out.WebhookRepositoryPort;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.WebhookJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WebhookRepositoryAdapter implements WebhookRepositoryPort {

    private final DomainMapper mapper;
    private final WebhookJpaRepository webhookJpaRepository;

    public WebhookRepositoryAdapter(DomainMapper mapper, WebhookJpaRepository webhookJpaRepository) {
        this.mapper = mapper;
        this.webhookJpaRepository = webhookJpaRepository;
    }

    @Override
    public Webhook save(Webhook webhook) {
        return null;
    }

    @Override
    public Optional<Webhook> findByUUID(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<Webhook> findbyPaymentUUID(UUID paymentId) {
        return Optional.empty();
    }

    @Override
    public List<Webhook> findByActiveTrue() {
        return List.of();
    }

    @Override
    public List<Webhook> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }
}
