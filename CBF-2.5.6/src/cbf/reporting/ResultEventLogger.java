/******************************************************************************
$Id : ResultEventLogger.java 3/3/2016 6:00:46 PM
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import cbf.engine.TestResult;
import cbf.engine.TestCaseRunner.TCMaker;
import cbf.engine.TestResult.ResultType;
import cbf.harness.ResourcePaths;
import cbf.model.ResultReporter;
import cbf.model.TestCase;
import cbf.model.TestIteration;
import cbf.model.TestStep;
import cbf.utils.DataRow;
import cbf.utils.FileUtils;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbf.utils.Utils;

/**
 * 
 * Implementing ResultReporter and logs the result events in log file which will
 * be used in analysing the results and coverage
 * 
 */
public class ResultEventLogger implements ResultReporter {
	public File targetFile;
	public int count = 0;
	public Integer id, testCaseID, iterationID, testStepID, componentID;

	/**
	 * Constructor to initialize parameter log file path
	 * 
	 * @param params
	 *            map containing parameters
	 */
	public ResultEventLogger(Map params) {
		filePath = (String) params.get("filepath");
		if (filePath.equals("")) {
			filePath = ResourcePaths.getInstance().getRunResource(
					"Events.xml", "");
		}
	}

	/**
	 * Closes the result log file
	 */
	public void close() {
		// FIXME : should not be hard coded

	}

	/**
	 * Reporter open method which creates new file for event log if not present
	 * in log folder, if already present just appends the execution results to
	 * log file
	 * 
	 * @param headers
	 *            contains header info, like run name, config details etc
	 */
	public void open(Map headers) {

		logger.trace("Report: open");
		targetFile = new File(filePath);
		boolean isExists = targetFile.exists();
		if (!isExists) {
			try {
				targetFile.createNewFile();
			} catch (Exception e) { // FIXME: specific exception
				logger.handleError(
						"Error in making a new report file from template",
						targetFile, e);
			}
		}
		writeHeaders(headers);
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

		report("FINISH", result, details);
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
	 * Reports entity execution start details
	 * 
	 * @param result
	 *            entity object
	 */
	public void start(TestResult result) {

		report("START", result, result.entityDetails);
	}

	/**
	 * Writes headers to event log file
	 * 
	 * @param headers
	 *            contains header info, like run name, config details etc
	 */
	public void writeHeaders(Map headers) {
		Object[] temp = { "'Entity'", "'Configuration'" };
		Map<Object, Object> obj = Utils.toMap(temp);
		obj.put("'ExecutionDate'",
				"'" + String.valueOf((new Date()).getTime() / 1000) + "'");
		Iterator<Entry<Object, Object>> it = headers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String name = (String) entry.getKey();
			String value = entry.getValue().toString();
			if (value.equals(""))
				value = "<Empty>";
			obj.put("'" + name + "'", "'" + value + "'");
		}
		write("<log type='setting'>");
		write("<![CDATA[");
		String result = obj.toString();
		result = result.replace("=", ":");
		result = result.replace(":{", ":\n{");
		result = result.replace(", ", ",\n");
		write(result);

		write("]]>");
		write("</log>");
		write("\n");
	}

	// TODO: Create properties object with an empty hash map and use for
	// assigning ID to different entityType.

