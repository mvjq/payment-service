package org.example.paymentservice.application.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    @Value("${encryption.secret-key}")
    private String password;

    @Value("${encryption.salt}")
    private String salt;

    private TextEncryptor encryptor;

    @PostConstruct
    public void init() {
        this.encryptor = Encryptors.text(password, salt);
    }

    public String encrypt(String data) {
        try {
            return encryptor.encrypt(data);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            return encryptor.decrypt(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    public String maskCard(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return "****";
        }
        try {
            String decrypted = decrypt(encryptedData);
            if (decrypted.length() <= 4) {
                return "****";
            }
            return "**** **** **** " + decrypted.substring(decrypted.length() - 4);
        } catch (Exception e) {
            return "****";
        }
    }
}
