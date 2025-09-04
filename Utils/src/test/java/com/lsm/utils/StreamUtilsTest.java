package com.lsm.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Test class created by Amazon Q to improve code coverage
 */
public class StreamUtilsTest {

    @Test
    public void testCopyToByteArray() throws IOException {
        String testData = "Hello World";
        InputStream input = new ByteArrayInputStream(testData.getBytes());
        byte[] result = StreamUtils.copyToByteArray(input);
        assertArrayEquals(testData.getBytes(), result);
    }

    @Test
    public void testCopyToString() throws IOException {
        String testData = "Hello World";
        InputStream input = new ByteArrayInputStream(testData.getBytes());
        String result = StreamUtils.copyToString(input, StandardCharsets.UTF_8);
        assertEquals(testData, result);
    }

    @Test
    public void testCopyToStringArray() throws IOException {
        String testData = "Line1\nLine2\nLine3";
        InputStream input = new ByteArrayInputStream(testData.getBytes());
        List<String> result = StreamUtils.copyToStringArray(input, StandardCharsets.UTF_8);
        assertEquals(3, result.size());
        assertEquals("Line1", result.get(0));
        assertEquals("Line2", result.get(1));
        assertEquals("Line3", result.get(2));
    }

    @Test
    public void testCopyByteArrayToOutputStream() throws IOException {
        byte[] testData = "Hello World".getBytes();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StreamUtils.copy(testData, output);
        assertArrayEquals(testData, output.toByteArray());
    }

    @Test
    public void testCopyStringToOutputStream() throws IOException {
        String testData = "Hello World";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StreamUtils.copy(testData, StandardCharsets.UTF_8, output);
        assertEquals(testData, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testCopyInputStreamToOutputStream() throws IOException {
        String testData = "Hello World";
        InputStream input = new ByteArrayInputStream(testData.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int bytesCopied = StreamUtils.copy(input, output);
        assertEquals(testData.length(), bytesCopied);
        assertEquals(testData, output.toString());
    }

    @Test
    public void testEmptyInput() throws IOException {
        InputStream empty = StreamUtils.emptyInput();
        assertNotNull(empty);
        assertEquals(0, empty.available());
        assertEquals(-1, empty.read());
    }

    @Test
    public void testGetResourceAsStreamClasspath() throws IOException {
    	String[] options = {"classpath://log4j2.xml","classpath:log4j2.xml","log4j2.xml","./log4j2.xml"};
    	for (String option : options) {
    	  try (InputStream stream = StreamUtils.getResourceAsStream(option)) {
    		  assertNotNull(stream,"load option failed "+ option);
    	  }catch (IOException e) {
			  fail("load option failed "+ option + " on schema " + StreamUtils.uriFileScheme(option)+ ": " + e.getMessage());
    	  }
    	}  	
    }

    @Test
    public void testGetResourceAsStreamFile() throws IOException {
        String tempFile = System.getProperty("java.io.tmpdir") + "/test.txt";
        java.io.File file = new java.io.File(tempFile);
        file.createNewFile();
        file.deleteOnExit();
        
        InputStream stream = StreamUtils.getResourceAsStream("file://" + tempFile);
        assertNotNull(stream);
        stream.close();
    }

    @Test
    public void testGetResourceURL() {
        java.net.URL url = StreamUtils.getResourceURL("log4j2.xml");
        assertNotNull(url);
    }

    @Test
    public void testGetResource() {
        java.io.File file = StreamUtils.getResource("log4j2.xml");
        assertNotNull(file);
        assertTrue(file.exists());
    }

    @Test
    public void testCloseQuietly() {
        InputStream stream = new ByteArrayInputStream("test".getBytes());
        assertDoesNotThrow(() -> StreamUtils.closeQuietly(stream));
        assertDoesNotThrow(() -> StreamUtils.closeQuietly(null));
    }

    @Test
    public void testSetFileReadPermissions() throws IOException {
        Path tempFile = Files.createTempFile("test", ".txt");
        assertDoesNotThrow(() -> StreamUtils.setFileReadPermissions(tempFile));
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testUriFileScheme() {
        assertEquals("classpath", StreamUtils.uriFileScheme("classpath://test.txt"));
        assertEquals("classpath", StreamUtils.uriFileScheme("classpath:test.txt"));
        assertEquals("file", StreamUtils.uriFileScheme("file://test.txt"));
        assertEquals("http", StreamUtils.uriFileScheme("http://example.com"));
        assertEquals("https", StreamUtils.uriFileScheme("https://example.com"));
        assertEquals("file", StreamUtils.uriFileScheme("/absolute/path"));
        assertEquals("classpath", StreamUtils.uriFileScheme("relative/path"));
        assertEquals("", StreamUtils.uriFileScheme(""));
        assertEquals("", StreamUtils.uriFileScheme(null));
        assertEquals("custom", StreamUtils.uriFileScheme("custom:resource"));
    }

    @Test
    public void testGetResourceAsStreamNonExistent() {
        assertThrows(IOException.class, () -> {
            StreamUtils.getResourceAsStream("nonexistent.txt");
        });
    }

    @Test
    public void testGetResourceAsStreamUnsupportedScheme() {
        assertThrows(IOException.class, () -> {
            StreamUtils.getResourceAsStream("ftp://example.com/file.txt");
        });
    }

    @Test
    public void testGetResourceAsStreamClasspathWithProtocol() throws IOException {
        InputStream stream = StreamUtils.getResourceAsStream("classpath://log4j2.xml");
        assertNotNull(stream);
        stream.close();
    }

    @Test
    public void testGetResourceAsStreamRelativePath() throws IOException {
        InputStream stream = StreamUtils.getResourceAsStream("log4j2.xml");
        assertNotNull(stream);
        stream.close();
    }

    @Test
    public void testGetResourceNonExistent() {
        java.io.File file = StreamUtils.getResource("nonexistent.txt");
        assertNull(file);
    }
}