	private Object entityReferences(TestResult result, String eventType) {

		String IDKEY = "'id'";
		if (result.entityType.toString().equals("TestCase")) {
			if (eventType.equals("START")) {
				Random rand = new Random();
				int random = rand.nextInt(1000) + 1;
				int randID = Integer.parseInt(new SimpleDateFormat("HHmmss")
						.format(new Date()));
				testCaseID = Integer.parseInt(Integer.toString(random)
						+ Integer.toString(randID));
				result.index = testCaseID;
				id = testCaseID;
			} else {
				id = testCaseID;
			}
		}
		if (result.entityType.toString().equals("Iteration")) {
			if (eventType.equals("START")) {
				Random rand = new Random();
				int random = rand.nextInt(1000) + 1;
				int randID = Integer.parseInt(new SimpleDateFormat("HHmmss")
						.format(new Date()));
				iterationID = Integer.parseInt(Integer.toString(random)
						+ Integer.toString(randID));
				result.index = iterationID;
				id = iterationID;
			} else {
				id = iterationID;
			}
		}
		if (result.entityType.toString().equals("TestStep")) {
			if (eventType.equals("START")) {
				Random rand = new Random();
				int random = rand.nextInt(1000) + 1;
				int randID = Integer.parseInt(new SimpleDateFormat("HHmmss")
						.format(new Date()));
				testStepID = Integer.parseInt(Integer.toString(random)
						+ Integer.toString(randID));
				result.index = testStepID;
				id = testStepID;
			} else {
				id = testStepID;
			}
		}
		if (result.entityType.toString().equals("Component")) {
			if (eventType.equals("START")) {
				Random rand = new Random();
				int random = rand.nextInt(1000) + 1;
				int randID = Integer.parseInt(new SimpleDateFormat("HHmmss")
						.format(new Date()));
				componentID = Integer.parseInt(Integer.toString(random)
						+ Integer.toString(randID));
				result.index = componentID;
				id = componentID;
			} else {
				id = componentID;
			}
		}

		Map<Object, Object> dict;
		Object[] temp = { IDKEY, "'" + id + "'", "'entityType'",
				"'" + result.entityType + "'", "'entityName'",
				"'" + result.entityName + "'" };
		dict = Utils.toMap(temp);

		if (result.parent != null) {
			dict.put("'parentId'", "'" + result.parent.index + "'");
		}

		if (eventType.equals("DETAILS") || eventType.equals("FINISH")) {
			dict.remove("'entityType'");
			dict.remove("'parentId'");
			dict.remove("'entityName'");
		}

		switch (result.entityType) {
		case TESTCASE:
			dict.put("'entityDetails'", testcaseDtls2Map(result.entityDetails));
			break;
		case ITERATION:
			dict.put("'entityDetails'", iteration2Map(result.entityDetails));
			break;
		case TESTSTEP:
			dict.put("'entityDetails'", step2Map(result.entityDetails));
			break;

		}
		return dict;
	}

	private void report(String eventType, TestResult result, Object eventData) {

		logger.trace("Report:" + eventType + ":" + StringUtils.toString(result)
				+ ":" + StringUtils.toString(eventData));

		try {
			switch (result.entityType) {
			case TESTCASE:
				LogEvt(eventType, result, eventData);
				if (eventType.equals("FINISH")) {
					close();

					String targetFile = ResourcePaths.getInstance()
							.getRunResource("ResultsViewer.lnk", "");

					String templateFile = ResourcePaths.getInstance()
							.getFrameworkResource("Misc_ResultsViewer",
									"ResultsViewer.hta");
					createShortCut(templateFile, targetFile);// .substring(0,
																// targetFile.length()-1));
				}
				break;
			case ITERATION:
				if (eventType.equals("START")) {
					String tcName = result.parent.entityName, itName;
					TestIteration testIt = (TestIteration) result.entityDetails;

					if (testIt.parameters() == null)
						itName = "1";
					else if (testIt.parameters().size() == 0
							|| testIt.parameters().values() == null) {
						itName = "1";
					} else {
						itName = testIt.parameters().get("name").toString()
								.replace("Itr", "");
					}
					int itCount = ((TestCase) result.parent.entityDetails)
							.iterationCount();
					if (itCount > 1)
						result.entityName = " Iteration:(" + itName + " of "
								+ itCount + ")";
					else {
						result.entityName = " Iteration:" + itName;
					}
					logComponents(result);
				}

				LogEvt(eventType, result, eventData);

				break;
			case TESTSTEP:
				LogEvt(eventType, result, eventData);
				break;
			case COMPONENT:
				LogEvt(eventType, result, eventData);
				break;
			}
		} catch (Exception e) {
			logger.handleError("Error in Result Event Logger ", e);

		}
	}

	private void LogEvt(String eventType, TestResult result, Object eventData) {
		Map<Object, Object> execData;

		execData = Evt2Map(eventType, result, eventData);

		write("<log type='event'>");
		write("<![CDATA[");
		String temp = execData.toString();
		temp = temp.replace("=", ":");
		temp = temp.replace(":{", ":\n{");
		temp = temp.replace(", ", ",\n");
		write(temp);

		write("]]>");
		write("</log>");
		write("\n");

	}

