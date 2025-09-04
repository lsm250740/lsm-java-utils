package com.lsm.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropsUtilTest {

    @BeforeEach
    void setUp() {
        // Clear cache before each test
        PropsUtil.refresh(new HashMap<>());
    }

    @Test
    void testGetProperty() {
        PropsUtil.putProperty("test.key", "test.value");
        
        String result = PropsUtil.getProperty("test.key");
        
        assertEquals("test.value", result);
    }

    @Test
    void testGetPropertyWithDefault() {
        String result = PropsUtil.getProperty("nonexistent.key", "default.value");
        
        assertEquals("default.value", result);
    }

    @Test
    void testGetIntProperty() {
        PropsUtil.putProperty("int.key", "123");
        
        Integer result = PropsUtil.getIntProperty("int.key");
        
        assertEquals(123, result);
    }

    @Test
    void testGetIntPropertyInvalid() {
        PropsUtil.putProperty("int.key", "invalid");
        
        Integer result = PropsUtil.getIntProperty("int.key");
        
        assertEquals(0, result);
    }

    @Test
    void testGetIntPropertyWithDefault() {
        Integer result = PropsUtil.getIntProperty("nonexistent.key", 456);
        
        assertEquals(456, result);
    }

    @Test
    void testGetBooleanProperty() {
        PropsUtil.putProperty("bool.key", "true");
        
        boolean result = PropsUtil.getBooleanProperty("bool.key");
        
        assertTrue(result);
    }

    @Test
    void testGetBooleanPropertyWithDefault() {
        boolean result = PropsUtil.getBooleanProperty("nonexistent.key", true);
        
        assertTrue(result);
    }

    @Test
    void testGetLongProperty() {
        PropsUtil.putProperty("long.key", "123456789");
        
        Long result = PropsUtil.getLongProperty("long.key");
        
        assertEquals(123456789L, result);
    }

    @Test
    void testGetDoubleProperty() {
        PropsUtil.putProperty("double.key", "123.45");
        
        Double result = PropsUtil.getDoubleProperty("double.key");
        
        assertEquals(123.45, result);
    }

    @Test
    void testGetFloatProperty() {
        PropsUtil.putProperty("float.key", "123.45");
        
        Float result = PropsUtil.getFloatProperty("float.key");
        
        assertEquals(123.45f, result);
    }

    @Test
    void testContainsProperty() {
        PropsUtil.putProperty("exists.key", "value");
        
        assertTrue(PropsUtil.contansProperty("exists.key"));
        assertFalse(PropsUtil.contansProperty("nonexistent.key"));
    }

    @Test
    void testGetPropertyAssertable() {
        PropsUtil.putProperty("assertable.key", "value");
        
        String result = PropsUtil.getPropertyAssertable("assertable.key");
        
        assertEquals("value", result);
    }

    @Test
    void testGetPropertyAssertableThrows() {
        assertThrows(IllegalAccessError.class, () -> {
            PropsUtil.getPropertyAssertable("nonexistent.key");
        });
    }

    @Test
    void testGetAllProperties() {
        PropsUtil.putProperty("test.key1", "value1");
        PropsUtil.putProperty("test.key2", "value2");
        
        Properties result = PropsUtil.getAllProperties();
        
        assertEquals("value1", result.getProperty("test.key1"));
        assertEquals("value2", result.getProperty("test.key2"));
    }

    @Test
    void testGetStartupTime() {
        assertNotNull(PropsUtil.getStartupTime());
    }

    @Test
    void testGetHostName() {
        assertNotNull(PropsUtil.getHostName());
    }

    @Test
    void testGetEnvironment() {
        assertNotNull(PropsUtil.getEnvironment());
    }

    @Test
    void testRefresh() {
        Map<String, Object> newProps = new HashMap<>();
        newProps.put("refresh.key", "refresh.value");
        
        PropsUtil.refresh(newProps);
        
        assertEquals("refresh.value", PropsUtil.getProperty("refresh.key"));
    }
}