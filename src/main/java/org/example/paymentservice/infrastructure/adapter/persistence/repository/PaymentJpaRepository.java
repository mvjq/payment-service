package org.example.paymentservice.infrastructure.adapter.persistence.repository;

import org.example.paymentservice.domain.model.Payment;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
}
