package com.lsm.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;


public interface IRuntimeException<T extends IRuntimeException<T>>   {
	String getMessage();
	Throwable getCause();
	Map<String,Object> getDetails();
	void printStackTrace(PrintWriter s) ;
	
	default int getCode() {
		return -1;
	}
	
	/**
	 * @return - error Stack trace as string
	 * @throws NullPointerException if exception==null
	 */
	default String exceptionToString() {
		Throwable exception = Optional.ofNullable(getCause()).orElseGet(() -> {
			if (this instanceof Throwable) {
				return (Throwable) this;
			}
			return null;
		});
		return exceptionToString(exception);
	}

	/**
	 * @return - error Stack trace as string
	 * @throws NullPointerException if exception==null
	 */
	public static String exceptionToString(Throwable exception) {
		if (exception != null) {
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);

			if (exception instanceof IRuntimeException && exception.getCause() != null) {
				exception.getCause().printStackTrace(printWriter);
			} else
				exception.printStackTrace(printWriter);
			printWriter.flush();
			String result = writer.toString();
			try {
				return result.substring(0, result.indexOf("\n", result.lastIndexOf("com.migdal")));
			} catch (Throwable e) {
				return writer.toString();
			}
		}
		return null;
	}

}
