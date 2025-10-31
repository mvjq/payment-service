package org.example.paymentservice.infrastructure.adapter.persistence.adapter;

import org.example.paymentservice.domain.DomainMapper;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.port.out.WebhookRepositoryPort;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.WebhookJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WebhookRepositoryAdapter implements WebhookRepositoryPort {

    private final DomainMapper mapper;
    private final WebhookJpaRepository repository;

    public WebhookRepositoryAdapter(DomainMapper mapper, WebhookJpaRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public Webhook save(Webhook webhook) {
        WebhookEntity entity = mapper.toEntity(webhook);
        WebhookEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    @Override
    public Optional<Webhook> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Webhook> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
