/******************************************************************************
$Id : TestSetRunner.java 3/3/2016 6:00:22 PM
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

package cbf.harness;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import cbf.engine.TestResult;
import cbf.engine.TestCaseRunner.TCMaker;
import cbf.model.TestCase;
import cbf.model.TestCaseAccess;
import cbf.model.TestSet;
import cbf.plugin.PluginManager;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

public class TestSetRunner {

	/**
	 * Run the test set from parameters
	 * 
	 * @param runMap
	 *            command line arguments
	 * @param runName
	 * 
	 */
	public static void run(Map<String, String> runMap, String runName) {
		Harness harness = new Harness(runMap, runName);

		TestSetRunner runner = new TestSetRunner(harness);

		runner.runTestSet();

		harness.finalize();
	}

	/**
	 * Convenience method
	 */
	public static void run(Map<String, String> runMap) {
		run(runMap, "Dummy");
	}

	public TestSetRunner(Harness harness) {
		this.harness = harness;
	}

	/**
	 * Executes each instance from the test set
	 */
	public void runTestSet() {
		TestSet ts = TestSetAccess.instantiate();

		int tsCount = ts.testInstanceCount();
		for (int ix = 0; ix < tsCount; ix++) {
			if (ix > 0) { // delay between 2 test cases
				delayTestCase();
			}

			Map<String, String> instanceMap = new HashMap<String, String>();

			instanceMap.put("folderPath", ts.testInstance(ix).folderPath());
			instanceMap.put("instanceName", ts.testInstance(ix).instanceName());

			try {
				TestResult result = runTestInstance(instanceMap);
			} catch (Exception e) {
				logger.handleError(
						"Unknown error during testinstance execution ", e, " ",
						ix, " ", instanceMap);
			}
		}
	}

	/**
	 * Triggers execution for the current instance(runName)
	 * 
	 * @param instanceMap
	 *            name of instance
	 * @throws ReflectiveCopyException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public TestResult runTestInstance(final Map<String, String> instanceMap) {
		final String instanceName = instanceMap.get("instanceName");

		TCMaker tcMaker = new TCMaker() {
			public TestCase make() throws Exception {
				return getTestCase(instanceMap);
			}

			public String toString() {
				return instanceName;
			}
		};
		return harness.runTest(tcMaker, instanceName);
	}

	private TestCase getTestCase(Map<String, String> instanceMap) {
		logger.trace("GetTestCase(), instanceMap");
		try {
			return getTestCaseAccess().getTestCase(instanceMap);
		} catch (ClassCastException e) {
			logger.handleError("Error in building test case from ",
					instanceMap, e);
		}
		return null;
	}

	private void delayTestCase() {
		int interTcDelay;
		interTcDelay = Integer.parseInt((String) Harness.GCONFIG
				.get("InterTestCaseDelay"));
		if (interTcDelay != 0) {
			try {
				Thread.sleep(interTcDelay * 1000);
				logger.trace("Test case delay:" + interTcDelay);
			} catch (InterruptedException e) {
				logger.handleError("Exception caught in delay test case : ", e,
						interTcDelay);
			}
		}
	}

	private TestCaseAccess getTestCaseAccess() {
		if (testCaseAccess != null)
			return testCaseAccess;
		Map<String, Object> pluginParams = null;
		try {
			pluginParams = (Map<String, Object>) Harness.GCONFIG
					.get("TestCaseAccess");
			if (pluginParams == null) {
				logger.handleError("TestCaseAccess is not configured; cannot build test case");
			}
			testCaseAccess = (TestCaseAccess) PluginManager
					.getPlugin(pluginParams);
		} catch (ClassCastException e) {
			logger.handleError("'TestCaseAccess' is not configured correctly",
					pluginParams, e);
		}

		return testCaseAccess;
	}

	/**
	 * Returns TestSetRunner format string
	 */
	public String toString() {
		return StringUtils.mapString(this, harness);
	}

	/* @CHECKME: check if harness can be privatized */
	public final Harness harness;

	private TestCaseAccess testCaseAccess = null;
	private LogUtils logger = new LogUtils(this);
}
