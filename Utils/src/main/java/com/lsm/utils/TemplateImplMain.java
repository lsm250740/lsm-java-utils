package com.lsm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateImplMain {
	private static final Logger logger = LoggerFactory.getLogger(TemplateImplMain.class);

	public static String getVersion() {
		Package pkg = TemplateImplMain.class.getPackage();
		return (pkg != null && pkg.getImplementationVersion() != null) ? pkg.getImplementationVersion()
				: "Version not defined";
	}

	public static void main(String[] args) {
		logger.info("Package version: " + getVersion());
	}
}
