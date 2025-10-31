package org.example.paymentservice.infrastructure.adapter.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WebhookClientTest {

    public static final String HTTP_EXAMPLE_COM_WEBHOOK = "http://example.com/webhook";
    public static final String SECRET = "verygoodsecret";
    public static final String PAYMENT_DATA = "{\"payment\":\"data\"}";
    private WebhookClient webhookClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        webhookClient = new WebhookClient(restTemplate);
    }

    @Test
    void shouldSendWebhookSuccessfully() {
        String url = HTTP_EXAMPLE_COM_WEBHOOK;
        String payload = PAYMENT_DATA;
        String secret = SECRET;

        when(restTemplate.exchange(eq(url), any(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        WebhookClientResponse response = webhookClient.sendWebhook(url, payload, secret);

        assertTrue(response.success());
        assertEquals(200, response.statusCode());
        verify(restTemplate).exchange(eq(url), any(), any(), eq(String.class));
    }

    @Test
    void shouldHandleHttpClientError() {
        String url = HTTP_EXAMPLE_COM_WEBHOOK;
        String payload = PAYMENT_DATA;

        when(restTemplate.exchange(eq(url), any(), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        WebhookClientResponse response = webhookClient.sendWebhook(url, payload, null);

        assertFalse(response.success());
        assertEquals(400, response.statusCode());
    }

    @Test
    void shouldHandleGenericException() {
        String url = HTTP_EXAMPLE_COM_WEBHOOK;
        String payload = PAYMENT_DATA;

        when(restTemplate.exchange(eq(url), any(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

        WebhookClientResponse response = webhookClient.sendWebhook(url, payload, null);

        assertFalse(response.success());
        assertNotNull(response.errorMessage());
    }
}
