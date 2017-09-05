/******************************************************************************
$Id : TestResultLogger.java 3/3/2016 6:00:14 PM
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

package cbf.engine;

import cbf.engine.TestResult.ResultType;
import cbf.utils.StringUtils;
import cbf.utils.Utils;

/**
 * 
 * Logs the execution results in reports
 * 
 */
public class TestResultLogger {
	
	private TestResultLogger(TestResultTracker tracker) {
		TestResultLogger.tracker = tracker;
	}

	/**
	 * Method to create a singleton object of TestResultLogger class
	 * 
	 * @param tracker
	 *            object of TestResultTracker
	 */

	public static TestResultLogger getInstance(TestResultTracker tracker) {
		if (RESULT == null)
			RESULT = new TestResultLogger(tracker);
		return RESULT;
	}

	/**
	 * Logs TestStep result as passed in report
	 * 
	 * @param name
	 *            name of TestStep
	 * @param expected
	 *            expected result of TestStep
	 * @param actual
	 *            actual result of TestStep
	 */
	public static void passed(String name, String expected, String actual) {
		log(name, ResultType.PASSED, expected, actual, true);
	}

	/**
	 * Logs TestStep result as failed in report
	 * 
	 * @param name
	 *            name of TestStep
	 * @param expected
	 *            expected result of TestStep
	 * @param actual
	 *            actual result of TestStep
	 */
	public static void failed(String name, String expected, String actual) {
		log(name, ResultType.FAILED, expected, actual, true);
	}

	/**
	 * Logs TestStep result as error in report
	 * 
	 * @param name
	 *            name of TestStep
	 * @param expected
	 *            expected result of TestStep
	 * @param actual
	 *            actual result of TestStep
	 */
	public static void error(String name, String expected, String actual) {
		log(name, ResultType.ERROR, expected, actual, true);
	}

	/**
	 * Logs TestStep result as done in report
	 * 
	 * @param name
	 *            name of TestStep
	 * @param expected
	 *            expected result of TestStep
	 * @param actual
	 *            actual result of TestStep
	 */
	public static void done(String name, String expected, String actual) {
		log(name, ResultType.DONE, expected, actual, true);
	}

	/**
	 * Logs TestStep result as warning in report
	 * 
	 * @param name
	 *            name of TestStep
	 * @param expected
	 *            expected result of TestStep
	 * @param actual
	 *            actual result of TestStep
	 */
	public static void warning(String name, String expected, String actual) {
		log(name, ResultType.WARNING, expected, actual, true);
	}

	/**
	 * Logs the execution results in report as per the inputs
	 * 
	 * @param name
	 *            name of TestStep
	 * @param rsType
	 *            ResultType of TestStep
	 * @param expected
	 *            expected result of TestStep
	 * @param actual
	 *            actual result of TestStep
	 */
	public static void log(String name, ResultType rsType, String expected,
			String actual, boolean screenDump) {
		tracker.log(rsType, Utils.toMap(new Object[] { "name", name,
				"expected", expected, "actual", actual, "screenDump",
				new Boolean(screenDump) }));
	}

	/**
	 * Returns TestResultLogger format string
	 */
	public String toString() {
		return StringUtils.mapString(this, tracker);
	}
	
	private static TestResultTracker tracker;
	private static TestResultLogger RESULT;
}
