package org.example.paymentservice.infrastructure.adapter.persistence.repository;

import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebhookHistoryJpaRepository extends JpaRepository<WebhookHistoryEntity, UUID> {
}
