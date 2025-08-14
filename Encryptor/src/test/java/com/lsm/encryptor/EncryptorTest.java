package com.lsm.encryptor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class created by Amazon Q to improve code coverage
 */

public class EncryptorTest {

    private static class TestEncryptor extends Encryptor {
        @Override
        public String encrypt(String paramString) throws CryptoException {
            return "encrypted:" + paramString;
        }

        @Override
        public String decrypt(String paramString) throws CryptoException {
            return paramString.replace("encrypted:", "");
        }

        public String testToHex(byte[] bytes) {
            return toHex(bytes);
        }

        public byte[] testFromHex(String hex) {
            return fromHex(hex);
        }

        public String testBuildKey() {
            return buildKey();
        }
    }

    @Test
    public void testToHex() {
        TestEncryptor encryptor = new TestEncryptor();
        byte[] bytes = {0x48, 0x65, 0x6c, 0x6c, 0x6f}; // "Hello"
        String hex = encryptor.testToHex(bytes);
        assertEquals("48656c6c6f", hex);
    }

    @Test
    public void testToHexWithPadding() {
        TestEncryptor encryptor = new TestEncryptor();
        byte[] bytes = {0x01, 0x02}; // Small values that need padding
        String hex = encryptor.testToHex(bytes);
        assertEquals("0102", hex);
    }

    @Test
    public void testFromHex() {
        TestEncryptor encryptor = new TestEncryptor();
        String hex = "48656c6c6f";
        byte[] bytes = encryptor.testFromHex(hex);
        assertArrayEquals(new byte[]{0x48, 0x65, 0x6c, 0x6c, 0x6f}, bytes);
    }

    @Test
    public void testBuildKey() {
        TestEncryptor encryptor = new TestEncryptor();
        String key = encryptor.testBuildKey();
        assertNotNull(key);
        assertFalse(key.isEmpty());
    }

    @Test
    public void testHexRoundTrip() {
        TestEncryptor encryptor = new TestEncryptor();
        byte[] original = "Test String".getBytes();
        String hex = encryptor.testToHex(original);
        byte[] restored = encryptor.testFromHex(hex);
        assertArrayEquals(original, restored);
    }
}