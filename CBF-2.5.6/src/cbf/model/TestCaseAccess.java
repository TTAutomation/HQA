/******************************************************************************
$Id : TestCaseAccess.java 3/3/2016 6:00:30 PM
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

import java.util.Map;

/**
 * 
 * Interface for accessing TestCase
 * 
 */
public interface TestCaseAccess {

	/**
	 * Returns TestCase object
	 * 
	 * @param info
	 *            Map of TestCase info like testcase name, test folder path(in
	 *            case of designer tc access)
	 * @return TestCase object
	 */
	public TestCase getTestCase(Map info);
}
