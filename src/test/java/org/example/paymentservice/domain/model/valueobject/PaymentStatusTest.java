package org.example.paymentservice.domain.model.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStatusTest {

    @Test
    void shouldReturnTrueForFinalStates() {
        assertTrue(PaymentStatus.COMPLETED.isFinal());
        assertTrue(PaymentStatus.FAILED.isFinal());
    }

    @Test
    void shouldReturnFalseForNonFinalStates() {
        assertFalse(PaymentStatus.CREATED.isFinal());
        assertFalse(PaymentStatus.PROCESSING.isFinal());
    }

    @Test
    void shouldAllowProcessingForCreatedStatus() {
        assertTrue(PaymentStatus.CREATED.canProcess());
    }

    @Test
    void shouldNotAllowProcessingForOtherStatuses() {
        assertFalse(PaymentStatus.PROCESSING.canProcess());
        assertFalse(PaymentStatus.COMPLETED.canProcess());
        assertFalse(PaymentStatus.FAILED.canProcess());
    }

    @Test
    void shouldHaveAllExpectedStatuses() {
        PaymentStatus[] statuses = PaymentStatus.values();
        assertEquals(4, statuses.length);
        assertEquals(PaymentStatus.CREATED, PaymentStatus.valueOf("CREATED"));
        assertEquals(PaymentStatus.PROCESSING, PaymentStatus.valueOf("PROCESSING"));
        assertEquals(PaymentStatus.COMPLETED, PaymentStatus.valueOf("COMPLETED"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.valueOf("FAILED"));
    }
}
