package org.example.paymentservice.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    public static final String CARD_NUMBER = "4111111111111111";
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
        ReflectionTestUtils.setField(encryptionService, "password", "test-password");
        ReflectionTestUtils.setField(encryptionService, "salt", "deadbeef");
        encryptionService.init();
    }

    @Test
    void shouldEncryptCardNumber() {
        String cardNumber = CARD_NUMBER;
        
        String encrypted = encryptionService.encrypt(cardNumber);
        
        assertNotNull(encrypted);
        assertNotEquals(cardNumber, encrypted);
    }

    @Test
    void shouldDecryptCardNumber() {
        String cardNumber = CARD_NUMBER;
        String encrypted = encryptionService.encrypt(cardNumber);
        
        String decrypted = encryptionService.decrypt(encrypted);
        
        assertEquals(cardNumber, decrypted);
    }

    @Test
    void shouldMaskCardShowingLastFourDigits() {
        String cardNumber = CARD_NUMBER;
        String encrypted = encryptionService.encrypt(cardNumber);
        
        String masked = encryptionService.maskCard(encrypted);
        
        assertEquals("**** **** **** 1111", masked);
    }

    @Test
    void shouldReturnMaskForNullCard() {
        String masked = encryptionService.maskCard(null);
        
        assertEquals("****", masked);
    }

    @Test
    void shouldReturnMaskForEmptyCard() {
        String masked = encryptionService.maskCard("");
        
        assertEquals("****", masked);
    }

    @Test
    void shouldThrowExceptionOnInvalidDecryption() {
        assertThrows(RuntimeException.class, () -> 
            encryptionService.decrypt("invalid-encrypted-data")
        );
    }
}
