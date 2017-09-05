/******************************************************************************
$Id : HtmlEventReporter.java 3/3/2016 6:00:46 PM
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import cbf.engine.TestResult;
import cbf.engine.TestResult.ResultType;
import cbf.harness.ResourcePaths;
import cbf.model.ResultReporter;
import cbf.model.TestCase;
import cbf.model.TestIteration;
import cbf.utils.FileUtils;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

/**
 * 
 * Implements ResultReporter and generates HTML reports
 * 
 */
public class HtmlEventReporter implements ResultReporter {

	/**
	 * Constructor to initialize parameters
	 * 
	 * @param params
	 *            map containing parameters
	 */

	public HtmlEventReporter(Map params) {
		this.params = params;
		summaryPath = (String) params.get("summaryPath");
		if (summaryPath.equals("")) {
			summaryPath = ResourcePaths.getInstance().getRunResource("", "");
		}
		if (!(FileUtils.makeFolder(summaryPath))) {
			logger.handleError("Cant create/access html reports folder; these will not be generated: "
					+ summaryPath);
		}

		detailsPath = (String) params.get("detailsPath");
		if (detailsPath.equals("")) {
			detailsPath = ResourcePaths.getInstance().getRunResource(
					"HtmlEvents", "");
		}
		if (!(FileUtils.makeFolder(detailsPath))) {
			logger.handleError("Cant create/access html reports folder; these will not be generated: "
					+ detailsPath);
		}

		logoPath = ResourcePaths.getInstance().getFrameworkResource(
				"Misc_ResultsViewer", "igate.jpg");
	}

	/**
	 * Reporter open method
	 * 
	 * @param headers
	 *            contains header info, like run name, config details etc
	 */
	public void open(Map headers) {
	}

	/**
	 * Reporter close method
	 */
	public void close() {
	}

	/**
	 * Reports entity execution start details
	 * 
	 * @param result
	 *            entity object
	 */
	public void start(TestResult result) {
		report("START", result, result.entityDetails);
	}

	/**
	 * Logs execution details in report
	 * 
	 * @param result
	 *            entity details
	 * @param rsType
	 *            result type of the current executed entity
	 * @param details
	 *            execution details of the current executed entity
	 */
	public void log(TestResult result, ResultType rsType, Map details) {
		report("DETAILS", result, details);
	}

	/**
	 * Reports execution details along with result counts
	 * 
	 * @param result
	 *            execution details
	 * @param rsType
	 *            result type of the current executed entity
	 * @param details
	 *            execution details of the current executed entity
	 */
	public void finish(TestResult result, ResultType rsType, Object details) {
		report("FINISH", result, result.entityDetails);
	}

	private void writeTestSet() {
System.out.printf("TestSet " + params.get("pattern") + " .html", "TestSet");
		startSummaryFile("TestSet" + params.get("pattern") + ".html", "TestSet");
		String str = "";
		str = "<td>" + testCases + "</td><td>" + failed + "</td><td>"
				+ testStart + "</td><td>" + testFinish + "</td><td>"
				+ calculateDuration(testFinish, testStart) + "</td><td>"
				+ eReportLink("ExecutionReport");
		testSetArray.add(str);
		logSummary(testSetArray);
		testCaseSummaryTemplate();
		logSummary(testCasesArray);
		finishSummaryFile();
	}

