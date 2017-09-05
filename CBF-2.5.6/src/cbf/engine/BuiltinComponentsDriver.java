/******************************************************************************
$Id : BuiltinComponentsDriver.java 3/3/2016 6:00:13 PM
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

import cbf.harness.ParameterAccess;
import cbf.utils.DataRow;
import cbf.utils.LogUtils;
import cbf.utils.SleepUtils;
import cbf.utils.StringUtils;
import cbf.utils.SleepUtils.TimeSlab;

/**
 * 
 * Implements built-in components
 * 
 */
class BuiltinComponentsDriver extends BaseModuleDriver {
	private ParameterAccess paramsAccess;
	LogUtils logger = new LogUtils(this);

	/**
	 * Constructor to initialize ParameterAccess object
	 * 
	 * @param paramsAccess
	 *            object of ParameterAccess
	 */
	public BuiltinComponentsDriver(ParameterAccess paramsAccess) {
		this.paramsAccess = paramsAccess;
	}

	/**
	 * 
	 * Saves output parameters for further use
	 * 
	 */
	public void save(DataRow input, DataRow output) {
		paramsAccess.copyRecentParameters(input.get("inputName"),
				input.get("outputName"));
	}

	/**
	 * 
	 * Provides sleep time slabs
	 * 
	 */
	public void sleep(DataRow input, DataRow output) {
		String level = input.get("level");
		if (level.equals(""))
			logger.handleError("Parameter level must be specified", level);
		TimeSlab sleepLevel = null;
		switch (level.charAt(0)) {
		case 'Y':
			sleepLevel = TimeSlab.YIELD;
			break;
		case 'L':
			sleepLevel = TimeSlab.LOW;
			break;
		case 'M':
			sleepLevel = TimeSlab.MEDIUM;
			break;
		case 'H':
			sleepLevel = TimeSlab.HIGH;
			break;
		default:
			logger.handleError("Invalid level : ", sleepLevel);

		}
		SleepUtils.sleep(sleepLevel);
	}

	public void fail(DataRow input, DataRow output) {
		logger.handleError("Failed with parameters ", input, output);
	}

	/**
	 * Returns BuiltinComponentsDriver format string
	 */
	public String toString() {
		return StringUtils.mapString(this, paramsAccess);
	}

}
