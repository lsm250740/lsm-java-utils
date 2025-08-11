package com.lsm.jfiglet;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Test;

class SampleTest {

	@Test
	void test() {
		String asciiArt2 = "Failed to convert hello";
		try {
			// using default font standard.flf, obtained from maven artifact
			asciiArt2 = FigletFont.convertOneLine("hello");
			System.out.println(asciiArt2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String resourceFile = "/fonts/wavy.flf";

		try {

			// using font font2.flf, located somewhere in classpath under path
			// /flf/font2.flf
			// String asciiArt2 =
			// FigletFont.convertOneLine(FigletFont.class.getResourceAsStream("/flf/font2.flf"),
			// "hello");
			asciiArt2 = FigletFont.convertOneLine(FigletFont.class.getResourceAsStream(resourceFile), "hello");
			System.out.println(asciiArt2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.print("getResourceAsStream failed " + e.toString());
		}

	
		try {
			asciiArt2 = FigletFont.convertOneLine("classpath:" + resourceFile, "hello");
			System.out.println(asciiArt2);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("from calsspath  failed " + e.toString());
		}
	}

	@Test
	public void testAll() {
	
		try {
			URL url = getClass().getClassLoader().getResource("fonts");
			if (url != null && url.getProtocol().equals("file")) {
			    File folder = new File(url.toURI());
			    File[] files = folder.listFiles();
			    for (File f : files) {
			        System.out.println(f.getName());
			        String asciiArt2 = FigletFont.convertOneLine( f, "hello");
					System.out.println(asciiArt2);
			    }
			} else {
			    System.out.println("resource not  found");
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
