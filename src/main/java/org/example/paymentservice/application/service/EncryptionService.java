package org.example.paymentservice.application.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${encryption.secret-key}")
    private String secretKey;
    private SecretKey key;
    private static final String ALGORITHM = "AES";

    @PostConstruct
    public void init() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        byte[] keyArray = new byte[16];
        System.arraycopy(keyBytes, 0, keyArray, 0, Math.min(keyBytes.length, 16));
        this.key = new SecretKeySpec(keyArray, ALGORITHM);
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
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