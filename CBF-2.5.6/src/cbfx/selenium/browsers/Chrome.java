/******************************************************************************
$Id : Chrome.java 3/3/2016 6:02:27 PM
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
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

public class Chrome implements Browser {

	/**
	 * Constructor to initialise browser related parameters
	 * 
	 * @param parameters
	 * 
	 */

	public Chrome(Map parameters) {

		this.params = parameters;
	}

	/**
	 * Loads chrome driver
	 * 
	 * @return chrome driver instance
	 */

	public WebDriver openDriver() {
		System.setProperty("webdriver.chrome.driver",
				(String) params.get("browserdriver")+"chromedriver.exe");		
		try{
			chromeDriver = new ChromeDriver();
			//chromeDriver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		}catch(Exception e){
			logger.handleError("Failed to read browser details ", e);
		}
		return chromeDriver;
	}
	
	
	
	/**
	 * Quits chrome driver
	 * 
	 */

	public void closeDriver() {
		chromeDriver.quit();
	}

	/**
	 * Overriding toString() method to return Chrome format string
	 */
	public String toString() {
		return StringUtils.mapString(this);
	}

	private Map params;
	private WebDriver chromeDriver;
	private static LogUtils logger = new LogUtils();
}
