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

import static cbf.engine.TestResultLogger.*;

import java.io.IOException;

//import org.sikuli.script.FindFailed;

import cbf.engine.TestResult.ResultType;
import cbf.utils.DataRow;
import cbf.utils.SleepUtils;
import cbf.utils.SleepUtils.TimeSlab;
import cbfx.BaseWebDriver.BaseWebModuleDriver;

/**
 * 
 * Extends BaseModuleDriver class and performs application specific operations
 * 
 */
public class IConnectDriver extends BaseWebModuleDriver {

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
	public void LaunchApp(DataRow input, DataRow output) {

		uiDriver.launchApplication(input.get("url"));

		output.put("userID", "1234");

		if (uiDriver.checkPage("IConnect : Unified Service Desk")) {
			passed("Launching the Application", "Should open the Application",
					"Application opened sucessfully!");
		} else {
			log("Launching the Application", ResultType.FAILED,
					"Should open the Application",
					"Error in opening the Application", true);
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
			//throws InterruptedException, FindFailed 
	{
		// uiDriver.DrawHighlight("userID", webDriver);
		uiDriver.setValue("userID", input.get("userID").trim());
		// sikuliUIDriver.type("username", input.get("userID"));
		// Thread.sleep(3000);
		// sikuliUIDriver.type("pwd", input.get("password"));
		uiDriver.setValue("password", input.get("password"));
		SleepUtils.sleep(TimeSlab.YIELD);
		uiDriver.click("login");
		// sikuliUIDriver.click("loginbtn");
		SleepUtils.sleep(TimeSlab.MEDIUM);
		if (uiDriver.checkPage(input.get("pageName"))) {
			SleepUtils.sleep(TimeSlab.YIELD);
			passed("Login", "Should be Logged in Successfully",
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
		SleepUtils.sleep(TimeSlab.YIELD);
		uiDriver.click("logout");
		SleepUtils.sleep(TimeSlab.YIELD);
		System.out.println(uiDriver.getValue("userID"));
		if (uiDriver.getValue("userID").equals("")) {
			passed("Logging out from the Application", "Should log out",
					"Logged out sucessfully!");
		} else {
			log("Logging out from the Application", ResultType.FAILED,
					"Should log out", "Failed to log out", true);
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
	public void Navigate(DataRow input, DataRow output)
			throws InterruptedException {
		uiDriver.setValue("search", input.get("Value"));
		Thread.sleep(2000);
		uiDriver.getValue("search");
		uiDriver.setValue("search", "");
		uiDriver.setValue("search", "Jamya" + input.get("Value"));
	}

	/**
	 * Overriding toString() method of object class to print IConnectDriver
	 * format string
	 */
	public String toString() {
		return "IConnectDriver()";
	}
}
