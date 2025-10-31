package org.example.paymentservice.application.service;

import org.example.paymentservice.application.command.WebhookCommand;
import org.example.paymentservice.domain.DomainMapper;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.port.out.WebhookRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    public static final String HTTP_EXAMPLE_COM_WEBHOOK = "http://example.com/webhooking";
    public static final String TEST_WEBHOOK = "Test webhookign";
    public static final String SECRET = "verygoodsecret";
    @Mock
    private WebhookRepositoryPort webhookRepositoryPort;

    @Mock
    private DomainMapper mapper;

    @InjectMocks
    private WebhookService webhookService;

    private Webhook webhook;
    private UUID webhookId;

    @BeforeEach
    void setUp() {
        webhookId = UUID.randomUUID();
        webhook = Webhook.builder()
                .id(webhookId)
                .url(HTTP_EXAMPLE_COM_WEBHOOK)
                .isActive(true)
                .build();
    }

    @Test
    void shouldCreateWebhook() {
        WebhookCommand command = WebhookCommand.builder()
                .url(HTTP_EXAMPLE_COM_WEBHOOK)
                .description(TEST_WEBHOOK)
                .secret(SECRET)
                .build();

        when(mapper.toDomain(command)).thenReturn(webhook);
        when(webhookRepositoryPort.save(webhook)).thenReturn(webhook);

        Webhook result = webhookService.create(command);

        assertNotNull(result);
        verify(webhookRepositoryPort).save(webhook);
    }

    @Test
    void shouldDisableWebhook() {
        when(webhookRepositoryPort.findById(webhookId)).thenReturn(Optional.of(webhook));
        when(webhookRepositoryPort.save(any())).thenReturn(webhook);

        webhookService.disableWebhook(webhookId);

        assertFalse(webhook.getIsActive());
        verify(webhookRepositoryPort).save(webhook);
    }

    @Test
    void shouldThrowExceptionWhenDisablingNonExistentWebhook() {
        when(webhookRepositoryPort.findById(webhookId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            webhookService.disableWebhook(webhookId)
        );
    }

    @Test
    void shouldDeleteWebhook() {
        doNothing().when(webhookRepositoryPort).deleteById(webhookId);

        webhookService.deleteWebhook(webhookId);

        verify(webhookRepositoryPort).deleteById(webhookId);
    }

    @Test
    void shouldFindAllWebhooks() {
        List<Webhook> webhooks = List.of(webhook);
        when(webhookRepositoryPort.findAll()).thenReturn(webhooks);

        List<Webhook> result = webhookService.findAllWebhooks();

        assertEquals(1, result.size());
        verify(webhookRepositoryPort).findAll();
    }
}
