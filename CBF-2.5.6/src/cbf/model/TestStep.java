/******************************************************************************
$Id : TestStep.java 3/3/2016 6:00:31 PM
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
 * Defines the structure of the TestStep
 * 
 */
public interface TestStep {
	/**
	 * Returns TestStep name
	 * 
	 * @return name of TestStep
	 */
	public String stepName();

	/**
	 * Returns moduleCode of TestStep
	 * 
	 * @return TestStep moduleCode
	 */
	public String moduleCode();

	/**
	 * Returns componentCode of TestStep
	 * 
	 * @return TestStep componentCode
	 */
	public String componentCode();

	/**
	 * Returns component parameters in form of DataRow object
	 * 
	 * @return object of DataRow
	 */
	public DataRow componentParameters();

	/**
	 * Returns value of OutputValidation field
	 * 
	 * @return OutputValidation value
	 */
	public String componentOutputValidation();

	/**
	 * Returns value of failTestIfUnexpected field
	 * 
	 * @return failTestIfUnexpected value
	 */
	public boolean failTestIfUnexpected();

	/**
	 * Returns value of abortTestIfUnexpected field
	 * 
	 * @return abortTestIfUnexpected value
	 */
	public boolean abortTestIfUnexpected();
}
