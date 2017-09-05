/******************************************************************************
$Id : ResultReporter.java 3/3/2016 6:00:30 PM
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
import cbf.engine.TestResultTracker;

/**
 * 
 * Extends the reporter with open and close methods Opens report Logs execution
 * events with details Closes the report
 * 
 */
public interface ResultReporter extends TestResultTracker.Observer {
	/**
	 * Opens the report file and updates the headers as required
	 * 
	 * @param headers
	 *            run details to be updated in report
	 * 
	 */
	public void open(Map headers);

	/**
	 * Closes the report file
	 */
	public void close();
}
