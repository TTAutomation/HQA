/******************************************************************************
$Id : ExcelObjectMap.java 3/3/2016 6:01:28 PM
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cbf.harness.ResourcePaths;
import cbf.utils.ExcelAccess;
import cbf.utils.FileUtils;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

public class ExcelObjectMap implements ObjectMap {

	public List<Map> uiMap;

	public ExcelObjectMap(Map params) {
		uiMap = readLocators();
	}

	/**
	 * Reads locators and returns List of it
	 * 
	 * @return List of locators
	 */
	public List<Map> readLocators() {
		List<Map> locators = new ArrayList<Map>();
		try {

			String filepath = ResourcePaths.getInstance().getSuiteResource(
					"Plan/AppDriver/OR", "uiMap.xls");
			if (FileUtils.fileExists(filepath)) {
				ExcelAccess.accessLocatorSheet(filepath,
						new ExcelAccess.RowArrayBuilder(locators));
			} else {
				ExcelAccess.accessLocatorSheet(ResourcePaths.getInstance()
						.getSuiteResource("Plan/AppDriver/OR", "uiMap.xlsx"),
						new ExcelAccess.RowArrayBuilder(locators));
			}
		} catch (Exception e) {
			logger.handleError("Error while reading the locators", e);
		}

		return locators;

	}

	public List<Map> ObjectMaps(String pageName) {
		Map result;
		List<Map> resultMap = new ArrayList();
		String sElement = "";
		try {
			for (Map map : uiMap) {
				sElement = ((String) map.get("elementName"));
				if (sElement.contains(".")) {
					String temp = sElement.substring(0, sElement.indexOf("."));
					if (pageName.matches(temp)) {
						result = map;
						resultMap.add(result);
					}
				}
			}
		} catch (Exception e) {
			logger.trace(
					"Error: Caused while searching the locators of page \" "
							+ pageName + " \"", e);
		}
		return resultMap;

	}

	public Map properties(String element) {
		Map result = null;
		try {
			for (Map map : uiMap) {
				String temp = (String) map.get("elementName");
				if (element.matches(temp) || element.equals(temp)) {
					result = map;
					break;
				}
			}
		}

		catch (Exception e) {
			logger.handleError(
					"Error: Trying to retrieve the Locator of Unknown Element \" "
							+ element + " \"", e);
		}
		return result;
	}

	/**
	 * Overriding toString() method and returns ObjectMap format string
	 */
	public String toString() {
		return StringUtils.mapString(this, uiMap);
	}

	LogUtils logger = new LogUtils(this);
}