	private Map<Object, Object> Evt2Map(String eventType, TestResult result,
			Object eventData) {
		Map<Object, Object> reslt;

		Object[] temp = { "'eventType'", "'" + eventType + "'", "'entity'",
				entityReferences(result, eventType) };
		reslt = Utils.toMap(temp);

		if (eventType.equals("FINISH")) {
			reslt.put("'childcount'", "'" + result.childCount + "'");
			reslt.put("'passedCount'", "'" + result.value + "'");
			reslt.put("'startTime'", "'" + unixTime(result.startTime) + "'");
			reslt.put("'finishTime'", "'" + unixTime(result.finishTime) + "'");
		}

		if (!eventData.getClass().toString().contains("serializer")) {
			if (eventData.getClass().toString().contains("ResultType"))
				reslt.put("'eventData'", "{'ResultType':'" + result.msRsType
						+ "'}");
			else if (eventData.getClass().toString().contains("HashMap")) {

				HashMap m = (HashMap) eventData;
				Object[] t = { "delete", "delete" };
				Map<Object, Object> mtemp = Utils.toMap(t);
				Iterator<Entry<Object, Object>> it = m.entrySet().iterator();

				if (eventType.equals("DETAILS")) {
					mtemp.put("'Details'", detaildata(eventData));
					mtemp.put("'rsType'", "'" + result.msRsType + "'");
					if (screendump) {
						mtemp.put(
								"'screenDumpName'",
								"'"
										+ (String) ((Map) result.miscInfo
												.get("screenDump")).get("name")
										+ "'");
					} else {
						mtemp.put("'screenDumpName'", "'<Empty>'");
					}
				} else {
					while (it.hasNext()) {
						Entry<Object, Object> entry = it.next();
						String name = (String) entry.getKey();
						String value = entry.getValue().toString();
						mtemp.put("'" + name + "'", "'" + value + "'");
					}

				}
				mtemp.remove("delete");
				reslt.put("'eventData'", mtemp);
			} else
				reslt.put("'eventData'", "'<Empty>'");
		} else
			reslt.put("'eventData'", "'<Empty>'");
		return reslt;
	}