	private void report(String eventType, TestResult result, Object eventData) {
		String str1 = "";
		try {
			switch (result.entityType) {
			case ITERATION:
				if (eventType.equals("START")) {
					if (testStart == null)
						testStart = result.startTime;
					TestIteration iteration = (TestIteration) eventData;
					String iterName = "";
					Map iterParams = iteration.parameters();
					if (iterParams != null) {
						if (iterParams.containsKey("_rowId"))
							iterName = iterParams.get("_rowId").toString();
						if (iterName == null || iterName.equals("")) {
							iterName = "("
									+ result.parent.childCount
									+ " of "
									+ ((TestCase) result.parent.entityDetails)
											.iterationCount() + ")";
						}
					}
					tcName = result.parent.entityName;
					if (iterName != null) {
						tcName = tcName + " " + iterName;
					}

					detailsArray.add("<td colspan = 8 align = center>" + tcName
							+ "</td>");

				} else if (eventType.equals("FINISH")) {
					testCases++;
					testFinish = result.finishTime;
					String str = "";
					String status = "";
					if (result.msRsType.isPassed())
						status = "Passed";
					else {
						status = "Failed";
						failed++;
					}
					str = "<td>"
							+ ((TestIteration) eventData).stepCount()
							+ "</td><td>"
							+ result.childCount
							+ "</td><td>"
							+ result.startTime
							+ "</td><td>"
							+ result.finishTime
							+ "</td><td>"
							+ calculateDuration(result.finishTime,
									result.startTime);

					str1 = "<td>" + tcName + "</td><td>" + status + "</td>"
							+ str;
					summaryArray.add(str1);
					str1 = "<td>"
							+ tcName
							+ "</td><td>"
							+ testCaseFileLink(
									entityName + params.get("pattern"), status)
							+ "</td>" + str;
					testCasesArray.add(str1);
				}
				break;
			case TESTCASE:
				if (eventType.equals("START")) {
					entityName = result.entityName;
					testSetPath = "../TestSet" + params.get("pattern")
							+ ".html";
					startDetailsFile(entityName + params.get("pattern")
							+ ".html", entityName);
				} else if (eventType.equals("FINISH")) {
					logDetails(summaryArray);
					componentsTemplate();
					logDetails(detailsArray);

					finishDetailsFile();
					writeTestSet();
				}
				break;
			case TESTSTEP:
				if (eventType.equals("START")) {
					String str = "<td colspan = 8 align = center >"
							+ result.entityName + "</td>";
					startTime = result.startTime;
					// detailsArray.add(str);
				} else if (eventType.equals("FINISH")
						&& !(result.finalRsType.equals(ResultType.DONE))) {

					String str = "<td + rightSpan + >"
							+ result.entityName.toString() + "</td><td/><td/>"
							+ "<td>";

					if (result.msRsType.isPassed()) {
						str = str + "PASSED";
					} else {
						str = str + "FAILED";
					}
					str = str + "</td><td>" + result.startTime + "</td>";
					str = str + "<td>" + result.finishTime + "</td>";
					str = str
							+ "<td>"
							+ calculateDuration(result.finishTime,
									result.startTime) + "</td>";

				}
				break;
			case COMPONENT:
				if (eventType.equals("DETAILS")) {

					Map detailsMap = (Map) eventData;

					String str = "<td + rightSpan + >"
							+ detailsMap.get("name").toString() + "</td>";

					str = str + "<td>" + result.finalRsType + "</td>";
					str = str + "<td>" + detailsMap.get("expected") + "</td>";
					str = str
							+ "<td>"
							+ screenDumpLink((String) detailsMap.get("actual"),
									result);
					str = str + "<td>" + startTime + "</td>";
					str = str + "<td>" + new Date() + "</td>";
					str = str + "<td>"
							+ calculateDuration(new Date(), startTime)
							+ "</td>";
					startTime = new Date();
					detailsArray.add(str);
				}
			}
		} catch (Exception e) {
			logger.handleError("Error in HTML reporting", e);
		}

	}

	private String stylingTemplate() {
		String str = "body {background-color: #FFFFCC;}";
		str = str + "table {background-color: #DCDCDC; text-align: center;}";

		str += "th { background-color: #003399;text-align: center;  color: #FFFFFF; font-family:  Candara, Calibri, Segoe, 'Segoe UI', Optima, Arial, sans-serif;font-size: 15px;}";
		str += "tr { background-color: #E6E6E6; color: #1F1F7A;  font-family:  Candara, Calibri, Segoe, 'Segoe UI', Optima, Arial, sans-serif;font-size: 13px;}";
		str += "tr.d0 td { background-color: #01A9DB; }";
		str += "h1 { align=center; text-align: center; color: #003399; font-family:  Candara, Calibri, Segoe, 'Segoe UI', Optima, Arial, sans-serif;font-size: 25px; }";
		str += "h4 {text-align=right;font-family:  Candara, Calibri, Segoe, 'Segoe UI', Optima, Arial, sans-serif;font-size: 13px; }";
		return str;
	}

