package edu.ncsu.csc.nl.util;

public class Logger {

	public static final int LEVEL_TRACE = 1;
	public static final int LEVEL_DEBUG = 3;
	public static final int LEVEL_INFO  = 5;
	public static final int LEVEL_WARN  = 7;
	public static final int LEVEL_ERROR = 9;
	public static final int LEVEL_FATAL = 11;
	
	private static int _currentLoggingLevel = LEVEL_INFO;
	
	private static int _holdLoggingLevel = LEVEL_INFO;  // always code to switch to a new level, and then restore the level without having to save the value themselves
	
	private static final java.io.PrintStream ps = System.out;
	
	private static final java.text.SimpleDateFormat _sdf = new java.text.SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
	
	/** sets a new logging level.  Any messages passed at this level or higher will be printed to the console */
	public static void setCurrentLoggingLevel(int newLoggingLevel) {
		_currentLoggingLevel = newLoggingLevel;
	}

	/** returns the current logging level */
	public static int getCurrentLoggingLevel() {
		return _currentLoggingLevel;
	}

	/** prints the message to the log if the current logging level is greater than or equal to the passed in level */
	public static void log(int level, String message) {
		if (level >= _currentLoggingLevel) {
			ps.print(_sdf.format(new java.util.Date()));
			ps.print(": ");
			ps.println(message);
		}
	}
		
	/** prints the object to the log (with no timestamp) if the current logging level is greater than or equal to the passed in level */
	public static void logObject(int level, Object objectValue) {
		if (level >= _currentLoggingLevel) {
			ps.println(objectValue);
		}
	}	

	
	public static void switchToLevel(int newLevel) {
		_holdLoggingLevel = _currentLoggingLevel;
		_currentLoggingLevel = newLevel;
	}
	
	public static void restoreLoggingLevel() {
		_currentLoggingLevel = _holdLoggingLevel;
	}

}
