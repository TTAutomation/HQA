/******************************************************************************
$Id : Engine.java 3/3/2016 6:00:13 PM
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

import cbf.engine.TestCaseRunner.TCMaker;
import cbf.model.AppDriver;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

public class Engine {
	AppDriver appDriver;
	TestResultTracker resultTracker;
	TestResultLogger RESULT;
	LogUtils logger;

	// Need clarification on Result Reporter object
	public Engine(String runName, AppDriver appDriver,
			TestResultTracker resultTracker) {
		this.appDriver = appDriver;
		this.resultTracker = resultTracker;
		this.RESULT = TestResultLogger.getInstance(this.resultTracker);
		appDriver.initialize();
	}

	public TestResult runTestCase(TCMaker tcMaker, String TCName) {
		TestCaseRunner oTestCaseRunner = new TestCaseRunner(appDriver,
				resultTracker);
		return oTestCaseRunner.runTestCase(tcMaker, TCName);
	}

	/**
	 * Returns Engine format string
	 */
	public String toString() {
		return StringUtils.mapString(this, appDriver, resultTracker, RESULT);
	}
}
