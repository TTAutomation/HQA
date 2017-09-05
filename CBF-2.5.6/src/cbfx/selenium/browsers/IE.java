/******************************************************************************
$Id : IE.java 3/3/2016 6:02:28 PM
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

package cbfx.selenium.browsers;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

public class IE implements Browser {

	/**
	 * Constructor to initialise browser related parameters
	 * 
	 * @param parameters
	 * 			
	 */
	public IE(Map parameters) {

		this.params = parameters;
	}

	/**
	 * Loads IE driver
	 * 
	 * @return IE driver instance
	 */

	public WebDriver openDriver() {
		System.setProperty("webdriver.ie.driver",
				(String) params.get("browserdriver")+"IEDriverServer.exe");
		try{
			IEDriver = new InternetExplorerDriver();
			IEDriver.manage().window().maximize();
		}catch(IllegalStateException e){
			logger.handleError("Failed to read browser details ", e);
		}
		return IEDriver;
	}

	/**
	 * Quits IE driver
	 * 
	 */
	public void closeDriver() {
		//IEDriver.quit();
	}

	/**
	 * Overriding toString() method to return IE format string
	 */
	public String toString() {
		return StringUtils.mapString(this);
	}

	private Map params;
	private WebDriver IEDriver;
	private LogUtils logger = new LogUtils(this);
}
