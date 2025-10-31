package org.example.paymentservice.infrastructure.config;

public class RABBITMQ_CONSTANTS {
    public static final String PAYMENT_QUEUE = "payment.event.queue";
    public static final String PAYMENT_DLQ = "payment.event.queue.dlq";
    public static final String EXCHANGE = "payment.events";
    public static final String ROUTING_KEY = "payment.created";
    public static final String DLQ_ROUTING_KEY = "payment.created.dlq";
    public static final int MAX_DLQ_RETRIES = 3;
}
