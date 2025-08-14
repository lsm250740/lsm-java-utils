package com.lsm.encryptor.aes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.lsm.encryptor.CryptoException;

/**
 * Test class created by Amazon Q to improve code coverage
 */

public class AESEncryptorAdvancedTest {

    @Test
    public void testHighestPowerOf2() {
        assertEquals(8, AESEncryptor.highestPowerOf2(5));
        assertEquals(8, AESEncryptor.highestPowerOf2(8));
        assertEquals(16, AESEncryptor.highestPowerOf2(9));
        assertEquals(16, AESEncryptor.highestPowerOf2(16));
        assertEquals(32, AESEncryptor.highestPowerOf2(17));
        assertEquals(128, AESEncryptor.highestPowerOf2(100)); // This should be > 32
    }

    @Test
    public void testInvalidKeyLength() {
        // highestPowerOf2(100) = 128, which is > 32, so should throw
        assertThrows(CryptoException.class, () -> {
            new AESEncryptor("a".repeat(100));
        });
        
        // Test with key that results in pow > 32
        assertThrows(CryptoException.class, () -> {
            new AESEncryptor("a".repeat(33)); // highestPowerOf2(33) = 64 > 32
        });
        
        // Test with very short key that results in pow < 8
        assertThrows(CryptoException.class, () -> {
            new AESEncryptor("a"); // highestPowerOf2(1) = 1 < 8
        });
    }

    @Test
    public void testValidCustomKey() throws CryptoException {
        String customKey = "mykey123";
        AESEncryptor encryptor = new AESEncryptor(customKey);
        
        String plaintext = "Test message";
        String encrypted = encryptor.encrypt(plaintext);
        String decrypted = encryptor.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testValidShortKey() throws CryptoException {
        String shortKey = "mykey123"; // length 8, highestPowerOf2(8) = 8, which is valid
        AESEncryptor encryptor = new AESEncryptor(shortKey);
        
        String plaintext = "Test message";
        String encrypted = encryptor.encrypt(plaintext);
        String decrypted = encryptor.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testDecryptInvalidData() {
        AESEncryptor encryptor = new AESEncryptor();
        
        assertThrows(CryptoException.class, () -> {
            encryptor.decrypt("invalid_hex_data");
        });
    }

    @Test
    public void testEncryptNullString() {
        AESEncryptor encryptor = new AESEncryptor();
        
        assertThrows(CryptoException.class, () -> {
            encryptor.encrypt(null);
        });
    }
}