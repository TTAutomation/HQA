/******************************************************************************
$Id : ExcelTestSetAccess.java 3/3/2016 6:00:53 PM
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cbf.harness.ResourcePaths;
import cbf.model.TestCase;
import cbf.model.TestInstance;
import cbf.model.TestIteration;
import cbf.model.TestSet;
import cbf.utils.DTAccess;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbf.utils.Utils;

import java.io.IOException;

/**
 * 
 * Implements TestSet interface and makes TestInstance
 * 
 */
public class ExcelTestSetAccess implements TestSet {

	private static Map<Integer, ArrayList<String>> testInstanceInfo = new HashMap<Integer, ArrayList<String>>();

	/**
	 * Constructor which reads the TestSet sheet and makes a list map for
	 * instances as per the user selection
	 * 
	 * @param params
	 */
	public ExcelTestSetAccess(Map params) {
		this.params = params;
		String testCaseName;
		String folderPath;
		List<Map> testInstances = null;

		DTAccess dtAccess = new DTAccess((String) ResourcePaths.getInstance()
				.getSuiteResource("Lab", (String) params.get("testSetFile")));
		try{
			testInstances = dtAccess.readSheet((String) params.get("testSetSheet"));
		}catch(Exception e){
			logger.handleError("Failed to read the test set sheet ", params.get("testSetSheet"), e);
		}
		int index = 0;

		for (int ix = 0; ix < testInstances.size(); ix++) {
			try {
				if (!Utils.string2Bool(((String) testInstances.get(ix).get(
						"SelectYN")))) {
					continue;
				}
				testCaseName = (String) testInstances.get(ix).get(
						"TestCase Name");
				folderPath = (String) testInstances.get(ix).get("Folder Path");
				ArrayList<String> instanceList = new ArrayList<String>();
				instanceList.add(testCaseName);
				instanceList.add(folderPath);
				testInstanceInfo.put(index, instanceList);
				index++;

			} catch (Exception e) {
				logger.handleError("No input value is provided for SelectYN column");
			}
		}
	}

	/**
	 * Returns test instance object specified at the given index in instance
	 * array
	 * 
	 * @param ix
	 *            index of TestInstance
	 * @return testInstance
	 */
	public TestInstance testInstance(final int ix) {
		testInstanceInfo.get(ix);
		final ArrayList<String> params = testInstanceInfo.get(ix);
		return new TestInstance() {

			public TestCase testCase() {
				return null;
			}

			public String description() {
				return null;
			}

			public String instanceName() {
				return params.get(0);
			}

			public TestIteration[] iterations() {
				return null;
			}

			public String folderPath() {
				return params.get(1);
			}
		};
	}

	/**
	 * Returns number of TestInstances
	 * 
	 * @return TestInsances count
	 */
	public int testInstanceCount() {
		return testInstanceInfo.size();
	}

	/**
	 * Returns ExcelTestSetAccess format string
	 */
	public String toString() {
		return StringUtils.mapString(this, params);
	}

	private LogUtils logger = new LogUtils(this);
	private Map params;
}
