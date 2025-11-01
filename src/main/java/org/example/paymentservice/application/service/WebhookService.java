package org.example.paymentservice.application.service;

import jakarta.transaction.Transactional;
import org.example.paymentservice.domain.DomainMapper;
import org.example.paymentservice.domain.model.Webhook;
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
    public Webhook create(WebhookCommand command) {

        Webhook webhook = mapper.toDomain(command);
        return webhookRepositoryPort.save(webhook);
    }
    @Override
    @Transactional
    public Webhook update(UUID uuid, WebhookCommand command) {
        Webhook found = webhookRepositoryPort.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Webhook not found"));

        found.update(
            command.getUrl(),
            command.getDescription(),
            command.getSecret(),
            null
        );

        return webhookRepositoryPort.save(found);
    }

    @Override
    @Transactional
    public void deleteWebhook(UUID uuid) {
        webhookRepositoryPort.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Webhook not found"));
        webhookRepositoryPort.deleteById(uuid);
    }


    @Override
    @Transactional
    public void disableWebhook(UUID uuid) {
        Webhook found = webhookRepositoryPort.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Webhook not found"));
        found.deactivate();
        webhookRepositoryPort.save(found);
    }

    @Override
    public Webhook findWebhookByPaymentId(UUID paymentId) {
        return webhookRepositoryPort.findById(paymentId)
                // TODO add custom exception
                .orElseThrow(() -> new IllegalArgumentException("Webhook not found for " + paymentId));
    }

    @Override
    public List<Webhook> findAllWebhooks() {
        return webhookRepositoryPort.findAll();
    }
}
