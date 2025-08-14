package com.lsm.encryptor.aes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.lsm.encryptor.CryptoException;

/**
 * Test class created by Amazon Q to improve code coverage
 */

public class AESEncryptorUtilTest {

    @Test
    public void testDefaultEncryption() throws CryptoException {
        String plaintext = "Hello World";
        String encrypted = AESEncryptorUtil.aes256.encrypt(plaintext);
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);
        
        String decrypted = AESEncryptorUtil.aes256.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testRebuildWithSalt() throws CryptoException {
        String plaintext = "Test Message";
        String salt = "customsalt";
        
        AESEncryptorUtil.aes256.rebuild(salt);
        String encrypted = AESEncryptorUtil.aes256.encrypt(plaintext);
        String decrypted = AESEncryptorUtil.aes256.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testEmptyString() throws CryptoException {
        String plaintext = "";
        String encrypted = AESEncryptorUtil.aes256.encrypt(plaintext);
        String decrypted = AESEncryptorUtil.aes256.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testLongString() throws CryptoException {
        String plaintext = "This is a very long string that should test the encryption with larger data sizes and ensure it works correctly";
        String encrypted = AESEncryptorUtil.aes256.encrypt(plaintext);
        String decrypted = AESEncryptorUtil.aes256.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    public void testSpecialCharacters() throws CryptoException {
        String plaintext = "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        String encrypted = AESEncryptorUtil.aes256.encrypt(plaintext);
        String decrypted = AESEncryptorUtil.aes256.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }
}