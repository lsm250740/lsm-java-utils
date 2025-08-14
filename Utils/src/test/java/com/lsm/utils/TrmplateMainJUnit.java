package com.lsm.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class created by Amazon Q to improve code coverage
 */
public class TrmplateMainJUnit {

	@Test
	void testGetVersionDoesNotThrow() {
		assertDoesNotThrow(() -> TemplateImplMain.getVersion());
		String version = TemplateImplMain.getVersion();
		assertNotNull(version);
		assertFalse(version.isEmpty());
	}
	
	@Test
	void testGetVersionReturnsString() {
		String version = TemplateImplMain.getVersion();
		assertNotNull(version);
		assertInstanceOf(String.class, version);
	}
	
	@Test
	void testMainMethodDoesNotThrow() {
		assertDoesNotThrow(() -> TemplateImplMain.main(new String[]{}));
	}
	
	@Test
	void testMainMethodWithArgs() {
		String[] args = {"arg1", "arg2"};
		assertDoesNotThrow(() -> TemplateImplMain.main(args));
	}
	
	@Test
	void testMainMethodWithNullArgs() {
		assertDoesNotThrow(() -> TemplateImplMain.main(null));
	}
}
