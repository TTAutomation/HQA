/******************************************************************************
$Id : ExcelAccess.java 3/3/2016 6:00:53 PM
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

package cbf.testAccess;

import java.io.FileNotFoundException;
import java.util.Map;

//import org.python.antlr.PythonParser.return_stmt_return;

import cbf.harness.Harness;
import cbf.harness.ResourcePaths;
import cbf.model.DataAccess;
import cbf.model.TestCase;
import cbf.model.TestCaseAccess;
import cbf.plugin.PluginManager;
import cbf.utils.FileUtils;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

/**
 * 
 * Implements TestCaseAccess interface and makes TestCase from test file
 * 
 */
public class ExcelAccess implements TestCaseAccess {

	/**
	 * Constructor for initializing resource paths and data table access
	 * 
	 * @param params
	 *            map containing parameters
	 */
	public ExcelAccess(Map params) {
		this.params = params;
	}

	/**
	 * Deserializes test file, resolves references and returns TestCase object
	 * 
	 * @param info
	 *            Map having info related to TestCase like test name
	 * @return object of TestCase
	 */
	public TestCase getTestCase(Map info) {
		logger.trace("getTestCase()");
		testName = (String) info.get("instanceName");
		String testFile = (String) params.get("folderpath");
		if (testFile.equals("")) {
			testFile = ResourcePaths.getInstance().getSuiteResource(
					"Plan/TestCases", testName + ".xls");
			if ((testFile == null) || !(FileUtils.fileExists(testFile))) {
				logger.trace("Test Case file " + testFile + " does not exists. ",
						testName, testFile);
				testFile = null;
			}
		}
		if (testFile == null) {
			testFile = ResourcePaths.getInstance().getSuiteResource(
					"Plan/TestCases", testName + ".xlsx");
			if ((testFile == null) || !(FileUtils.fileExists(testFile))) {
				logger.trace("Test Case file " + testFile + " does not exists. ",
						testName, testFile);
				testFile = null;
			}
		}
		if (testFile == null) {
			logger.handleError("Deserializing test step file:" + testFile
					+ ": can not find it");
		}
		TestCase oTestCase = null;
		try {
			oTestCase = deserializeTest(testName, testFile);
		} catch (Exception e) {
			logger.handleError("Deserializing test step file:", testFile, e);
		}

		return oTestCase;
	}

	private TestCase deserializeTest(String testName, String serializedFileName) {

		logger.trace("DeserializeTest(" + testName + ";" + serializedFileName
				+ ")");
		try {
			if (testName == "") {
				logger.handleError("Testcase name is not provided in testset file ",testName, serializedFileName);
				return null;
			} else {
				return ExcelDeserializer.deserialize(getDataAccess(), testName,
						serializedFileName, params);
			}
		} catch (FileNotFoundException e) {
			logger.handleError("File doesn't exists ", serializedFileName, e);
			return null;
		}

	}

	private DataAccess getDataAccess() {
		Map<String, Object> dataAccessMap = null;
		try {
			dataAccessMap = (Map<String, Object>) Harness.GCONFIG
					.get("DataAccess");
			if (dataAccessMap.isEmpty())
				logger.handleError("DataAccess is not configured");

			return (DataAccess) PluginManager.getPlugin(dataAccessMap);
		} catch (ClassCastException e) {
			logger.handleError("'DataAccess' is configured incorrectly",
					dataAccessMap, e);
			return null;
		}
	}

	/**
	 * Returns ExcelAccess format string
	 */
	public String toString() {
		return StringUtils.mapString(this, params);
	}

	private LogUtils logger = new LogUtils(this);
	private Map params;
	private String testName;
}
