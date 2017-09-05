/******************************************************************************
$Id : BridgeTestCase.java 3/3/2016 6:01:37 PM
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

package cbfx.testng;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import cbf.engine.TestResult;
import cbf.engine.TestResult.ResultType;
import cbf.engine.TestResultTracker;
import cbf.harness.Harness;
import cbf.harness.TestSetRunner;

public class BridgeTestCase {
	@BeforeSuite
	// @BeforeTest
	@Parameters({ "configFilePath", "browser" })
	public void startCbf(String configFilePath, String browser) {
		Map<String, String> runMap = new HashMap<String, String>();
		runMap.put("configFilePath", configFilePath);
		runMap.put("browser", browser);

		harness = new Harness(runMap, "ExcelTestSet");
		harness.addTracker(testNgReporter);

		runner = new TestSetRunner(harness);
	}

	@Test
	@Parameters({ "tc_name", "folderPath" })
	private void runCBFTest(String tc_name, String folderPath) {
		// TODO: Remove hard-coding of instanceName here
		Map<String, String> instanceMap = new HashMap<String, String>();
		TestResult result = null;
		instanceMap.put("instanceName", tc_name);
		instanceMap.put("folderPath", folderPath);

		try {
			result = runner.runTestInstance(instanceMap);
		} catch (Exception e) {
			Assert.fail("Error: " + e);

		}
		if (!result.finalRsType.isPassed())
			Assert.fail();
	}

	@AfterSuite
	public void stopCbf() {
		harness.finalize();
	}

	private TestResultTracker.Observer testNgReporter = new TestResultTracker.Observer() {
		private final String BR = "<br/>";

		public void start(TestResult result) {
			switch (result.entityType) {
			case TESTCASE:
				Reporter.log("Start:TestCase(" + result.entityName + ")" + BR);
				break;
			case COMPONENT:
				Reporter.log("Start:Component( " + result.entityName + ")" + BR);
				break;
			}
		}

		// @TODO Fix .css of the report. Add style if possible
		public void log(TestResult result, ResultType rsType, Map details) {
			Reporter.log("<span >" + details.get("expected") + "<BR>"
					+ details.get("actual") + "</span>" + BR);
		}

		public void finish(TestResult result, ResultType rsType, Object details) {
			switch (result.entityType) {
			case TESTCASE:
				Reporter.log("<span>" + "Finish:TestCase(" + result.entityName
						+ ")" + "</span>" + ":" + cssClass(rsType) + BR);
				break;
			case COMPONENT:
				Reporter.log("<b>Status:</b>" + cssClass(rsType) + BR
						+ "Finish:Component(" + result.entityName + ")" + BR);
				break;
			}
		}

		// Css class=passed/error/failed/warning/done for appropriate decoration
		private String cssClass(ResultType rsType) {
			String str = null;
			switch (rsType) {
			case DONE:
				str = "<font color='black'>" + rsType + "</font>";
				break;
			case PASSED:
				str = "<font color='green'>" + rsType + "</font>";
				break;

			case FAILED:
				str = "<font color='red'>" + rsType + "</font>";
				break;
			case ERROR:
				str = "<font color='brown'>" + rsType + "</font>";
				break;

			case WARNING:
				str = "<font color='orange'>" + rsType + "</font>";
				break;

			}

			return str;
		}
	};

	private static TestSetRunner runner;
	private Harness harness;
}
