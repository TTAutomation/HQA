/******************************************************************************
$Id : BaseAppDriver.java 3/3/2016 6:00:13 PM
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

import java.util.Map;

import cbf.model.AppDriver;
import cbf.model.ModuleDriver;
import cbf.utils.DataRow;
import cbf.utils.LogUtils;

/**
 * 
 * Implements application driver methods
 * 
 */
abstract public class BaseAppDriver implements AppDriver {
	public void initialize() {
		moduleDrivers = loadModuleDrivers();
	}

	/**
	 * Invokes function mapped with component code for
	 * execution
	 * 
	 * @param moduleCode
	 *            module code for the input component
	 * @param componentCode
	 *            code for the component under execution
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during
	 *            execution of component
	 */
	public void perform(String moduleCode, String componentCode, DataRow input,
			DataRow output) {

		ModuleDriver driver = moduleDrivers.get(moduleCode);
		if (driver == null) {
			logger.handleError("Cannot find driver for module", moduleCode);
			return;
		}
		driver.perform(componentCode, input, output);
	}

	/**
	 * Recovers from abrupt execution interruption
	 */
	public void recover() {
	}

	abstract public Map<String, ModuleDriver> loadModuleDrivers();

	private Map<String, ModuleDriver> moduleDrivers;
	protected LogUtils logger = new LogUtils(this);
}
