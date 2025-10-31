package org.example.paymentservice.domain.model.valueobject;


public enum WebhookStatus {
    PENDING,
    SENDING,
    SUCCESS,
    FAILED,
    DLQ;
}
