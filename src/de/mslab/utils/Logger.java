package de.mslab.utils;

/**
 * A simple logger.
 * 
 */
public class Logger {
	
	public static final String DEBUG = "DEBUG";
	public static final String INFO = "INFO";
	public static final String ERROR = "ERROR";
	public static final String WARNING = "WARNING";
	
	private static final Logger INSTANCE = new Logger();
	
	/**
	 * Indicates if this logger instance prints info messages.
	 */
	public boolean isInfoEnabled = true;
	/**
	 * Indicates if this logger instance prints error messages.
	 */
	public boolean isErrorEnabled = true;
	/**
	 * Indicates if this logger instance prints debug messages.
	 */
	public boolean isDebugEnabled = true;
	/**
	 * Indicates if this logger instance prints warning messages.
	 */
	public boolean isWarningsEnabled = true; 
	
	/**
	 * Accessor for a singleton logger instance.
	 * @return A singleton logger instance.
	 */
	public static Logger getLogger() {
		return INSTANCE;
	}
	
	/**
	 * Prints a message at debug level.
	 */
	public void debug(Object message) {
		if (isDebugEnabled) {
			print(DEBUG, paramToString(message));
		}
	}
	
	/**
	 * Prints a message at debug level.
	 */
	public void debug(String message, Object first) {
		if (isDebugEnabled) {
			print(DEBUG, replace(message, new Object[]{first}));
		}
	}
	
	/**
	 * Prints a message at debug level.
	 */
	public void debug(String message, Object first, Object second) {
		if (isDebugEnabled) {
			print(DEBUG, replace(message, new Object[]{first, second}));
		}
	}
	
	/**
	 * Prints a message at debug level.
	 */
	public void debug(String message, Object first, Object second, Object third) {
		if (isDebugEnabled) {
			print(DEBUG, replace(message, new Object[]{first, second, third}));
		}
	}
	
	/**
	 * Prints a message at debug level.
	 */
	public void debug(String message, Object first, Object second, Object third, Object fourth) {
		if (isDebugEnabled) {
			print(DEBUG, replace(message, new Object[]{first, second, third, fourth}));
		}
	}
	
	/**
	 * Prints a message at debug level.
	 */
	public void debug(String message, Object[] params) {
		if (isDebugEnabled) {
			print(DEBUG, replace(message, params));
		}
	}
	
	/**
	 * Prints a message at error level.
	 */
	public void error(Object message) {
		if (isErrorEnabled) {
			print(ERROR, paramToString(message));
		}
	}
	
	/**
	 * Prints a message at error level.
	 */
	public void error(String message, Object first) {
		if (isErrorEnabled) {
			print(ERROR, replace(message, new Object[]{first}));
		}
	}
	
	/**
	 * Prints a message at error level.
	 */
	public void error(String message, Object first, Object second) {
		if (isErrorEnabled) {
			print(ERROR, replace(message, new Object[]{first, second}));
		}
	}
	
	/**
	 * Prints a message at error level.
	 */
	public void error(String message, Object first, Object second, Object third) {
		if (isErrorEnabled) {
			print(ERROR, replace(message, new Object[]{first, second, third}));
		}
	}
	
	/**
	 * Prints a message at error level.
	 */
	public void error(String message, Object first, Object second, Object third, Object fourth) {
		if (isErrorEnabled) {
			print(ERROR, replace(message, new Object[]{first, second, third, fourth}));
		}
	}
	
	/**
	 * Prints a message at error level.
	 */
	public void error(String message, Object[] params) {
		if (isErrorEnabled) {
			print(ERROR, replace(message, params));
		}
	}
	
	/**
	 * Prints a message at info level.
	 */
	public void info(Object message) {
		if (isInfoEnabled) {
			print(INFO, paramToString(message));
		}
	}
	
	/**
	 * Prints a message at info level.
	 */
	public void info(String message, Object first) {
		if (isInfoEnabled) {
			print(INFO, replace(message, new Object[]{first}));
		}
	}
	
	/**
	 * Prints a message at info level.
	 */
	public void info(String message, Object first, Object second) {
		if (isInfoEnabled) {
			print(INFO, replace(message, new Object[]{first, second}));
		}
	}
	
	/**
	 * Prints a message at info level.
	 */
	public void info(String message, Object first, Object second, Object third) {
		if (isInfoEnabled) {
			print(INFO, replace(message, new Object[]{first, second, third}));
		}
	}
	
	/**
	 * Prints a message at info level.
	 */
	public void info(String message, Object first, Object second, Object third, Object fourth) {
		if (isInfoEnabled) {
			print(INFO, replace(message, new Object[]{first, second, third, fourth}));
		}
	}

	/**
	 * Prints a message at info level.
	 */
	public void info(String message, Object first, Object second, Object third, Object fourth, Object fifth) {
		if (isInfoEnabled) {
			print(INFO, replace(message, new Object[]{first, second, third, fourth, fifth}));
		}
	}
	
	/**
	 * Prints a message at info level.
	 */
	public void info(String message, Object[] params) {
		if (isInfoEnabled) {
			print(INFO, replace(message, params));
		}
	}
	
	/**
	 * Prints a message at warn level.
	 */
	public void warn(Object message) {
		if (isWarningsEnabled) {
			print(WARNING, paramToString(message));
		}
	}
	
	/**
	 * Prints a message at warn level.
	 */
	public void warn(String message, Object first) {
		if (isWarningsEnabled) {
			print(WARNING, replace(message, new Object[]{first}));
		}
	}
	
	/**
	 * Prints a message at warn level.
	 */
	public void warn(String message, Object first, Object second) {
		if (isWarningsEnabled) {
			print(WARNING, replace(message, new Object[]{first, second}));
		}
	}
	
	/**
	 * Prints a message at warn level.
	 */
	public void warn(String message, Object first, Object second, Object third) {
		if (isWarningsEnabled) {
			print(WARNING, replace(message, new Object[]{first, second, third}));
		}
	}
	
	/**
	 * Prints a message at warn level.
	 */
	public void warn(String message, Object first, Object second, Object third, Object fourth) {
		if (isWarningsEnabled) {
			print(WARNING, replace(message, new Object[]{first, second, third, fourth}));
		}
	}
	
	/**
	 * Prints a message at warn level.
	 */
	public void warn(String message, Object[] params) {
		if (isWarningsEnabled) {
			print(WARNING, replace(message, params));
		}
	}
	
	private String paramToString(Object param) {
		if (param != null) {
			return param.toString();
		} else {
			return "null";
		}
	}
	
	/**
	 * Replaces all occurances of {@code {i} in {@code message} with {@code params[i]}.
	 * @param message The message with log occurance tags placed within. 
	 * @param params An object with log contents that shall be placed in the message.
	 * @return The new message string.
	 */
	private String replace(String message, Object[] params) {
		Object param;
		String pattern;
		
		for (int i = 0; i < params.length; i++) {
			param = params[i];
			pattern = "\\{" + i + "\\}";
			message = message.replaceAll(pattern, paramToString(param));
		}
		
		return message;
	}
	
	private void print(String level, String message) {
		System.out.println(message);
	}
	
}
