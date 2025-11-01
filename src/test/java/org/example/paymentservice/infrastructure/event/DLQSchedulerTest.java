package org.example.paymentservice.infrastructure.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.example.paymentservice.infrastructure.config.RABBITMQ_CONSTANTS.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DLQSchedulerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DLQScheduler dlqScheduler;

    private Message message;
    private MessageProperties messageProperties;

    @BeforeEach
    void setUp() {
        messageProperties = new MessageProperties();
        message = new Message("test".getBytes(), messageProperties);
    }

    @Test
    void shouldSkipWhenNoMessagesInDLQ() throws Exception {
        when(rabbitTemplate.execute(any())).thenReturn(0L);

        dlqScheduler.retryDlqMessages();

        verify(rabbitTemplate, never()).receive(anyString(), anyLong());
    }

    @Test
    void shouldRetryMessageFromDLQWithIncrementedRetryCount() throws Exception {
        when(rabbitTemplate.execute(any())).thenReturn(1L);
        when(rabbitTemplate.receive(eq(PAYMENT_QUEUE), anyLong())).thenReturn(message);
        
        messageProperties.setHeader("x-dlq-retry-count", 1);

        dlqScheduler.retryDlqMessages();

        verify(rabbitTemplate).send(eq(PAYMENT_QUEUE), eq(message));
    }

    @Test
    void shouldSetInitialRetryCountWhenHeaderIsNull() throws Exception {
        when(rabbitTemplate.execute(any())).thenReturn(1L);
        when(rabbitTemplate.receive(eq(PAYMENT_QUEUE), anyLong())).thenReturn(message);

        dlqScheduler.retryDlqMessages();

        verify(rabbitTemplate).send(eq(PAYMENT_QUEUE), eq(message));
    }

    @Test
    void shouldMoveToDLQWhenMaxRetriesReached() throws Exception {
        when(rabbitTemplate.execute(any())).thenReturn(1L);
        when(rabbitTemplate.receive(eq(PAYMENT_QUEUE), anyLong())).thenReturn(message);
        
        messageProperties.setHeader("x-dlq-retry-count", MAX_DLQ_RETRIES);

        dlqScheduler.retryDlqMessages();

        verify(rabbitTemplate).send(eq(PAYMENT_DLQ), eq(message));
        verify(rabbitTemplate, never()).send(eq(PAYMENT_QUEUE), any());
    }

    @Test
    void shouldHandleExceptionGracefully() throws Exception {
        when(rabbitTemplate.execute(any())).thenThrow(new RuntimeException("RabbitMQ connection error"));

        dlqScheduler.retryDlqMessages();

        // Should not throw exception
        verify(rabbitTemplate, never()).receive(anyString(), anyLong());
    }

    @Test
    void shouldProcessMultipleMessages() throws Exception {
        when(rabbitTemplate.execute(any())).thenReturn(3L);
        when(rabbitTemplate.receive(eq(PAYMENT_QUEUE), anyLong()))
                .thenReturn(message)
                .thenReturn(message)
                .thenReturn(message);

        dlqScheduler.retryDlqMessages();

        verify(rabbitTemplate, times(3)).send(eq(PAYMENT_QUEUE), eq(message));
    }

    @Test
    void shouldLimitTo100MessagesPerRun() throws Exception {
        when(rabbitTemplate.execute(any())).thenReturn(150L);
        when(rabbitTemplate.receive(eq(PAYMENT_QUEUE), anyLong())).thenReturn(message);

        dlqScheduler.retryDlqMessages();

        // Should only process 100 messages
        verify(rabbitTemplate, atMost(100)).receive(eq(PAYMENT_QUEUE), anyLong());
    }

    @Test
    void shouldHandleNullMessage() throws Exception {
        when(rabbitTemplate.execute(any())).thenReturn(1L);
        when(rabbitTemplate.receive(eq(PAYMENT_QUEUE), anyLong())).thenReturn(null);

        dlqScheduler.retryDlqMessages();

        verify(rabbitTemplate, never()).send(anyString(), any(Message.class));
    }
}
