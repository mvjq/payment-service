package org.example.paymentservice.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WebhookTest {

    public static final String HTTP_EXAMPLE_COM = "http://example.com";

    @Test
    void shouldActivateWebhook() {
        Webhook webhook = Webhook.builder()
                .id(UUID.randomUUID())
                .url(HTTP_EXAMPLE_COM)
                .isActive(false)
                .build();

        webhook.activate();

        assertTrue(webhook.getIsActive());
    }

    @Test
    void shouldDeactivateWebhook() {
        Webhook webhook = Webhook.builder()
                .id(UUID.randomUUID())
                .url(HTTP_EXAMPLE_COM)
                .isActive(true)
                .build();

        webhook.deactivate();

        assertFalse(webhook.getIsActive());
    }

    @Test
    void shouldReturnTrueWhenCanReceiveEvents() {
        Webhook webhook = Webhook.builder()
                .id(UUID.randomUUID())
                .url(HTTP_EXAMPLE_COM)
                .isActive(true)
                .build();

        assertTrue(webhook.canReceiveEvents());
    }

    @Test
    void shouldReturnFalseWhenCannotReceiveEvents() {
        Webhook webhook = Webhook.builder()
                .id(UUID.randomUUID())
                .url(HTTP_EXAMPLE_COM)
                .isActive(false)
                .build();

        assertFalse(webhook.canReceiveEvents());
    }

    @Test
    void shouldReturnFalseWhenIsActiveIsNull() {
        Webhook webhook = Webhook.builder()
                .id(UUID.randomUUID())
                .url(HTTP_EXAMPLE_COM)
                .isActive(null)
                .build();

        assertFalse(webhook.canReceiveEvents());
    }
}
