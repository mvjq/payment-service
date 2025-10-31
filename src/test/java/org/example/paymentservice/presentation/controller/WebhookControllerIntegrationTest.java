package org.example.paymentservice.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.WebhookJpaRepository;
import org.example.paymentservice.presentation.dto.WebhookRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@DisplayName("Webhook Controller Integration Tests")
class WebhookControllerIntegrationTest {

    public static final String HTTPS_EXAMPLE_COM_WEBHOOK = "https://example.com/webhook";
    public static final String TEST_WEBHOOK = "Test webhook";
    public static final String SECRET = "my-secret";
    public static final String V_1_WEBHOOKS = "/api/v1/webhooks";
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(false);

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management-alpine")
            .withReuse(false);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebhookJpaRepository webhookRepository;

    @BeforeEach
    void setUp() {
        webhookRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        webhookRepository.deleteAll();
    }

    @Test
    @DisplayName("Should register webhook successfully with valid data")
    void shouldRegisterWebhookSuccessfully() throws Exception {
        // Given
        WebhookRequest request = WebhookRequest.builder()
                .url(HTTPS_EXAMPLE_COM_WEBHOOK)
                .description(TEST_WEBHOOK)
                .secret(SECRET)
                .active(true)
                .build();

        // When & Then
        mockMvc.perform(post(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.url").value(HTTPS_EXAMPLE_COM_WEBHOOK))
                .andExpect(jsonPath("$.description").value(TEST_WEBHOOK))
                .andExpect(jsonPath("$.is_active").value(true));
    }

    @Test
    @DisplayName("Should return 400 when URL is missing")
    void shouldReturnBadRequestWhenUrlIsMissing() throws Exception {
        // Given
        WebhookRequest request = WebhookRequest.builder()
                .description(TEST_WEBHOOK)
                .secret(SECRET)
                .active(true)
                .build();

        // When & Then
        mockMvc.perform(post(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when URL is blank")
    void shouldReturnBadRequestWhenUrlIsBlank() throws Exception {
        // Given
        WebhookRequest request = WebhookRequest.builder()
                .url("")
                .description(TEST_WEBHOOK)
                .secret(SECRET)
                .active(true)
                .build();

        // When & Then
        mockMvc.perform(post(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should register webhook with minimal data")
    void shouldRegisterWebhookWithMinimalData() throws Exception {
        // Given
        WebhookRequest request = WebhookRequest.builder()
                .url("https://minimal.com/webhook")
                .build();

        // When & Then
        mockMvc.perform(post(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.url").value("https://minimal.com/webhook"));
    }

    @Test
    @DisplayName("Should find all webhooks")
    void shouldFindAllWebhooks() throws Exception {
        // Given - Create test webhooks
        WebhookEntity webhook1 = WebhookEntity.builder()
                .url("https://webhook1.com")
                .description("Webhook 1")
                .secret("secret1")
                .isActive(true)
                .build();

        WebhookEntity webhook2 = WebhookEntity.builder()
                .url("https://webhook2.com")
                .description("Webhook 2")
                .secret("secret2")
                .isActive(false)
                .build();

        webhookRepository.save(webhook1);
        webhookRepository.save(webhook2);

        // When & Then
        mockMvc.perform(get(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].url", containsInAnyOrder(
                        "https://webhook1.com",
                        "https://webhook2.com"
                )));
    }

    @Test
    @DisplayName("Should return empty list when no webhooks exist")
    void shouldReturnEmptyListWhenNoWebhooksExist() throws Exception {
        mockMvc.perform(get(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should delete webhook successfully")
    void shouldDeleteWebhookSuccessfully() throws Exception {
        WebhookEntity webhook = WebhookEntity.builder()
                .url("https://todelete.com/webhook")
                .description("To be deleted")
                .secret("secret")
                .isActive(true)
                .build();
        WebhookEntity savedWebhook = webhookRepository.save(webhook);

        mockMvc.perform(delete("/api/v1/webhooks/" + savedWebhook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assert webhookRepository.findById(savedWebhook.getId()).isEmpty();
    }

    @Test
    @DisplayName("Should return 400 when deleting non-existent webhook")
    void shouldReturnNotFoundWhenDeletingNonExistentWebhook() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/webhooks/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should disable webhook successfully")
    void shouldDisableWebhookSuccessfully() throws Exception {
        WebhookEntity webhook = WebhookEntity.builder()
                .url("https://todisable.com/webhook")
                .description("To be disabled")
                .secret("secret")
                .isActive(true)
                .build();
        WebhookEntity savedWebhook = webhookRepository.save(webhook);

        mockMvc.perform(post("/api/v1/webhooks/" + savedWebhook.getId() + "/disable")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        WebhookEntity disabledWebhook = webhookRepository.findById(savedWebhook.getId()).orElseThrow();
        assert !disabledWebhook.getIsActive();
    }

    @Test
    @DisplayName("Should return 400 when disabling non-existent webhook")
    void shouldReturnBadRequestWhenDisablingNonExistentWebhook() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/webhooks/" + nonExistentId + "/disable")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate description max length")
    void shouldValidateDescriptionMaxLength() throws Exception {
        String longDescription = "a".repeat(256);
        WebhookRequest request = WebhookRequest.builder()
                .url(HTTPS_EXAMPLE_COM_WEBHOOK)
                .description(longDescription)
                .secret("secret")
                .active(true)
                .build();

        mockMvc.perform(post(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept description with exactly 255 characters")
    void shouldAcceptDescriptionWithExactly255Characters() throws Exception {
        String maxDescription = "a".repeat(255);
        WebhookRequest request = WebhookRequest.builder()
                .url(HTTPS_EXAMPLE_COM_WEBHOOK)
                .description(maxDescription)
                .secret("secret")
                .active(true)
                .build();

        mockMvc.perform(post(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(maxDescription));
    }

    @Test
    @DisplayName("Should register multiple webhooks")
    void shouldRegisterMultipleWebhooks() throws Exception {
        WebhookRequest request1 = WebhookRequest.builder()
                .url("https://first.com/webhook")
                .description("First webhook")
                .active(true)
                .build();

        mockMvc.perform(post(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        WebhookRequest request2 = WebhookRequest.builder()
                .url("https://second.com/webhook")
                .description("Second webhook")
                .active(true)
                .build();

        mockMvc.perform(post(V_1_WEBHOOKS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get(V_1_WEBHOOKS))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
