/******************************************************************************
$Id : BaseWebAppDriver.java 3/3/2016 6:01:28 PM
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

package cbfx.selenium;

import java.util.Map;

import cbf.engine.BaseAppDriver;
import cbfx.misc.SikuliUIDriver;

abstract public class BaseWebAppDriver extends BaseAppDriver {

	public static WebUIDriver uiDriver;
	public static SikuliUIDriver sikuliUIDriver;

	/**
	 * initialize WebDriver
	 */
	public BaseWebAppDriver(Map params) {

		uiDriver = new WebUIDriver(params);
		sikuliUIDriver = new SikuliUIDriver();
	}

	/**
	 * close browsers
	 */
	public void recover() {
		if (uiDriver != null)
			uiDriver.closeBrowsers();
	}
}
