package org.example.paymentservice.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WebhookTest {

    public static final String HTTP_EXAMPLE_COM = "http://url.com";
    public static final String SECRET = "secret";
    public static final String URL = "http://url.com/webhook";

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

    @Test
    void shouldUpdateWebhookUrl() {
        Webhook webhook = Webhook.builder()
                .id(UUID.randomUUID())
                .url(HTTP_EXAMPLE_COM)
                .description("description")
                .secret(SECRET)
                .isActive(true)
                .build();

        String newUrl = URL;
        webhook.update(newUrl, null, null, null);

        assertEquals(newUrl, webhook.getUrl());
        assertNotNull(webhook.getUpdatedAt());
    }

    @Test
    void shouldUpdateAllWebhookFields() {
        Webhook webhook = Webhook.builder()
                .id(UUID.randomUUID())
                .url(HTTP_EXAMPLE_COM)
                .description("description")
                .secret(SECRET)
                .isActive(true)
                .build();

        webhook.update("http://url2.com", "description 2", "secret 2", false);

        assertEquals("http://url2.com", webhook.getUrl());
        assertEquals("description 2", webhook.getDescription());
        assertEquals("secret 2", webhook.getSecret());
        assertFalse(webhook.getIsActive());
    }

    @Test
    void shouldNotUpdateUrlWhenBlank() {
        Webhook webhook = Webhook.builder()
                .id(UUID.randomUUID())
                .url(HTTP_EXAMPLE_COM)
                .build();

        webhook.update("", null, null, null);

        assertEquals(HTTP_EXAMPLE_COM, webhook.getUrl());
    }

    @Test
    void shouldNotUpdateSecretWhenBlank() {
        Webhook webhook = Webhook.builder()
                .id(UUID.randomUUID())
                .url(HTTP_EXAMPLE_COM)
                .secret(SECRET)
                .build();

        webhook.update(null, null, "", null);

        assertEquals(SECRET, webhook.getSecret());
    }
}
