package com.lsm.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


public class StreamUtils {
	private static org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(StreamUtils.class);
	private final static Set<PosixFilePermission> posixFilePermissions = PosixFilePermissions.fromString("rw-r--r--");

	/**
	 * Simple utility methods for dealing with streams. The copy methods of this class are
	 * similar to those defined in {@link FileUtils} except that all affected streams are
	 * left open when done. All copy methods use a block size of 4096 bytes.
	 *
	 * <p>
	 * Mainly for use within the framework, but also useful for application code.
	 *
	 */

	public static final int BUFFER_SIZE = 4096;

	private static final byte[] EMPTY_CONTENT = new byte[0];

	/**
	 * Copy the contents of the given InputStream into a new byte array.
	 * Leaves the stream open when done.
	 * @param in the stream to copy from
	 * @return the new byte array that has been copied to
	 * @throws IOException in case of I/O errors
	 */
	public static byte[] copyToByteArray( InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
		copy(in, out);
		return out.toByteArray();
	}

	/**
	 * Copy the contents of the given InputStream into a String.
	 * Leaves the stream open when done.
	 * @param in  - the InputStream to copy from
	 * @param charset - the context to copy
	 * @return the String that has been copied to
	 * @throws IOException in case of I/O errors
	 */
	public static String copyToString( InputStream in,
	                                   Charset charset) throws IOException {
		Assert.notNull(in, "No InputStream specified");
		StringBuilder out = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(in, charset);
		char[] buffer = new char[BUFFER_SIZE];
		int bytesRead = -1;
		while((bytesRead = reader.read(buffer)) != -1){
			out.append(buffer, 0, bytesRead);
		}
		return out.toString();
	}

