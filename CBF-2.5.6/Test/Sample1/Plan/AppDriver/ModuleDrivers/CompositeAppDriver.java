/******************************************************************************
$Id : CompositeAppDriver.java 9/8/2014 1:21:28 PM
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

import java.util.HashMap;
import java.util.Map;

import cbf.model.ModuleDriver;
import cbfx.selenium.BaseWebAppDriver;

/**
 * 
 * Extends BaseWebAppDriver class and starts execution
 * 
 */
public class CompositeAppDriver extends BaseWebAppDriver {
	
	public CompositeAppDriver(Map params) {
		super(params);
	}

	/**
	 * Initializes the modules specific to the application to be automated
	 * 
	 * @param resultLogger
	 *            TestResultLogger object with methods like passed, failed,
	 *            error etc available for reporting runtime results
	 * @return Map of modules
	 */
	public Map<String, ModuleDriver> loadModuleDrivers() {
		HashMap<String, ModuleDriver> moduleDrivers = new HashMap<String, ModuleDriver>();
		//moduleDrivers.put("DemoInsurance", new DemoInsuranceDriver());
		moduleDrivers.put("IConnect", new IConnectDriver());
		moduleDrivers.put("FlightBooking", new FlightBookingDriver());		
		return moduleDrivers;
	}
	
	public void recover(){
		super.recover();
	}

}
