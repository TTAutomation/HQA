/******************************************************************************
$Id : TestSetAccess.java 3/3/2016 6:00:21 PM
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

import java.util.HashMap;
import java.util.Map;

import cbf.model.TestSet;
import cbf.plugin.PluginManager;
import cbf.utils.LogUtils;

/**
 * 
 * Provides access to testset
 * 
 */
public class TestSetAccess {

	/**
	 * Instantiates TestSet
	 * 
	 * @return TestSet object
	 */
	public static TestSet instantiate() {
		try {
			Map<String, Object> testSetAccessMap = (Map<String, Object>) Harness.GCONFIG
					.get("TestSetAccess");
			Map<String, Object> params = new HashMap<String, Object>();

			if (testSetAccessMap.get("parameters") != null)
				params.putAll((Map<String, Object>) testSetAccessMap
						.get("parameters"));
			return ((TestSet) PluginManager.getPlugin(
					(String) testSetAccessMap.get("plugin"), params));
		} catch (ClassCastException e) {
			new TestSetAccess().logger
					.handleError(
							"Value for 'TestSetAccess' is not proper in user config file ",
							e);
		}
		return null;

	}

	private LogUtils logger = new LogUtils(this);
}
