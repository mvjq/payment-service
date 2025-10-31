package org.example.paymentservice.infrastructure.adapter.persistence.repository;

import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebhookJpaRepository extends JpaRepository<WebhookEntity, UUID> {
}
