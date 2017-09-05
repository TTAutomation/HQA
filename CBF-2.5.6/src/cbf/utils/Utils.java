/******************************************************************************
$Id : Utils.java 3/3/2016 6:01:06 PM
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

package cbf.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Generic utility functions.
 * 
 */
public class Utils {

	/**
	 * Converts passed object array to map
	 * 
	 * @param array
	 *            of object to be converted
	 * @return Map of objects
	 */
	public static Map<Object, Object> toMap(Object[] array) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		for (int i = 0; i < array.length; i = i + 2) {
			map.put(array[i], array[i + 1]);
		}
		return map;
	}

	/**
	 * Converts passed object to string
	 * 
	 * @param object
	 *            to be converted
	 * @return string format of object
	 */
	public static String toString(Object object) {
		return object.toString();
	}

	/**
	 * Converts given string to boolean value
	 * 
	 * @param value
	 *            string to be converted
	 * @return boolean value of string
	 */
	public static boolean string2Bool(String value) {
		boolean result = false;
		if (StringUtils.match(value, StringUtils.pattern(
				"(Pass|True|Yes|yes|Y|true)", false, true))) {
			result = true;
		}
		if (StringUtils.match(value, StringUtils.pattern(
				"(Fail|False|No|N|false)", false, true))) {
			result = false;
		}
		return result;
	}

	/**
	 * Converts passed map to DataRow object
	 * 
	 * @param map
	 *            to be converted
	 * @return object of DataRow
	 */
	public static DataRow Map2DataRow(Map map) {
		return new DataRow(map);
	}
}
