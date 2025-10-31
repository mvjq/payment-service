package org.example.paymentservice.infrastructure.adapter.persistence.adapter;

import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.DomainMapper;
import org.example.paymentservice.domain.model.Payment;
import org.example.paymentservice.domain.port.out.PaymentRepositoryPort;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.PaymentEntity;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.PaymentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final DomainMapper mapper;
    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentRepositoryAdapter(DomainMapper mapper, PaymentJpaRepository paymentJpaRepository) {
        this.mapper = mapper;
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override
    public Payment save(Payment Payment) {
        PaymentEntity entity  = mapper.toEntity(Payment);
        PaymentEntity saved = paymentJpaRepository.save(entity);
        log.info("PaymentEntity saved with ID [{}]", saved.getId());
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findByUUID(UUID id) {
        return paymentJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return paymentJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
