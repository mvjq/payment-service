package org.example.paymentservice.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.OutboxJpaRepository;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.PaymentJpaRepository;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.WebhookJpaRepository;
import org.example.paymentservice.presentation.dto.PaymentRequest;
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
@DisplayName("Payment Controller Integration Tests")
class PaymentControllerIntegrationTest {

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
    private PaymentJpaRepository paymentRepository;

    @Autowired
    private WebhookJpaRepository webhookRepository;

    @Autowired
    private OutboxJpaRepository outboxEventRepository;

    private UUID webhookId;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        outboxEventRepository.deleteAll();
        webhookRepository.deleteAll();

        WebhookEntity webhook = WebhookEntity.builder()
                .url("https://example.com/webhook")
                .description("Test webhook")
                .secret("test-secret")
                .isActive(true)
                .build();
        WebhookEntity savedWebhook = webhookRepository.save(webhook);
        webhookId = savedWebhook.getId();
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
        outboxEventRepository.deleteAll();
        webhookRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create payment successfully with valid data")
    void shouldCreatePaymentSuccessfully() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .webhokUUID(webhookId)
                .firstName("John")
                .lastName("Doe")
                .zipCode("12345")
                .cardNumber("4111111111111111")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.zip_code").value("12345"))
                .andExpect(jsonPath("$.masked_card_number").value(startsWith("****")));
    }

    @Test
    @DisplayName("Should return 400 when firstName is missing")
    void shouldReturnBadRequestWhenFirstNameIsMissing() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .webhokUUID(webhookId)
                .lastName("Doe")
                .zipCode("12345")
                .cardNumber("4111111111111111")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when lastName is missing")
    void shouldReturnBadRequestWhenLastNameIsMissing() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .webhokUUID(webhookId)
                .firstName("John")
                .zipCode("12345")
                .cardNumber("4111111111111111")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when zipCode is missing")
    void shouldReturnBadRequestWhenZipCodeIsMissing() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .webhokUUID(webhookId)
                .firstName("John")
                .lastName("Doe")
                .cardNumber("4111111111111111")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when cardNumber is missing")
    void shouldReturnBadRequestWhenCardNumberIsMissing() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .webhokUUID(webhookId)
                .firstName("John")
                .lastName("Doe")
                .zipCode("12345")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when webhookUUID is null")
    void shouldReturnBadRequestWhenWebhookUUIDIsNull() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .zipCode("12345")
                .cardNumber("4111111111111111")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when webhook does not exist")
    void shouldReturnBadRequestWhenWebhookDoesNotExist() throws Exception {
        UUID nonExistentWebhookId = UUID.randomUUID();
        PaymentRequest request = PaymentRequest.builder()
                .webhokUUID(nonExistentWebhookId)
                .firstName("John")
                .lastName("Doe")
                .zipCode("12345")
                .cardNumber("4111111111111111")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should mask card number in response")
    void shouldMaskCardNumberInResponse() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .webhokUUID(webhookId)
                .firstName("Jane")
                .lastName("Smith")
                .zipCode("54321")
                .cardNumber("5555555555554444")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.masked_card_number").value(not(containsString("5555555555554444"))))
                .andExpect(jsonPath("$.masked_card_number").value(containsString("****")))
                .andExpect(jsonPath("$.masked_card_number").value(containsString("4444")));
    }

    @Test
    @DisplayName("Should create payment and generate outbox event")
    void shouldCreatePaymentAndGenerateOutboxEvent() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .webhokUUID(webhookId)
                .firstName("Alice")
                .lastName("Johnson")
                .zipCode("99999")
                .cardNumber("4111111111111111")
                .build();

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());

        long outboxEventCount = outboxEventRepository.count();
        assert outboxEventCount > 0 : "Outbox event should have been created";
    }
}
