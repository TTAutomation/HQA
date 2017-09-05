/******************************************************************************
$Id : UniqueUtils.java 3/3/2016 6:01:06 PM
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Utility to retrieve unique string
 * 
 */
public class UniqueUtils {

	/**
	 * Retrieves unique string from the given one
	 * 
	 * @param str
	 *            of which unique string to be found
	 * @return unique sring value
	 */
	public String uniqueString(String str) {

		String[] parsed = new String[2];
		parsed = parse(str);
		if (parsed.equals("")) {
			logger.handleError("Invalid input : " + str);
			return "????";
		}
		List<Character> domain;
		if (parsed[1].equals(""))
			domain = getDomain("0");
		else
			domain = getDomain(parsed[1]);

		int domLen = domain.size();
		int outLen = Integer.parseInt(parsed[0]);
		// setTime();
		int nSecs = Math.abs((int) (beginTm.getTime() - new Date().getTime()));
		String uniqueString = "";

		while (uniqueString.length() < outLen) {
			int n = nSecs % domLen;
			uniqueString = domain.get(n) + uniqueString;
			nSecs = nSecs / domLen;
		}

		return uniqueString;
	}

	private String[] parse(String str) {
		String[] result = new String[2];
		Pattern pattern = Pattern.compile("^\\s*(\\d+)(\\w*)\\s*$");
		Matcher match = pattern.matcher(str);
		while (match.find()) {
			result[0] = match.group(1);
			result[1] = match.group(2);
		}
		return result;
	}

	private List getDomain(String str) {
		List<Character> domain = new ArrayList<Character>();
		String[] variants = { "A", "a", "0" };
		for (String variant : variants) {
			if (str.contains(variant)) {
				switch (variant.charAt(0)) {
				case 'A':
					domain = append('A', 26);
					break;
				case 'a':
					domain = append('a', 26);
					break;
				case '0':
					domain = append('0', 10);
				}
			}
		}
		return domain;
	}

	private List append(char startChar, int numChars) {
		List domain = new ArrayList<Character>();
		if (!Character.isDigit(startChar)) {
			int asciiCode = (int) startChar;
			for (int ix = asciiCode; ix <= asciiCode + numChars - 1; ix++) {
				domain.add((char) ix);
			}
		} else {
			for (int ix = Integer.parseInt("" + startChar); ix <= numChars - 1; ix++)
				domain.add(ix);
		}
		return domain;
	}

	private LogUtils logger = new LogUtils(this);
	private Date beginTm;
	private static UniqueUtils uniqueUtils;

	/**
	 * Returns instance of UniqueUtils class
	 * 
	 * @return object of UniqueUtils
	 */
	public static synchronized UniqueUtils getInstance() {
		if (uniqueUtils == null) {
			uniqueUtils = new UniqueUtils();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		uniqueUtils.beginTm = new Date(c.get(Calendar.YEAR), 1, 1);
		return uniqueUtils;
	}

	public static String uniqueDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		String uniqueDate = sdf.format(date);
		SimpleDateFormat sdf1 = new SimpleDateFormat("HH-mm-ss");
		Date time = new Date();
		String uniqueTime = sdf1.format(time);
		String uniqueName = uniqueDate + "_Time_" + uniqueTime;

		return uniqueName;
	}

}
