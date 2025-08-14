package com.lsm.encryptor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class created by Amazon Q to improve code coverage
 */

public class CryptoExceptionTest {

    @Test
    public void testDefaultConstructor() {
        CryptoException exception = new CryptoException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testMessageConstructor() {
        String message = "Test error message";
        CryptoException exception = new CryptoException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testCauseConstructor() {
        Throwable cause = new RuntimeException("Root cause");
        CryptoException exception = new CryptoException(cause);
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testMessageAndCauseConstructor() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Root cause");
        CryptoException exception = new CryptoException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testFullConstructor() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Root cause");
        CryptoException exception = new CryptoException(message, cause, true, true);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}