package com.lsm.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class created by Amazon Q to improve code coverage
 */
public class TemplateImplMainAdvancedTest {

    @Test
    void testGetVersionNotNull() {
        String version = TemplateImplMain.getVersion();
        assertNotNull(version);
    }

    @Test
    void testGetVersionDefaultValue() {
        String version = TemplateImplMain.getVersion();
        // Since package info is likely not available in test, should return default
        assertTrue(version.equals("Version not defined") || version.matches("\\d+\\.\\d+.*"));
    }

    @Test
    void testMainWithEmptyArgs() {
        String[] emptyArgs = {};
        assertDoesNotThrow(() -> TemplateImplMain.main(emptyArgs));
    }

    @Test
    void testMainWithMultipleArgs() {
        String[] args = {"test", "arg1", "arg2", "arg3"};
        assertDoesNotThrow(() -> TemplateImplMain.main(args));
    }

    @Test
    void testGetVersionConsistency() {
        String version1 = TemplateImplMain.getVersion();
        String version2 = TemplateImplMain.getVersion();
        assertEquals(version1, version2);
    }

    @Test
    void testClassInstantiation() {
        assertDoesNotThrow(() -> new TemplateImplMain());
    }
}