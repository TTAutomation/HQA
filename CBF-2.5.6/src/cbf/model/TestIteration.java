/******************************************************************************
$Id : TestIteration.java 3/3/2016 6:00:31 PM
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
 * Defines the structure of iteration
 * 
 */
public interface TestIteration {

	/**
	 * Returns number of steps present in the current iteration
	 * 
	 * @return number of steps
	 */
	public int stepCount();

	/**
	 * Returns step with the given index from step array
	 * 
	 * @param stepIx
	 *            index of step
	 * @return TestStep object with step and component details
	 */
	public TestStep step(int stepIx);

	/**
	 * Returns iter parameters read from the iteration sheet
	 * 
	 * @return Map of parameters 
	 */
	public Map parameters();
}
