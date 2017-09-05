/******************************************************************************
$Id : ReportingManager.java 3/3/2016 6:00:38 PM
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

package cbf.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cbf.engine.TestResult;
import cbf.engine.TestResult.ResultType;
import cbf.engine.TestResultTracker.Observer;
import cbf.model.ResultReporter;
import cbf.utils.Configuration;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

/**
 * A reporter, which manages other reports Understands the dependency across
 * reports
 **/
public class ReportingManager implements ResultReporter {
	public ReportingManager(Object reporterObj, Configuration CONFIG) {
		this.CONFIG = CONFIG;
		selectReporters(reporterObj);
	}

	/**
	 * Opens the report file and updates the headers as required
	 * 
	 * @param headers
	 *            run details to be updated in report
	 * 
	 */
	public void open(Map headers) {
		// 'Start all reporters
		for (Observer reporter : reporters) {

			try {
				((ResultReporter) reporter).open(headers);
				logger.trace("opened reporter: ", reporter);
			} catch (Exception e) {
				logger.handleError("Error opening reporter", e, reporter);
			}
		}
	}

	/**
	 * Closes the report file
	 */
	public void close() {
		// 'Stop all reporters
		for (Observer reporter : reporters) {

			try {
				((ResultReporter) reporter).close();
			} catch (ClassCastException e) {
				// logger.warning("Error closing reporter - ", reporter, e);
			}
		}
	}

	/**
	 * Starts the specific reporter
	 * 
	 * @param result
	 *            object of TestResult
	 */
	public void start(TestResult result) {
		for (Observer reporter : reporters) {
			try {
				reporter.start(result);
			} catch (Exception e) {
				logger.warning("Error in start - ", reporter, result, e);
			}
		}
	}

	/**
	 * Logs specified reporter
	 * 
	 * @param result
	 *            entity details
	 * @param rsType
	 *            result type of the current executed entity
	 * @param details
	 *            execution details of the current executed entity
	 */
	public void log(TestResult result, ResultType rsType, Map details) {
		for (Observer reporter : reporters) {
			try {
				reporter.log(result, rsType, details);
			} catch (Exception e) {
				logger.warning("Error in reporting - ", reporter + " - "
						+ result + " - " + rsType + " - " + details, e);
			}
		}
	}

	/**
	 * Reporter finish method
	 * 
	 * @param result
	 *            execution details
	 * @param rsType
	 *            result type of the current executed entity
	 * @param details
	 *            execution details of the current executed entity
	 */
	public void finish(TestResult result, ResultType rsType, Object details) {
		for (Observer reporter : reporters) {
			try {
				reporter.finish(result, rsType, details);
			} catch (Exception e) {
				logger.warning("Error in finish - ", reporter + " - " + result
						+ " - " + rsType + " - " + details, e);
			}
		}
	}

	public List<Observer> getReporters() {
		return reporters;
	}

	private void selectReporters(Object reporterObj) {

		/*
		 * ' Adds reporter in the correct order of dependency ' Caveat:Any
		 * invalid selection is silently skipped without warning
		 */

		String reporterSelection = "";
		List<Map<String, Object>> reporterList = (ArrayList<Map<String, Object>>) reporterObj;
		if (!reporterObj.toString().equals("{}")) {
			int i = 0;
			for (Map<String, Object> map : reporterList) {
				if (i == 0) {
					reporterSelection = (String) map.get("plugin");
					i++;
				} else
					reporterSelection = reporterSelection + ","
							+ (String) map.get("plugin");
			}
		}
		String[] reportsSelected = null;
		if (reporterSelection != null && !reporterSelection.equals("")) {
			reporterSelection = reporterSelection.trim();
			reportsSelected = reporterSelection.split(",");
		}
		initializeReporters(reportsSelected, reporterList);
	}

	private void initializeReporters(String[] reportsSelected,
			List<Map<String, Object>> reporterList) {

		for (String reporterName : supportedReports) {
			boolean isSelected = false;
			if (reportsSelected == null) {
				isSelected = true; // ' Default: all
			} else {
				for (String selReport : reportsSelected) {
					if (selReport.equalsIgnoreCase(reporterName)) {
						isSelected = true;
						break;
					}
				}
			}
			if (isSelected) {
				Map<String, Object> reporterMap = traverseArray(reporterList,
						reporterName);
				Observer reporter = null;
				try {
					reporter = (Observer) PluginManager.getPlugin(reporterMap);
				} catch (ClassCastException c) {
					logger.handleError(reporterName
							+ " plugin does not match a valid reporter", c,
							reporterMap);

				}
				reporters.add(reporter);
				logger.trace("Reporter selected: ", reporter);
			}
		}
		/*
		 * for (Reporter reporter : reporters) ((ResultReporter)
		 * reporter).open(GCONFIG.getAllProperties());
		 */
		open(CONFIG.getAllProperties());
	}

	private Map<String, Object> traverseArray(
			List<Map<String, Object>> reporterList, String key) {

		for (Map<String, Object> innerMap : reporterList) {
			for (String str : innerMap.keySet()) {

				if (str.equalsIgnoreCase("plugin")) {

					if (innerMap.get(str).equals(key)) {
						return innerMap;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns PluginManager format string
	 */

	public String toString() {
		return StringUtils.mapString(this, CONFIG);
	}

	// new reports are added
	final String[] supportedReports = { "ScreenDump", "HtmlEvent",
			"ExcelReport", "ResultEventLogger", "EmailAlert",
			"JenkinsScreenDump", "JenkinsHtmlEvent", "JenkinsExcelReport",
			"TestLink", "JenkinsResultEventLogger", "AlmReporter" }; /*
																	 * "ExcelSummary"
																	 * old style
																	 * is
																	 * disabled
																	 * as a
																	 * default
																	 */

	public List<Observer> reporters = new ArrayList<Observer>(); /*
																 * Holds the
																 * managed
																 * reporters
																 * collection
																 */
	private Configuration CONFIG;
	private LogUtils logger = new LogUtils(this);
}
