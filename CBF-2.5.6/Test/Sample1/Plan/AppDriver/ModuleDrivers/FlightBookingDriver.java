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

import cbf.engine.TestResult.ResultType;
import cbf.utils.DataRow;
import cbf.utils.SleepUtils;
import cbf.utils.SleepUtils.TimeSlab;
import cbfx.selenium.BaseWebModuleDriver;

/**
 * 
 * Extends BaseModuleDriver class and performs application specific operations
 * 
 */
public class FlightBookingDriver extends BaseWebModuleDriver {

	/**
	 * Launches Application
	 * 
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during execution of component
	 * @throws IOException 
	 */
	public void LaunchApp(DataRow input, DataRow output) throws IOException {
		uiDriver.launchApplication(input.get("url"));
		uiDriver.waitForBrowserStability("2000");	
		if (uiDriver.checkPage(input.get("pageName"))) {
			passed("Launching the Application",
					"Should open the Application", 
			"Application opened sucessfully!");
		} else {
			log("Launching the Application", ResultType.FAILED,
					"Should open the Application",
					"Error in opening the Application", true);
		}
	}

	/**
	 * Enters trip details
	 * 
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during execution of component
	 * @throws InterruptedException 
	 */
	public void enterTripDetails(DataRow input, DataRow output) throws InterruptedException {
		uiDriver.click("journeyType");
		uiDriver.setValue("departureCity", input.get("departureCity"));
		uiDriver.setValue("toCity", input.get("toCity"));
		uiDriver.setValue("adults", "2");
		uiDriver.setValue("childs", "1");
		uiDriver.setValue("infants", "1");
		uiDriver.click("searchFlight");
		Thread.sleep(7000);
		if (uiDriver.checkPage(input.get("pageName"))) {
			SleepUtils.sleep(TimeSlab.YIELD);
			passed("Entering trip Details", "Details should be entered uccessfully",
			"Details entered successfully");
		} else {
			failed("Entering trip Details", "Details should be entered uccessfully",
			"Failed to enter details");
		}
	}

	/**
	 * Selects Flight
	 * 
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during execution of component
	 */
	public void selectFlight(DataRow input, DataRow output) {
		uiDriver.waitForBrowserStability("3000");
		uiDriver.click("selectFlight");
		SleepUtils.sleep(TimeSlab.YIELD);
		uiDriver.waitForBrowserStability("3000");
		if (uiDriver.checkPage("Booking Details - Flightraja Travels Pvt. Ltd. (Via.com)")) {
			passed("Selecting Flight", "Flight should be selected successfully",
			"Flight selected sucessfully!");
		} else {
			failed("Selecting Flight", "Flight should be selected successfully",
			"Flight not selected ");
		}
	}

	/**
	 * Enter Traveler Details
	 * 
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during execution of component
	 * @throws InterruptedException 
	 */
	public void enterTravelerData(DataRow input, DataRow output) throws InterruptedException{
		uiDriver.setValue("title1", input.get("title1"));
		uiDriver.setValue("firstName1", input.get("firstName1"));
		uiDriver.setValue("lastName1", input.get("lastName1"));
		uiDriver.setValue("title2", input.get("title2"));
		uiDriver.setValue("firstName2", input.get("firstName2"));
		uiDriver.setValue("lastName2", input.get("lastName2"));
		uiDriver.setValue("title3", input.get("title3"));
		uiDriver.setValue("firstName3", input.get("firstName3"));
		uiDriver.setValue("lastName3", input.get("lastName3"));
		uiDriver.setValue("DOB3", input.get("dob3"));
		uiDriver.setValue("title4", input.get("title4"));
		uiDriver.setValue("firstName4", input.get("firstName4"));
		uiDriver.setValue("lastName4", input.get("lastName4"));
		uiDriver.setValue("DOB4", input.get("dob4"));
		uiDriver.setValue("mobileNumber", input.get("mobileNumber"));
		uiDriver.setValue("email", input.get("emailID"));
		uiDriver.setValue("addressStreet", input.get("street"));
		uiDriver.setValue("addressCity", input.get("city"));
		uiDriver.setValue("addressState", input.get("state"));
		uiDriver.setValue("addressCountry", input.get("country"));
		uiDriver.setValue("addressPincode", input.get("pincode"));
		uiDriver.click("termsAndConditions");
		uiDriver.click("proceedToPay");
		Thread.sleep(7000);
		uiDriver.click("confirmProceed");
		Thread.sleep(4000);
		if (uiDriver.checkPage(input.get("pageName"))) {
			passed("Entering Traveller Details", "Traveller Details should be entered successfully",
			"Traveller Details entered sucessfully!");
		} else {
			failed("Entering Traveller Details", "Traveller Details should be entered successfully",
			"Traveller Details not entered sucessfully");
		}
	}
	
	/**
	 * Overriding toString() method of object class to print FlightBookingDriver
	 * format string
	 */
	public String toString(){
		return "FlightBookingDriver()";
	}
}
