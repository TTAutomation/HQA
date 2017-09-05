/******************************************************************************
$Id : EmailAlerter.java 3/3/2016 6:00:45 PM
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

package cbf.reporting;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cbf.engine.TestResult;
import cbf.engine.TestResult.ResultType;
import cbf.harness.ResourcePaths;
import cbf.model.ResultReporter;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbf.utils.Utils;

/**
 * 
 * Implements ResultReporter and sends mail to recipient mentioned in config
 * with execution failure details
 * 
 */
public class EmailAlerter implements ResultReporter {

	/**
	 * Constructor to initialize parameters
	 */
	public EmailAlerter(Map params) {
		logger.trace("In Email Alerter with parameters ", params);
		toField = (String) params.get("MailTo");
		subject = "Failure in Automated Execution of TestCase : ";
		body = "Hello,<br><br> Please find more details regarding the test case failure in below tables. <br><br> Also PFA failure screenshot for your reference. <br><br>";
		Object[] t = { "delete", "delete" };
		tsMap = Utils.toMap(t);
	}

	/**
	 * Send email with gathered execution failure results
	 */
	public void close() {
		if (isFailed) {
			tsBody = makeHTMLTable(tsMap);
			body = body + "<br>" + tcBody + "<br><br>" + tsBody
					+ "<br><br>Regards,<br>Automation Team";
			sendMail(toField, subject, body, ((Map) tsMap)
					.get("ScreenDumpPath").toString());
		}

	}

	/**
	 * Reporter open method
	 * 
	 * @param headers
	 *            contains header info, like run name, config details etc
	 */
	public void open(Map headers) {
		// TODO Auto-generated method stub

	}

	/**
	 * Reporter finish method
	 * 
	 * @param result
	 *            execution result
	 * @param rsType
	 *            execution result type(Passed, failed etc)
	 * @param details
	 *            execution details
	 */
	public void finish(TestResult result, ResultType rsType, Object details) {
		report("FINISH", result, details);

	}

	/**
	 * Reports execution results
	 * 
	 * @param eventType
	 *            type of entity
	 * @param result
	 *            object of TestResult
	 * @param eventData
	 *            object containing data related to event
	 */
	public void report(String eventType, TestResult result, Object eventData) {

		switch (result.entityType) {
		case TESTCASE:
			if (eventType.equals("FINISH")) {
				subject = subject + result.entityName.toString();
				Object[] temp = { "Test Case Name",
						result.entityName.toString() };
				Map tcMap = Utils.toMap(temp);
				String status = eventData.toString();
				if (eventData.toString().toLowerCase().contains("fail"))
					status = "<b><font color='#FF0000'>" + status
							+ "<font></b>";
				else
					status = "<b><font color='#00FF00'>" + status
							+ "<font></b>";
				tcMap.put("Test Case Status", status);
				tcMap.put("Start Time", result.startTime);
				tcMap.put("End Time", result.finishTime);
				tcBody = makeHTMLTable(tcMap);
			}
			break;
		case ITERATION:
			break;
		case TESTSTEP:
			if (eventType.equals("FINISH")
					&& !result.finalRsType.toString().toUpperCase()
							.equals("PASSED")) {
				isFailed = true;
				tsMap.put("Step Name", result.entityName.toString());
				tsMap.remove("delete");
			}
			break;
		case COMPONENT:
			if (eventType.equals("DETAILS")) {
				tsMap.put("Expected Result", ((Map) eventData).get("expected"));
				tsMap.put("Actual Result", ((Map) eventData).get("actual"));
				tsMap.put("ScreenDumpPath", ((Map) result.miscInfo
						.get("screenDump")).get("filePath"));

			}
			break;
		}

	}

	/**
	 * Makes email body using the execution details
	 * 
	 * @param result
	 *            execution results
	 * @param rsType
	 *            execution result type(Passed, failed etc)
	 * @param details
	 *            execution result details
	 */
	public void log(TestResult result, ResultType rsType, Map details) {
		report("DETAILS", result, details);

	}

	/**
	 * Reporter start
	 * 
	 * @param result
	 *            object of TestResult
	 */
	public void start(TestResult result) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sends the email to specified recipient
	 * 
	 * @param toField
	 *            to whom email has to be sent
	 * @param subject
	 *            subject of email
	 * @param body
	 *            execution details for failure
	 * @param attachments
	 *            error screenshots
	 */
	private void sendMail(String toField, String subject, String body,
			String attachments) {

		String pathSendMailVbs = ResourcePaths.getInstance()
				.getFrameworkResource("Resources", "vbUtils.vbs");
		logger.trace("Path of vbUtils ", pathSendMailVbs);
		if (attachments == "") {
			attachments = "BLANK";
		}
		try {
			logger.trace("wscript " + pathSendMailVbs + " \"" + toField
					+ "\" \"" + subject + "\" \"" + body + "\" \""
					+ attachments + "\"");
			Runtime.getRuntime().exec(
					"wscript " + pathSendMailVbs + " \"" + toField + "\" \""
							+ subject + "\" \"" + body + "\" \"" + attachments
							+ "\"");
		} catch (IOException e) {

		}
	}

	/**
	 * Makes a table with execution results
	 * 
	 * @param Data
	 *            execution failure data
	 * @return failure result
	 */
	private String makeHTMLTable(Map Data) {
		String result = "";
		Iterator<Entry<Object, Object>> it = Data.entrySet().iterator();
		result = result
				+ "<table border='2' style='font-size:14px; border:2px solid #98bf21; padding:3px 7px 2px 7px;'>";
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String name = (String) entry.getKey();
			String value = entry.getValue().toString();
			result = result + "<tr><td><b>" + name + "</b></td><td>" + value
					+ "</td></tr>";
		}
		result = result + "</table>";
		return result;
	}

	/**
	 * Returns Email Alerter format string
	 */
	public String toString() {
		return StringUtils.mapString(this, toField);

	}

	private LogUtils logger = new LogUtils(this);
	private String toField = "";
	private String subject = "";
	private String body = "";
	private String tcBody = "";
	private String tsBody = "";
	private static boolean isFailed = false;
	private static Map tsMap;
}
