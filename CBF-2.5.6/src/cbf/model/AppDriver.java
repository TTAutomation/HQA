/******************************************************************************
$Id : AppDriver.java 3/3/2016 6:00:30 PM
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


package cbf.model;

import cbf.utils.DataRow;

/**
 * 
 * Initializes report logger and contains a method recover for implementing
 * recovery steps in case of abrupt interruption in execution.
 */
public interface AppDriver {

	public void initialize();

	/**
	 * Invokes specific function related to provided component code for
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
			DataRow output);

	/**
	 * Recovers from abrupt execution interruption
	 */
	public void recover();
}