	private Map detaildata(Object eventData) {
		HashMap m = (HashMap) eventData;
		Object[] t = { "delete", "delete" };
		Map<Object, Object> mtemp = Utils.toMap(t);
		Iterator<Entry<Object, Object>> it = m.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String name = (String) entry.getKey();
			String value = entry.getValue().toString();
			mtemp.put("'" + name + "'", "'" + value + "'");
		}
		mtemp.remove("delete");
		screendump = false;
		if (!mtemp.get("'name'").equals("''")
				&& mtemp.get("'screenDump'").equals("'true'")) {
			screendump = true;
		}
		return mtemp;
	}

	private Map testcaseDtls2Map(Object entityDetails) {
		try {
			TestCase tc = (TestCase) entityDetails;
			Object[] temp = { "'iterationCount'",
					"'" + tc.iterationCount() + "'" };
			Map<Object, Object> obj = Utils.toMap(temp);
			obj.put("'Name'", "'" + tc.name() + "'");
			return obj;
		} catch (Exception e) {
			TCMaker tcm = (TCMaker) entityDetails;
			TestCase tc;
			Map<Object, Object> obj;
			try {

				tc = tcm.make();
				Object[] temp = { "'iterationCount'",
						"'" + tc.iterationCount() + "'" };
				obj = Utils.toMap(temp);
				obj.put("'Name'", "'" + tc.name() + "'");
			} catch (Exception e1) {
				Object[] temp = { "'Name'", "'" + "<empty>" + "'" };
				obj = Utils.toMap(temp);
			}
			return obj;
		}
	}

	private Map iteration2Map(Object entityDetails) {
		TestIteration test = (TestIteration) entityDetails;
		Map<Object, Object> obj = test.parameters();
		Object[] t = { "delete", "delete" };
		Map<Object, Object> temp = Utils.toMap(t);
		if (obj != null) {
			Iterator<Entry<Object, Object>> it = obj.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Object, Object> entry = it.next();
				String name = (String) entry.getKey();
				String value = (String) entry.getValue();
				temp.put("'" + name + "'", "'" + value + "'");
			}
		}
		temp.remove("delete");
		return temp;

	}

	private Map step2Map(Object entityDetails) {
		TestStep ts = (TestStep) entityDetails;
		Object[] temp = { "'stepName'", "'" + ts.stepName() + "'" };
		Map<Object, Object> obj = Utils.toMap(temp);
		obj.put("'moduleCode'", "'" + ts.moduleCode() + "'");
		obj.put("'componentCode'", "'" + ts.componentCode() + "'");
		obj.put("'failTestIfUnexpected'", "'" + ts.failTestIfUnexpected() + "'");
		obj.put("'abortTestIfUnexpected'", "'" + ts.abortTestIfUnexpected()
				+ "'");

		DataRow im = ts.componentParameters();
		String AP = im.toString();

		if (AP.length() != 2) {
			AP = AP.replace("=", "':'");
			AP = AP.replace("; ;", "");
			AP = AP.replace(";}", "}");
			AP = AP.replace(";", "','");
			AP = AP.replace("{", "{'");
			AP = AP.replace("}", "'}");
			obj.put("'componentParameters'", AP);
		}
		obj.put("'componentOutputValidation'",
				"'" + ts.componentOutputValidation() + "'");

		return obj;
	}

	private void write(String text) {
		PrintWriter pw = null;
		try {

			FileWriter fw = new FileWriter(targetFile, true);
			pw = new PrintWriter(fw);
			pw.println(text);
		} catch (IOException e) {

		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private String unixTime(Date time) {
		long unixTime;
		unixTime = (long) time.getTime() / 1000;
		return String.valueOf(unixTime);
	}

	private void createShortCut(String sourcePath, String targetPath)
			throws IOException, InterruptedException {

		if (FileUtils.fileExists(sourcePath)) {
			String script = "Set sh = CreateObject(\"WScript.Shell\")"
					+ "\nSet shortcut = sh.CreateShortcut(\"" + targetPath
					+ "\")" + "\nshortcut.TargetPath = \"" + sourcePath + "\""
					+ "\nshortcut.Save";
			String tempFile = targetPath.substring(0,
					targetPath.lastIndexOf("/"))
					+ "/temp.vbs";
			File file = new File(tempFile);
			FileOutputStream fo = new FileOutputStream(file);
			fo.write(script.getBytes());
			fo.close();
			Runtime.getRuntime().exec("wscript.exe " + file.getAbsolutePath());
			Thread.sleep(2000);
			file.delete();

		} else {
			System.out.println("File does not exist.");
		}

	}

	private void replaceData(Map finalData) throws IOException {
		int cnt = 0;
		String content = IOUtils.toString(new FileInputStream(filePath),
				"UTF-8");
		Iterator<Entry<Object, Object>> it = finalData.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			String name = (String) entry.getKey();
			String value = entry.getValue().toString();
			content = content.replaceAll(name, value);
		}
		IOUtils.write(content, new FileOutputStream(filePath), "UTF-8");
	}

	private void logComponents(TestResult result) {

		Map<Object, Object> nobj = new HashMap<Object, Object>();
		Object steps = result.entityDetails;
		TestIteration obj = (TestIteration) steps;
		int n = obj.stepCount();
		ArrayList<TestStep> arr = new ArrayList<TestStep>();
		ArrayList<TestStep> occurrence = new ArrayList<TestStep>();

		for (int i = 0; i < n; i++) {
			arr.add(obj.step(i));
		}

		Object[] oj = arr.toArray();
		String Str = "[", module = null;

		for (int j = 0; j < oj.length; j++) {
			Object o = oj[j];
			TestStep ts = (TestStep) o;
			nobj.put("'stepName'", "'" + ts.stepName() + "'");
			nobj.put("'moduleCode'", "'" + ts.moduleCode() + "'");
			nobj.put("'componentCode'", "'" + ts.componentCode() + "'");
			nobj.put("'occurrences'", occurrence);
			module = ts.moduleCode();
			Str = Str + "," + nobj.toString();
		}
		String Strcomplete = Str + "]";
		Strcomplete.replace("[,", "[");
		Map<Object, String> m = new HashMap<Object, String>();
		m.put("'" + module + "'", Strcomplete);
		write("<log type='components'>");
		write("<![CDATA[");
		String final1 = m.toString();
		final1 = final1.replace("=", ":");
		final1 = final1.replace("},", "}\n,");
		final1 = final1.replace("[,", "[");
		final1 = final1.replace(", ", ",\n");
		final1 = final1.replace("'}", "'\n}");
		final1 = final1.replace("}]", "}\n]");
		final1 = final1.replace("]}", "]\n}");
		write(final1);
		write("]]>");
		write("</log>");
	}

	/**
	 * Overrides toString() method of Object class to return ResultEventLogger
	 * format string
	 */
	public String toString() {
		return StringUtils.mapString(this, filePath);
	}

	private String filePath;
	private LogUtils logger = new LogUtils(this);
	private boolean screendump = false;

}
