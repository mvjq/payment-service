package org.example.paymentservice.application.service;

import jakarta.transaction.Transactional;
import org.example.paymentservice.domain.DomainMapper;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;
import org.example.paymentservice.application.command.WebhookCommand;
import org.example.paymentservice.application.port.in.WebhookUseCase;
import org.example.paymentservice.domain.port.out.WebhookRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WebhookService implements WebhookUseCase {

    private final WebhookRepositoryPort webhookRepositoryPort;
    private final DomainMapper mapper;

    public WebhookService(WebhookRepositoryPort webhookRepositoryPort, DomainMapper mapper) {
        this.webhookRepositoryPort = webhookRepositoryPort;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Webhook execute(WebhookCommand command) {

        Webhook webhook = mapper.toDomain(command);
        // save
        // update
        // etc

        ///  execute to WebhookResponse
        return webhookRepositoryPort.save(webhook);
    }
    @Override
    @Transactional
    public Webhook update(UUID uuid, WebhookEntity webhookEntity) {
        Webhook found = webhookRepositoryPort.findByUUID(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Webhook not found"));

        // found.update(webhookEntity);

        return webhookRepositoryPort.save(found);
    }

    @Override
    @Transactional
    public void deleteWebhook(UUID uuid) {
        webhookRepositoryPort.deleteById(uuid);
    }


    @Override
    @Transactional
    public void disableWebhook(UUID uuid) {
        Webhook found = webhookRepositoryPort.findByUUID(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Webhook not found"));
        found.deactivate();
        webhookRepositoryPort.save(found);
    }

    @Override
    public Webhook findWebhookByPaymentUUID(UUID paymentId) {
        return webhookRepositoryPort.findbyPaymentUUID(paymentId)
                // TODO add custom exception
                .orElseThrow(() -> new IllegalArgumentException("Webhook not found for " + paymentId));
    }

    @Override
    public List<Webhook> findAllWebhooks() {
        return webhookRepositoryPort.findAll();
    }
}
