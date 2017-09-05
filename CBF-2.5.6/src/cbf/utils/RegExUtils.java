/******************************************************************************
$Id : RegExUtils.java 3/3/2016 6:01:05 PM
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/*
 Add on functionality for...
 * global capture handling, internalizing stuff
 */

/**
 * 
 * Utility handles string matching and replacement functionalities.
 * 
 */
public class RegExUtils {

	/**
	 * 
	 * Matches String
	 * 
	 */
	public interface Handler {
		/**
		 * Matches string and returns boolean result
		 * 
		 * @param m
		 *            object of Matcher
		 * @return boolean result
		 */
		public boolean handle(Matcher m);
	};

	/**
	 * 
	 * Performs match and replaces string
	 * 
	 */
	public interface Replacements {
		/**
		 * Replaces old string with new one
		 * 
		 * @param m
		 *            object of matcher
		 * @return new string
		 */
		public String replacement(Matcher m); // return null if it cant be
		// replaced
	}

	/**
	 * Replaces string if matched with Pattern
	 * 
	 * @param src
	 *            string to be replaced
	 * @param p
	 *            Pattern with which string need to be matched
	 * @param r
	 *            with which source will be replaced
	 * @return new replaced string
	 */
	public static String replace(String src, Pattern p, Replacements r) {
		StringBuffer sb = new StringBuffer();
		Matcher m = p.matcher(src);
		while (m.find()) {
			String s = r.replacement(m);
			if (s == null) // retain old, if no replacement is found
				s = m.group();
			// To escape the special characters like ($)
			// TODO:Need to remove ASAP
			if (s.contains("$"))
				s = "\\" + s;
			m.appendReplacement(sb, s);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	// where replacements are directly got by keying the entire match from a map
	/**
	 * Converts map to object of Replacements
	 * 
	 * @param map
	 *            to be converted
	 * @return object of Replacements
	 */
	public static Replacements map2Replacements(final Map<String, String> map) {
		return new Replacements() {
			public String replacement(Matcher m) {
				return map.get(m.group());
			}
		};
	}

	/**
	 * Converts string to Pattern
	 * 
	 * @param s
	 *            to be converted
	 * @return Pattern generated
	 */
	public static Pattern string2Pattern(String s) {
		Pattern patt = null;
		try {
			patt = Pattern.compile(s);
		} catch (PatternSyntaxException synexc) {
		}
		return patt;
	}
}
