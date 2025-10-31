package org.example.paymentservice.application.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.domain.DomainMapper;
import org.example.paymentservice.domain.event.PaymentCreatedEvent;
import org.example.paymentservice.domain.model.Payment;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.PaymentEntity;
import org.example.paymentservice.application.command.PaymentCommand;
import org.example.paymentservice.application.port.in.PaymentUseCase;
import org.example.paymentservice.domain.port.out.EventStorePort;
import org.example.paymentservice.domain.port.out.PaymentRepositoryPort;
import org.example.paymentservice.presentation.dto.PaymentResponse;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class PaymentService implements PaymentUseCase {

    private final PaymentRepositoryPort paymentPort;
    private final EncryptionService encryptionService;
    private final EventStorePort eventStorePort;
    private final DomainMapper mapper;


    public PaymentService(PaymentRepositoryPort paymentPort, EncryptionService encryptionService, EventStorePort eventStorePort, DomainMapper mapper) {
        this.paymentPort = paymentPort;
        this.encryptionService = encryptionService;
        this.eventStorePort = eventStorePort;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public PaymentResponse execute(PaymentCommand command) {

        Payment payment = mapper.toDomain(command);
        Payment saved = paymentPort.save(payment);
        log.info("Payment saved with ID [{}]", saved.getId());

        PaymentCreatedEvent createdEvent = PaymentCreatedEvent
                .create(
                        saved.getId(),
                        saved.getFirstName(),
                        saved.getLastName(),
                        saved.getZipCode(),
                        saved.getEncryptedCardNumber()
                );

//        eventStorePort.store(createdEvent);
        log.info("PaymentCreatedEvent stored for Payment ID [{}]", saved.getId());

        return mapper.fromDomain(saved);
    }
}
