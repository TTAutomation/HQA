/******************************************************************************
$Id : SikuliUIDriver.java 3/3/2016 6:01:23 PM
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

package cbfx.misc;

import java.util.Map;

//import org.sikuli.script.Screen;

import cbf.harness.Harness;
import cbf.plugin.PluginManager;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbfx.selenium.ObjectMap;

public class SikuliUIDriver {

	/**
	 * @param parameters
	 *            contains sikuli
	 */
	public SikuliUIDriver() {
		Map obj = (Map<String, Object>) Harness.GCONFIG.get("ObjectMap");
		objMap = (ObjectMap) PluginManager.getPlugin(
				(String) obj.get("plugin"), null);
	}

	public String getLocator(String elementName) {
		String actualLocator = (String) objMap.properties(elementName).get(
				"locator");
		return actualLocator;
	}

	/**
	 * Clicks on element
	 * 
	 * @param elementName
	 *            name of element to be clicked
	 * 
	 */
	public void click(String elementName) {

		try {

			String locator = getLocator(elementName);

			//sikuli.click(locator);

		} catch (Exception e) {
			logger.handleError("Error caused while clicking on image ",
					elementName, e);
		}
	}

	/**
	 * Double clicks on element
	 * 
	 * @param elementName
	 *            name of element to be double clicked
	 * 
	 */
	public void doubleClick(String elementName) {
		try {

			String locator = getLocator(elementName);

			//sikuli.doubleClick(locator);

		} catch (Exception e) {
			logger.handleError("Error caused while double clicking on image ",
					elementName, e);
		}
	}

	/**
	 * Right clicks on element
	 * 
	 * @param elementName
	 *            name of element to be right clicked
	 * 
	 */

	public void rightClick(String elementName) {
		try {

			String locator = getLocator(elementName);

			//sikuli.rightClick(locator);

		} catch (Exception e) {
			logger.handleError("Error caused while right clicking on image ",
					elementName, e);
		}
	}

	/**
	 * sets value on element
	 * 
	 * @param elementName
	 *            name of element to be set
	 * @param value
	 *            value to be set
	 * 
	 */
	public void type(String elementName, String value) {
		try {

			String locator = getLocator(elementName);

			//sikuli.type(locator, value);

		} catch (Exception e) {
			logger.handleError("Error caused By typing on image ", elementName,
					e);
		}
	}

	/**
	 * Returns SikuliUIDriver format string
	 * 
	 */
	public String toString() {
		return StringUtils.mapString(this, objMap);

	}

	private ObjectMap objMap;
	//private Screen sikuli;
	private cbf.utils.LogUtils logger = new LogUtils(this);
}
