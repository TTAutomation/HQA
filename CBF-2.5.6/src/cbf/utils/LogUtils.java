/******************************************************************************
$Id : LogUtils.java 3/3/2016 6:01:05 PM
Copyright 2016-2017 IGATE GROUP OF COMPANIES. All rights reserved
(Subject to Limited Distribution and Restricted Disclosure Only.)
THIS SOURCE FILE MAY CONTAIN INFORMATION WHICH IS THE PROPRIETARY
INFORMATION OF IGATE GROUP OF COMPANIES AND IS INTENDED FOR USE
ONLY BY THE ENTITY WHO IS ENTITLED TO AND MAY CONTAIN
INFORMATION THAT IS PRIVILEGED, CONFIDENTIAL, OR EXEMPT FROM
DISCLOSURE UNDER APPLICABLE LAW.
YOUR ACCESS TO THIS SOURCE FILE IS GOVERNED BY THE TERMS AND
CONDITIONS OF AN AGREEMENT BETWEEN YOU AND IGATE GROUP OF COMPANIES.
The USE, DISCLOSURE REPRODUCTION OR TRANSFER OF THIS PROGRAM IS
RESTRICTED AS SET FORTH THEREIN.
******************************************************************************/

package cbf.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import cbf.harness.Harness;
import cbf.harness.ResourcePaths;

/**
 * 
 * Utility provides functionalities related to logging and error handling Levels
 * of logging: debug|trace|details|warning|error
 * 
 * Logging utilities accept varargs of objects. StringUtils is used to
 * stringify.
 * 
 * handleError if first vararg (after text msg) is an Exception, it is treated
 * as root cause
 */
public class LogUtils {

	/**
	 * Overloaded constructor to initialize owner
	 * 
	 * @param owner
	 *            value of owner
	 */
	public LogUtils(Object owner) {
		this.owner = owner;
	}

	public void initialize(String runHome, String configFile) {
		System.setProperty("rootPath", runHome);
		if (logInitialized == false) {
			try {
				DOMConfigurator.configure(configFile);
			} catch (Exception e) {
				handleError("Error in loading configuration", configFile, e);
			}
			logInitialized = true;
		}
	}

	/**
	 * Default constructor
	 */
	public LogUtils() {
		this("<Null>");
	}

	/**
	 * Logs arguments as debug
	 * 
	 * @param varargs
	 *            object of arguments
	 */
	public void debug(Object... varargs) {
		logger.debug(StringUtils.toString(owner) + DELIM + toString(varargs));
	}

	/**
	 * Logs arguments as trace
	 * 
	 * @param varargs
	 *            object of arguments
	 */
	public void trace(Object... varargs) {
		logger.trace(StringUtils.toString(owner) + DELIM + toString(varargs));
	}

	/**
	 * Logs arguments as detail
	 * 
	 * @param varargs
	 *            object of arguments
	 */
	public void detail(Object... varargs) {
		logger.info(StringUtils.toString(owner) + DELIM + toString(varargs));
	}

	/**
	 * Logs arguments as warning
	 * 
	 * @param varargs
	 *            object of arguments
	 */
	public void warning(Object... varargs) {
		logger.warn(StringUtils.toString(owner) + DELIM + toString(varargs));
	}

	/**
	 * Logs arguments as error
	 * 
	 * @param varargs
	 *            object of arguments
	 */
	public void error(Object... varargs) {
		logger.error(StringUtils.toString(owner) + DELIM + toString(varargs));

	}

	/**
	 * Handles error and logs arguments
	 * 
	 * @param msg
	 *            text message for exception
	 * @param varargs
	 *            object of arguments
	 */
	public void handleError(String msg, Object... varargs) {
		Exception rootCause = null;
		if (varargs.length != 0) {
			if (varargs[0] instanceof Exception) {
				rootCause = (Exception) varargs[0];
			}
		}
		String text = "Error: " + msg + DELIM + toString(varargs);
		error(text);

		// Do **not** repeatedly log framework exception's
		if (rootCause != null && !(rootCause instanceof FrameworkException)) {
			error(msg, rootCause.getStackTrace());
		}

		throw new FrameworkException(text, rootCause);
	}

	/**
	 * TODO: public void handleError(String msg, Exception exception, Object...
	 * varargs) Useful in highlighting root cause
	 */

	/**
	 * Rethrow an exception after due logging
	 * 
	 * @param msg
	 *            text message for exception
	 * @param exception
	 *            object of FrameworkException
	 */
	public void handleError(String msg, FrameworkException exception) {
		error("Error: " + msg + DELIM + "Re-throwing exception");
		throw exception;
	}

	private static final String DELIM = "|";

	private static String toString(Object... varargs) {
		String s = "", delim = "";
		for (Object obj : varargs) {
			s += delim + StringUtils.toString(obj);
			delim = DELIM;
		}
		return s;
	}

	/**
	 * Returns Log Utils format string
	 * 
	 */
	public String toString() {
		return StringUtils.mapString(this);

	}

	private Logger logger = Logger.getLogger(this.getClass());
	private static boolean logInitialized = false;
	private Object owner;
}
