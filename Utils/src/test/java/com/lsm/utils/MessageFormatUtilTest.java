package com.lsm.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Test class created by Amazon Q to improve code coverage
 */
public class MessageFormatUtilTest {

    @Test
    public void testFormatWithArgs() {
        String result = MessageFormatUtil.format("Hello %s, you have %d messages", "John", 5);
        assertEquals("Hello John, you have 5 messages", result);
    }

    @Test
    public void testFormatWithTuples() {
        String result = MessageFormatUtil.format("Hello {}, you have {} messages", "John", 5);
        assertEquals("Hello John, you have 5 messages", result);
    }

    @Test
    public void testFormatMessage() {
        String result = MessageFormatUtil.formatMessage("Hello {0}, you have {1} messages", "John", 5);
        assertEquals("Hello John, you have 5 messages", result);
    }

    @Test
    public void testFormatMessageWithNoArgs() {
        String result = MessageFormatUtil.formatMessage("Hello World");
        assertEquals("Hello World", result);
    }

    @Test
    public void testFormatMessageNoBraces() {
        String result = MessageFormatUtil.formatMessage("Hello World", "John");
        assertEquals("Hello World", result);
    }

    @Test
    public void testFormatWithNoArgs() {
        String result = MessageFormatUtil.format("Hello World");
        assertEquals("Hello World", result);
    }

    @Test
    public void testFormatWithNullArgs() {
        String result = MessageFormatUtil.format("Hello {}, value: {}", "John", null);
        assertEquals("Hello John, value: ", result);
    }

    @Test
    public void testFormatByName() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "John");
        args.put("count", 5);
        
        String result = MessageFormatUtil.formatByName("Hello {name}, you have {count} messages", args);
        assertEquals("Hello John, you have 5 messages", result);
    }

    @Test
    public void testFormatByNameWithEmptyArgs() {
        Map<String, Object> args = new HashMap<>();
        String result = MessageFormatUtil.formatByName("Hello {name}", args);
        assertEquals("Hello {name}", result);
    }

    @Test
    public void testFormatByNameWithNullArgs() {
        String result = MessageFormatUtil.formatByName("Hello {name}", null);
        assertEquals("Hello {name}", result);
    }

    @Test
    public void testFormatByNameWithMissingKey() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "John");
        
        String result = MessageFormatUtil.formatByName("Hello {name}, age {age}", args);
        assertEquals("Hello John, age ", result);
    }

    @Test
    public void testFormatByNameNoBraces() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", "John");
        
        String result = MessageFormatUtil.formatByName("Hello World", args);
        assertEquals("Hello World", result);
    }

    @Test
    public void testFormatMixWithNamedArgs() {
        Map<String, Object> namedArgs = new HashMap<>();
        namedArgs.put("name", "John");
        
        String result = MessageFormatUtil.formatMix("Hello {name}, you have %d messages", namedArgs, 5);
        assertEquals("Hello John, you have 5 messages", result);
    }

    @Test
    public void testFormatMixWithLinkedHashMap() {
        LinkedHashMap<String, Object> namedArgs = new LinkedHashMap<>();
        namedArgs.put("name", "John");
        namedArgs.put("count", 5);
        
        String result = MessageFormatUtil.formatMix("Hello {name}, you have {} messages", namedArgs,5);
        assertEquals("Hello John, you have 5 messages", result);
    }

    @Test
    public void testFormatMixWithEmptyArgs() {
        Map<String, Object> namedArgs = new HashMap<>();
        String result = MessageFormatUtil.formatMix("Hello World", namedArgs);
        assertEquals("Hello World", result);
    }

    @Test
    public void testFormatMixNoBraces() {
        Map<String, Object> namedArgs = new HashMap<>();
        namedArgs.put("name", "John");
        
        String result = MessageFormatUtil.formatMix("Hello World", namedArgs, "test");
        assertEquals("Hello World", result);
    }

    @Test
    public void testFormatWithMultipleTuples() {
        String result = MessageFormatUtil.format("Values: {}, {}, {}", "A", "B", "C");
        assertEquals("Values: A, B, C", result);
    }

    @Test
    public void testFormatByNameWithNullValue() {
        Map<String, Object> args = new HashMap<>();
        args.put("name", null);
        
        String result = MessageFormatUtil.formatByName("Hello {name}", args);
        assertEquals("Hello ", result);
    }

    @Test
    public void testFormatMessageWithMultipleArgs() {
        String result = MessageFormatUtil.formatMessage("User {0} has {1} points and {2} level", "John", 100, "Gold");
        assertEquals("User John has 100 points and Gold level", result);
    }

    @Test
    public void testFormatMessageWithRepeatedIndex() {
        String result = MessageFormatUtil.formatMessage("Hello {0}, {0} is your name", "John");
        assertEquals("Hello John, John is your name", result);
    }

    @Test
    public void testFormatMessageWithEmptyArgs() {
        String result = MessageFormatUtil.formatMessage("Hello {0}");
        assertEquals("Hello {0}", result);
    }
}