	private void startDetailsFile(String fileName, String title) {
		openDetailsFile(detailsPath + "/" + fileName);

		writeDetails("<html><head><style>");
		writeDetails(stylingTemplate());
		writeDetails("<title>" + title + "</title>");
		writeDetails("</style></head><body>");
		writeDetails("<img src=" + logoPath + " align=right>");
		writeDetails("<h1>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<u>TEST CASE REPORT</u></h1>");
		testCaseDetailsTemplate();

	}

	private void startSummaryFile(String fileName, String title) {
		openSummaryFile(summaryPath + "/" + fileName);

		writeSummary("<html><head><style>");
		writeSummary(stylingTemplate());
		writeSummary("<title>" + title + "</title>");

		writeSummary("</style></head><body>");
		writeSummary("<img src=" + logoPath + " align=right>");
		writeSummary("<h1>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<u>TEST SET REPORT</u></h1>");
		testSetTemplate();

	}

	private void finishDetailsFile() {
		writeDetails("</table><h4 align=right><a href='" + testSetPath
				+ "'>TestSetFile</a></h4></body></html>");

		closeDetailsFile();
	}

	private void finishSummaryFile() {
		writeSummary("</table></body></html>");

		closeSummaryFile();
	}

	private void testSetTemplate() {
		writeSummary("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<table align=center border=3 width=100%>");
		writeSummary("<th colspan=7>TEST SET SUMMARY</th>");
		writeSummary("<tr class=d0><td><b>Tests Executed</b></td><td><b>Tests Failed</b></td><td><b>Start time</b></td><td><b>End time</b></td><td><b>Duration</b></td><td><b>Execution Report</b></td></tr><br>");
	}

	private void testCaseDetailsTemplate() {
		writeDetails("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<table align=center border=3  width=100%>");
		writeDetails("<th colspan=7>TEST CASE SUMMARY</th>");
		writeDetails("<tr class=d0><td><b>TestCase Name</b></td><td><b>Status</b></td><td><b>No. of components</b></td><td><b>Components Run</b></td><td><b>Start time</b></td><td><b>End time</b></td><td><b>Duration</b></td></tr>");
	}

	private void testCaseSummaryTemplate() {
		writeSummary("<table align=center border=3 width=100%>");
		writeSummary("<br><br><br>");
		writeSummary("<th colspan=7>TEST CASE SUMMARY</th>");
		writeSummary("<tr class=d0><td><b>TestCase Name</b></td><td><b>Status</b></td><td><b>No. of components</b></td><td><b>Components Run</b></td><td><b>Start time</b></td><td><b>End time</b></td><td><b>Duration</b></td></tr>");
	}

	private void componentsTemplate() {
		writeDetails("<table align=center border=3 width=100%>");
		writeDetails("<br><br><br>");
		writeDetails("<th colspan=10>TEST CASE DETAILS</th>");
		writeDetails("<tr class=d0>"
				+ "<td><b>Step</b></td><td><b>Result</b></td><td><b>Expected Result</b></td><td><b>Actual Result</b></td><td><b>Start Time</b></td><td><b>End Time</b></td><td><b>Duration</b></td></tr>");
	}

	private void logDetails(ArrayList<String> strArray2) {
		for (String str : strArray2)

			writeDetails("<tr>" + str + "</tr>");
	}

	private void logSummary(ArrayList<String> strArray2) {
		for (String str : strArray2)

			writeSummary("<tr>" + str + "</tr>");
	}

