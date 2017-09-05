/******************************************************************************
$Id : TestResultTracker.java 3/3/2016 6:00:14 PM
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cbf.engine.TestResult.EntityType;
import cbf.engine.TestResult.ResultType;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbf.utils.Utils;

/**
 * 
 * Adds Reporters and tracks execution results for reporting
 * 
 */
public class TestResultTracker {
	/**
	 * 
	 * Logs execution events with details
	 * 
	 */
	public interface Observer {
		/**
		 * Starts Reporter
		 * 
		 * @param result
		 *            actual e
		 */
		public void start(TestResult result);

		/**
		 * Logs details in report
		 * 
		 * @param result
		 *            execution result
		 * @param rsType
		 *            result type of the current executed entity
		 * @param details
		 *            result details of the current executed entity
		 */
		public void log(TestResult result, ResultType rsType, Map details);

		/**
		 * Finishes the Reporter
		 * 
		 * @param result
		 *            execution result
		 * @param rsType
		 *            result type of the current executed entity
		 * @param details
		 *            result details of the current executed entity
		 */
		public void finish(TestResult result, ResultType rsType, Object details);
	};

	/**
	 * Constructor to initialize Reporter's list
	 * 
	 * @param oReporter
	 *            List of Reporters
	 */
	public TestResultTracker(List<Observer> oReporter) {
		reporters = oReporter;
	}

	/**
	 * Adds input reporter to selected report list
	 * 
	 * @param reporter
	 *            object of Reporter like excel, html etc
	 */
	public void addReporter(Observer reporter) {
		try {
			reporters.add(reporter);
		} catch (Exception e) {
			logger.handleError("Can not add reporter ", reporter, e);
		}
	}

	/**
	 * 
	 * Tracks logger
	 * 
	 */
	public interface Trackable {
		// results have to be added/set to this result object

		/**
		 * Tracks execution results taking TestResult object
		 * 
		 * @param result
		 *            object of testResult
		 */
		public void run(TestResult result) throws Exception;
	}

	/**
	 * Tracks logger and returns TestResult object
	 * 
	 * @param trackable
	 *            object of Trackable
	 * @param entityType
	 *            type of entity
	 * @param entityName
	 *            name of entity
	 * @param entity
	 *            object having entity details
	 * @return result
	 */
	public TestResult track(Trackable trackable,
			TestResult.EntityType entityType, String entityName, Object entity) {
		TestResult result = start(leafResult, entityType, entityName, entity);

		logger.detail("START : "
				+ Thread.currentThread().getStackTrace()[4].getMethodName());
		try {
			trackable.run(result);
		} catch (Exception e) {
			result
					.add(ResultType.ERROR,
							toMap(new Object[] { "exception", e }));
		}
		logger.detail("FINISH : "
				+ Thread.currentThread().getStackTrace()[4].getMethodName()
				+ " STATUS : " + result.finalRsType);

		finish();

		return result;
	}

	/**
	 * Logs ResultType and TestResult details
	 * 
	 * @param rsType
	 *            result type of the current executed entity
	 * @param details
	 *            Object containing TestResult details
	 */
	public void log(ResultType rsType, Object details) {
		if (leafResult.entityType == EntityType.TESTCASE) { // details is
			// TestCase
			leafResult.entityDetails = details;
			return; // consume this event
		}
		leafResult.add(rsType, (Map) details);
		logger.trace("Report Event: type:" + rsType.toString() + ":details:"
				+ toString(details).replace(",", ";"));
		for (Observer reporter : reporters)
			reporter.log(leafResult, rsType, (Map) details);
	}

	TestResult leafResult = null;

	/************* Functions ********************/
	private TestResult start(TestResult testResult,
			TestResult.EntityType entityType, String entityName,
			Object entityDetails) {
		TestResult result = new TestResult(leafResult, entityType, entityName,
				entityDetails);
		result.startTime = new Date();
		leafResult = result;
		for (Observer reporter : reporters)
			reporter.start(result);
		return result;
	}

	private void finish() {
		TestResult result = leafResult;
		if (result == null) {
			logger.handleError("No result when details called ");
		}
		result.finishTime = new Date();
		leafResult = result.parent;
		if (leafResult != null) {
			leafResult.add(result.msRsType, result);
		}
		for (Observer reporter : reporters)
			reporter.finish(result, result.msRsType, result.finalRsType);

	}

	private static String toString(Object o) {
		return StringUtils.toString(o);
	}

	private static Map toMap(Object[] o) {
		return Utils.toMap(o);
	}

	/**
	 * Returns TestResultTracker format string
	 * 
	 */
	public String toString() {
		return StringUtils.mapString(this, reporters);
	}

	private LogUtils logger = new LogUtils(this);
	private List<Observer> reporters = new ArrayList<Observer>();
}
