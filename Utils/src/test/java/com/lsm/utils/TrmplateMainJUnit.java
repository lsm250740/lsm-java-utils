package com.lsm.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class TrmplateMainJUnit {

	@Test
	void testGetVersionDoesNotThrow() {
		assertDoesNotThrow(() -> TemplateImplMain.getVersion());
		System.out.println("Package version: " + TemplateImplMain.getVersion());
	}
	
	@Test
	void test() {
		System.out.println("Hello from test!");
	}
}
