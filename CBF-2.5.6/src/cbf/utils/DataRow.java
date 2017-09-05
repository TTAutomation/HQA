/******************************************************************************
$Id : DataRow.java 3/3/2016 6:01:03 PM
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
 * Wrapper class on DataRow
 * 
 */
public class DataRow extends HashMap<String, String> {

	/**
	 * Overloaded constructor to generate Map
	 * 
	 * @param data
	 *            Map containing details
	 */
	public DataRow(Map<String, String> data) {
		this.putAll(data);
	}

	public DataRow() {
	}

	/**
	 * Returns map values in string format
	 */
	public String toString() {
		String mapValues = "";
		Iterable<String> keys = this.keySet();
		for (String key : keys) {
			mapValues = mapValues + key + " = " + this.get(key) + ";";
			System.out.println("MapValues--------------" + mapValues);

		}
		return StringUtils.mapString(this,mapValues);
	}

}
