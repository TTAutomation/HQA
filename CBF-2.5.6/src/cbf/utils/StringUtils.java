/******************************************************************************
$Id : StringUtils.java 3/3/2016 6:01:06 PM
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

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 
 * Utility to convert various data types to string
 * 
 */
public class StringUtils {

	/**
	 * Converts passed object to string format
	 * 
	 * @param o
	 *            Object to be converted
	 * @return String form of object
	 */
	public static String toString(Object o) {
		if (o == null) {
			return "<null>";
		}
		try {
			return mapToString((Map) o);
		} catch (ClassCastException oEx) {
		}

		try {
			return arrayToString((Object[]) o);
		} catch (ClassCastException e) {
		}

		try {
			return exceptionToString((Exception) o);
		} catch (ClassCastException e) {
		}

		try {
			return o.toString();
		} catch (Exception ex) {
		}

		return "<ObjectWithoutStringForm>";
	}

	/**
	 * Converts given map to String format
	 * 
	 * @param o
	 *            Map to be converted
	 * @return String form of Map
	 */
	public static String mapToString(Map o) {
		String resultStr = "";
		if (!o.isEmpty()) {
			Set<Object> keys = o.keySet();
			String delim = "";
			for (Object key : keys) {
				resultStr += delim + key.toString() + " = " + o.get(key);
				delim = " , ";
			}
			return "{ " + resultStr + " }";
		}
		return "";
	}

	/**
	 * Converts given object ellipsis to string form
	 * 
	 * @param varargs
	 *            ellipsis of Objects
	 * @return String form of String array
	 */
	public static String arrayToString(Object... varargs) {
		String conCatValue = "";
		for (Object input : varargs) {
			conCatValue = conCatValue + input.toString();
		}
		return conCatValue;
	}

	/**
	 * Converts passed exception value to string form
	 * 
	 * @param exception
	 *            exception need to be converted
	 * @return String form of exception passed
	 */
	public static String exceptionToString(Exception exception) {
		return exception.toString();
	}

	/**
	 * Compiles string and returns Pattern
	 * 
	 * @param patternStr
	 *            string to be compiled
	 * @param isExactMatch
	 *            boolean value for exact match
	 * @param bIgnoreCase
	 *            boolean value for ignore case
	 * @return object of Pattern
	 */
	public static Pattern pattern(String patternStr, boolean isExactMatch,
			boolean bIgnoreCase) {
		Pattern regEx;
		if (isExactMatch) {
			regEx = Pattern.compile("^" + patternStr + "$");
		} else {
			regEx = Pattern.compile(patternStr);
		}
		return regEx;
	}

	public static String mapString(Object o, Object... varArgs) {
		return o.getClass().getSimpleName() + "{ " + toString(varArgs) + " }";
	}

	/**
	 * Matches passed value with Pattern and returns boolean result
	 * 
	 * @param valueToBeMatched
	 *            string to be matched
	 * @param pattern
	 *            pattern with which string needed to matched
	 * @return boolean result
	 */
	public static boolean match(String valueToBeMatched, Pattern pattern) {
		return pattern.matcher(valueToBeMatched).find();
	}
}
