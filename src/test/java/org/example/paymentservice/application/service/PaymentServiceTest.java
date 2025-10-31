package org.example.paymentservice.application.service;

import org.example.paymentservice.application.command.PaymentCommand;
import org.example.paymentservice.domain.DomainMapper;
import org.example.paymentservice.domain.event.PaymentCreatedEvent;
import org.example.paymentservice.domain.model.Payment;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.port.out.EventStorePort;
import org.example.paymentservice.domain.port.out.PaymentRepositoryPort;
import org.example.paymentservice.domain.port.out.WebhookRepositoryPort;
import org.example.paymentservice.presentation.dto.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    public static final String FIRST_NAME = "Marcos";
    public static final String LAST_NAME = "Vinicius";
    public static final String ZIP = "12345";
    public static final String CARD_NUMBER = "4444444444444";
    @Mock
    private PaymentRepositoryPort paymentPort;

    @Mock
    private WebhookRepositoryPort webhookRepository;

    @Mock
    private EventStorePort eventStorePort;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private PaymentService paymentService;

    private UUID webhookId;
    private PaymentCommand command;
    private Webhook webhook;
    private Payment payment;

    @BeforeEach
    void setUp() {
        webhookId = UUID.randomUUID();
        
        command = PaymentCommand.builder()
                .webhookUUID(webhookId)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .zipCode(ZIP)
                .cardNumber(CARD_NUMBER)
                .build();

        webhook = Webhook.builder()
                .id(webhookId)
                .url("http://example.com/webhook")
                .isActive(true)
                .build();

        payment = Payment.builder()
                .id(UUID.randomUUID())
                .webhookId(webhookId)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .zipCode(ZIP)
                .encryptedCardNumber("encrypted")
                .build();
    }

    @Test
    void shouldCreatePaymentSuccessfully() {
        when(webhookRepository.findById(webhookId)).thenReturn(Optional.of(webhook));
        when(mapper.toDomain(command, webhook)).thenReturn(payment);
        when(paymentPort.save(payment)).thenReturn(payment);
        
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .id(payment.getId())
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .zipCode(ZIP)
                .maskedCardNumber("**** **** **** 1111")
                .build();
        when(mapper.fromDomain(payment)).thenReturn(expectedResponse);

        PaymentResponse response = paymentService.execute(command);

        assertNotNull(response);
        verify(webhookRepository).findById(webhookId);
        verify(paymentPort).save(payment);
        verify(eventStorePort).store(any(PaymentCreatedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenWebhookNotFound() {
        when(webhookRepository.findById(webhookId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            paymentService.execute(command)
        );

        verify(paymentPort, never()).save(any());
        verify(eventStorePort, never()).store(any());
    }

    @Test
    void shouldThrowExceptionWhenWebhookNotActive() {
        webhook.setIsActive(false);
        when(webhookRepository.findById(webhookId)).thenReturn(Optional.of(webhook));

        assertThrows(IllegalStateException.class, () -> 
            paymentService.execute(command)
        );

        verify(paymentPort, never()).save(any());
        verify(eventStorePort, never()).store(any());
    }
}
