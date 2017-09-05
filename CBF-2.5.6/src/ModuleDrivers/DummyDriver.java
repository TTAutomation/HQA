/******************************************************************************
$Id : IConnectDriver.java 9/8/2014 1:21:28 PM
Copyright 2014-2016 IGATE GROUP OF COMPANIES. All rights reserved
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

package ModuleDrivers;

import static cbf.engine.TestResultLogger.failed;
import static cbf.engine.TestResultLogger.passed;

import java.io.IOException;

import cbf.utils.DataRow;
import cbfx.BaseWebDriver.BaseWebModuleDriver;

/**
 * 
 * Extends BaseModuleDriver class and performs application specific operations
 * 
 */
public class DummyDriver extends BaseWebModuleDriver {

	// Launching the iConnect Application.
	/**
	 * Launches Application
	 * 
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during
	 *            execution of component
	 * @throws IOException
	 */
	public void launchApp(DataRow input, DataRow output) {
		uiDriver.launchApplication(input.get("url"));
		output.put("userId", "1234");
		if (true) {
			passed("Launching the Application" + input.get("url"),
					"Should open the Application",
					"Application opened sucessfully!");
		} else {
			failed("Launching the Application" + input.get("url"),
					"Should open the Application",
					"Error in opening the Application");
		}
	}

	// Log-In in the iConnect-Application.
	/**
	 * Performs Log-In
	 * 
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during
	 *            execution of component
	 * @throws InterruptedException
	 * @throws FindFailed
	 */
	public void login(DataRow input, DataRow output)
			throws InterruptedException {

		if (true) {
			passed("Login with " + input.get("userId"),
					"Should be Logged in Successfully",
					"Logged in successfully");
		} else {

			failed("Login", "Should be Logged in Successfully",
					"Failed to Log in");
		}
	}

	// Logging out from the Application.
	/**
	 * Performs Log-Out
	 * 
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during
	 *            execution of component
	 */
	public void logout(DataRow input, DataRow output) {

		if (true) {
			passed("Logging out from the Application", "Should log out",
					"Logged out sucessfully!");
		} else {
			failed("Logging out from the Application", "Should log out",
					"Failed to log out");
		}
	}

	/**
	 * Performs Search operation
	 * 
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during
	 *            execution of component
	 */
	public void search(DataRow input, DataRow output)
			throws InterruptedException {
		String value=input.get("fruits");
		System.out.println(value);
		if (true) {
			passed("search " + input.get("fruits"),
					"Should search Successfully", "Searched successfully");
		} else {

			failed("Search", "Should search Successfully", "Failed to search");
		}
	}

	public void navigate(DataRow input, DataRow output)
			throws InterruptedException {
		if (true) {
			passed("Navigate to " + input.get("value"),
					"Should navigate Successfully", "Navigated successfully");
		} else {

			failed("Navigate", "Should navigate Successfully",
					"Failed to navigate");
		}
	}

	public void select(DataRow input, DataRow output)
			throws InterruptedException {
		if (true) {
			passed("select " + input.get("fruits"),
					"Should select Successfully", "Selected successfully");
		} else {

			failed("Select", "Should select Successfully", "Failed to select");
		}
	}

	/**
	 * Overriding toString() method of object class to print IConnectDriver
	 * format string
	 */
	public String toString() {
		return "DummyDriver()";
	}
}