	private String screenDumpLink(String name, TestResult eventData) {
		String sDumpFile;
		try {
			sDumpFile = (String) ((Map) ((Map) eventData.miscInfo)
					.get("screenDump")).get("filePath");
			sDumpFile = sDumpFile.replaceAll(ResourcePaths.getInstance()
					.getRunResource("", ""), "..");
		} catch (Exception e) {

			return name;
		}

		if (sDumpFile == null) {
			// return null;
			return name;
		}

		return "<a href='" + sDumpFile + "'>" + name + "</a>";
	}

	private String eReportLink(String name) {
		String filepath;
		try {
			filepath = "./" + name + params.get("pattern") + ".xls";

		} catch (Exception e) {
			logger.handleError(
					"Error in accesing excel report link in HTML report", name,
					e);
			return "";
		}

		if (filepath == null) {
			return "";
		}

		return "<a href='" + filepath + "'>" + name + "</a>";
	}

	private String testCaseFileLink(String name, String status) {
		String filepath;
		try {
			filepath = "./HtmlEvents/" + name + ".html";

		} catch (Exception e) {
			logger.handleError(
					"Error in accesing test Case link in HTML report", name, e);
			return "";
		}

		if (filepath == null) {
			return "";
		}

		return "<a href='" + filepath + "'>" + status + "</a>";
	}

	private String calculateDuration(Date d2, Date d1) {
		long diff = d2.getTime() - d1.getTime();

		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		try {
			String diffTime = cal(String.valueOf(diffHours)) + ":"
					+ cal(String.valueOf(diffMinutes)) + ":"
					+ cal(String.valueOf(diffSeconds));
			return diffTime;
		} catch (Exception e) {
			logger.handleError("Error in calculating duration in HTML report",
					e);
			return null;
		}

	}

	private String cal(String time) {
		while (time.length() != 2)
			time = "0" + time;
		return time;
	}

	private void openDetailsFile(String filePath) {
		try {
			fileWriter = new FileWriter(filePath);
			bufWriter1 = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			logger.handleError("Exception caught while trying to open a file ",
					filePath, e);
		}
	}

	private void openSummaryFile(String filePath) {
		try {
			fileWriter = new FileWriter(filePath);
			bufWriter2 = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			logger.handleError(
					"Exception caught : When trying to open a file ", filePath,
					e);
		}
	}

	private void closeDetailsFile() {
		try {
			bufWriter1.close();
		} catch (IOException e) {
			logger.handleError("Exception caught while closing details file ",
					e);
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				logger.handleError(
						"Exception caught while closing details file ", e);
			} finally {
				fileWriter = bufWriter1 = null;
			}
		}
	}

	private void closeSummaryFile() {
		try {
			bufWriter2.close();
		} catch (IOException e) {
			logger.handleError("Exception caught while closing summary file ",
					e);
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				logger.handleError(
						"Exception caught while closing summary file ", e);
			} finally {
				fileWriter = bufWriter2 = null;
			}
		}
	}

	private void writeDetails(String lines) {
		try {
			bufWriter1.write(lines);
		} catch (IOException e) {
			logger.handleError(
					"Exception caught while writing details in HTML : ", e);
		}
	}

	private void writeSummary(String lines) {
		try {
			bufWriter2.write(lines);
		} catch (IOException e) {
			logger.handleError(
					"Exception caught while writing details in HTML : ", e);
		}
	}

	/**
	 * Returns HtmlEventReporter along with html report folder path format
	 * string
	 */
	public String toString() {
		return StringUtils.mapString(this, params);

	}

	private Writer bufWriter1, bufWriter2, fileWriter;
	private LogUtils logger = new LogUtils(this);
	private String summaryPath, detailsPath, entityName, logoPath;
	private String tcName = "", testSetPath;
	private int failed = 0, testCases = 0;
	private Date testStart, testFinish, startTime = null;
	private ArrayList<String> detailsArray = new ArrayList<String>();
	private ArrayList<String> summaryArray = new ArrayList<String>();
	private ArrayList<String> testSetArray = new ArrayList<String>();
	private ArrayList<String> testCasesArray = new ArrayList<String>();
	private Map params;
}
