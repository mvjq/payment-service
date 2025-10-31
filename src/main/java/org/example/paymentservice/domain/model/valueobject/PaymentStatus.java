package org.example.paymentservice.domain.model.valueobject;

/**
 * PaymentStatus value object
 * Represents the lifecycle of a payment (simplified for requirements)
 * 
 * Note: Per requirements, we only track creation.
 * In a real system, this would have more states like PROCESSING, AUTHORIZED, etc.
 */
public enum PaymentStatus {
    
    /**
     * Payment has been created and event published
     */
    CREATED,
    
    /**
     * Payment is being processed (optional - not in requirements)
     */
    PROCESSING,
    
    /**
     * Payment completed successfully (optional - not in requirements)
     */
    COMPLETED,
    
    /**
     * Payment failed (optional - not in requirements)
     */
    FAILED;
    
    /**
     * Business rule: Check if payment is in final state
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED;
    }
    
    /**
     * Business rule: Check if payment can be processed
     */
    public boolean canProcess() {
        return this == CREATED;
    }
}
