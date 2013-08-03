package com.agneya.util;

import java.util.logging.Filter;
import java.util.logging.LogRecord;


public class LogFilter implements Filter {
	public boolean isLoggable(LogRecord lr) {
		String logger = lr.getLoggerName();
		if (logger.startsWith("java") || logger.startsWith("sun") || logger.startsWith("org") 
                // || logger.startsWith("com.agneya")  || logger.startsWith("com.golconda")
               // || logger.startsWith("com.poker.nio")
                ) {
			return false;
		}
		return true;
	}
}
