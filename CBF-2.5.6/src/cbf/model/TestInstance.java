/******************************************************************************
$Id : TestInstance.java 3/3/2016 6:00:30 PM
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

/**
 * 
 * Defines the structure of test instance
 * 
 */
public interface TestInstance {

	/**
	 * Deserializes test file, resolves parameters and returns TestCase object.
	 * 
	 * @return TestCase object in an engine acceptable format
	 */
	public TestCase testCase();

	/**
	 * Returns iterations specified for the testcase
	 * 
	 * @return iterations
	 */
	public TestIteration[] iterations();

	/**
	 * Returns description of TestInstance
	 * 
	 * @return TestInstance's description
	 */
	public String description();

	/**
	 * Returns name of TestInstance
	 * 
	 * @return TestInstance's name
	 */
	public String instanceName();
	
	/**
	 * Returns path of folder
	 * 
	 * @return TestInstance's folder path
	 */
	public String folderPath();
}