	/**
	 * Copy the contents of the given InputStream into a String.
	 * Leaves the stream open when done.
	 * @param in  - the InputStream to copy from
	 * @param charset - the context to copy
	 * @return the String that has been copied to
	 * @throws IOException in case of I/O errors
	 */
	public static List<String> copyToStringArray( InputStream in,
	                                   Charset charset) throws IOException {
		Assert.notNull(in, "No InputStream specified");
		List<String> out = new LinkedList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset))) {
	        while (reader.ready()) {
	            out.add(reader.readLine());
	        }
	    }
		return out;
	}
	/**
	 * Copy the contents of the given byte array to the given OutputStream.
	 * Leaves the stream open when done.
	 * @param in the byte array to copy from
	 * @param out the OutputStream to copy to
	 * @throws IOException in case of I/O errors
	 */
	public static void copy( byte[] in,
	                         OutputStream out) throws IOException {
		Assert.notNull(in, "No input byte array specified");
		Assert.notNull(out, "No OutputStream specified");
		out.write(in);
	}

	/**
	 * Copy the contents of the given String to the given output OutputStream.
	 * Leaves the stream open when done.
	 * @param in the String to copy from
	 * @param charset the Charset
	 * @param out the OutputStream to copy to
	 * @throws IOException in case of I/O errors
	 */
	public static void copy( String in,
	                         Charset charset,
	                         OutputStream out) throws IOException {
		Assert.notNull(in, "No input String specified");
		Assert.notNull(charset, "No charset specified");
		Assert.notNull(out, "No OutputStream specified");
		Writer writer = new OutputStreamWriter(out, charset);
		writer.write(in);
		writer.flush();
	}

	/**
	 * Copy the contents of the given InputStream to the given OutputStream.
	 * Leaves both streams open when done.
	 * @param in the InputStream to copy from
	 * @param out the OutputStream to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy( InputStream in,
	                        OutputStream out) throws IOException {
		Assert.notNull(in, "No InputStream specified");
		Assert.notNull(out, "No OutputStream specified");
		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while((bytesRead = in.read(buffer)) != -1){
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		return byteCount;
	}

	/**
	 * Return an efficient empty {@link InputStream}.
	 * @return a {@link ByteArrayInputStream} based on an empty byte array
	 * @since 4.2.2
	 */
	public static InputStream emptyInput() {
		return new ByteArrayInputStream(EMPTY_CONTENT);
	}

	/**
	 * returns URI schema before ://
	 * If it isn't contains ://, return empty
	 * @param input
	 * @return URI Schema
	 */
	public static String uriFileScheme(String input) {
        if (StringUtils.isEmpty(input) ) {
            return "";
        }
     
        final int pos = input.indexOf("://");
        if (pos == -1) {
        	 if(input.startsWith("classpath:")) {
        		 return "classpath";
         	 }
        	if(input.startsWith("/") || input.matches("^[a-zA-Z]:\\\\.*")) { 
				return "file";
        	}
        return	input.contains(":")? input.substring(0, input.indexOf(":")) : "classpath";
        }
        return input.substring(0, pos);
        
	}

	/**
	 * returns InputStream for the resources 
	 * there are following options are  supported:
	 *  - classpath: returns resource from classpath
	 *  - file: returns resource from file
	 *  - http, https:
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	public static InputStream getResourceAsStream(String resourceName) throws IOException {
		String scheme = Assert.ofEmpty(uriFileScheme(resourceName)).orElse("");
		switch (scheme) {
		case "classpath":
			String resorcePath = StringUtils.contains(resourceName, "://")? StringUtils.substringAfter(resourceName, "://"):resourceName;
			 return Optional.ofNullable( _getResourceAsStream(resorcePath.replace("classpath:", "")))
					     .orElseThrow(() -> new FileNotFoundException("Resource " +resourceName+ " not found"));
		case "http":
		case "https":
			return URI.create(resourceName).toURL().openStream();
		case "file":
			return new FileInputStream(StringUtils.substringAfter(resourceName, "://"));
		default:
			throw new IOException("Unsupported scheme " + scheme + " in path " + resourceName);
		}
	}

	private static InputStream _getResourceAsStream(String resourceName) {
		ClassLoader loader = getDefaultClassLoader();
		InputStream resource = loader.getResourceAsStream(resourceName);
		if (resource == null) {
			resource = ClassLoader.getSystemResourceAsStream(resourceName);
		}
		return resource;
	}

	public static URL getResourceURL(String resourceName){
		ClassLoader loader = getDefaultClassLoader();
		return  loader.getResource(resourceName);
	}
	
	public static File getResource(String resourceName){
		ClassLoader loader = getDefaultClassLoader();
		URL  resource = loader.getResource(resourceName);
		File file = null;
		if (resource != null  ){ 
			 try{
				file = Paths.get(resource.toURI()).toFile();
			}catch (URISyntaxException e){
				// TODO Auto-generated catch block
				logger.warn("Resource {} not found",resourceName,e); 
			}
		   if (  file.exists()) 
			   return file;
		}
       return null;
	}

	
	private static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try{
			cl = Thread.currentThread().getContextClassLoader();
		}catch (Throwable ex){
			// Cannot access thread context ClassLoader - falling back...
		}
		if(cl == null){
			// No thread context class loader -> use class loader of this class.
			logger.trace("DefaultClassLoader:  No thread context class loader -> use class loader of this class.");
			cl = StreamUtils.class.getClassLoader();
			if(cl == null){
				// getClassLoader() returning null indicates the bootstrap ClassLoader
				logger.trace("DefaultClassLoader: returning null indicates the bootstrap ClassLoader");

				try{
					cl = ClassLoader.getSystemClassLoader();
				}catch (Throwable ex){
					// Cannot access system ClassLoader - oh well, maybe the caller can live with null...
					logger.warn("DefaultClassLoader: Cannot access system ClassLoader");

				}
			}
		}
		return cl;
	}

	
  public static void closeQuietly( Closeable stream) {
	 try {
		 if (stream != null) {
			 stream.close();
		 }
	 }catch (Exception e) {
		 logger.warn(e.getMessage());
	 }
  }
  
  public static void setFileReadPermissions( Path file) throws IOException {
	  try{
		Files.setPosixFilePermissions(file, posixFilePermissions);
	  }catch (java.lang.UnsupportedOperationException e) {
		  //ignore system does not support  exception
		  logger.trace("set File permissions failed" + file,e.getMessage());
	  }
	}
}
