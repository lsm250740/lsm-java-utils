package com.lsm.common;

import java.util.Map;
import  static com.lsm.utils.MessageFormatUtil.format; 

public class AppException extends RuntimeException implements 	IRuntimeException<AppException> {
  private static final long serialVersionUID = 1L;
  private Map<String, Object> details;
  
	public AppException() {
		super();

	}

	public AppException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public AppException(String message, Object ...args) {
 			super(format(message,args), cause(args));
	}

	
	private static Throwable cause (  Object ...args) {
		return args!=null && args.length > 0 && args[args.length -1] instanceof Throwable ? (Throwable) args[args.length -1]: null;
	 	
	}
	
	@Override
	public Map<String, Object> getDetails() {
		return details;
	}
}
