/******************************************************************************
$Id : Substitutor.java 3/3/2016 6:01:06 PM
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Evaluates the expression which provided as parameters for test step. E.g.
 * ${TODAY+1} gives you tomorrows date.
 * 
 * 
 */
public class Substitutor {
	public interface Substitution {
		/**
		 * Retrieves string for passed reference
		 * 
		 * @param ref
		 *            for which value is required
		 * @return String value for particular reference
		 */
		public String get(String ref);
	};

	private static class NestableMap implements Substitution {
		private Map map;

		NestableMap(Map m) {
			this.map = m;
		}

		public String get(String ref) {
			Object o = map.get(ref);
			if (o != null) {
				try {
					return (String) o; // classcastexception handling
				} catch (ClassCastException e) {

				}
			} else {
				if (ref.contains("UniqueUtils"))
					return UniqueUtils.uniqueDate();

			}
			int dotIx = ref.indexOf(".");
			if (dotIx < 0)
				return null;

			String ref1 = ref.substring(0, dotIx).trim();
			o = map.get(ref1);
			if (ref1 == null)
				return null;

			String ref2 = ref.substring(dotIx + 1).trim();
			if (o instanceof Substitution) {
				return ((Substitution) o).get(ref2);
			}

			if (o instanceof Map<?, ?>) {
				return (new NestableMap((Map) o)).get(ref2);
			}

			return null;
		}
	};

	private final Substitution substitution;

	/**
	 * Constructor to initialize Substitution object
	 * 
	 * @param substitution
	 *            object of Substitution
	 */
	public Substitutor(Substitution substitution) {
		this.substitution = substitution;
	}

	/**
	 * Overloaded constructor to initialize Map of substitution objects
	 * 
	 * @param substitution
	 *            Map of substitution objects
	 */
	public Substitutor(Map substitution) {
		this.substitution = new NestableMap(substitution);
	}

	private static Pattern referencePattern = RegExUtils
			.string2Pattern("\\$\\{([^}]+)\\}");

	/**
	 * Substitutes given string with references values
	 * 
	 * @param s
	 *            string to be substituted
	 * @return new string with substitution
	 */
	public String substitute(String s) {
		return RegExUtils.replace(s, referencePattern,
				new RegExUtils.Replacements() {
					public String replacement(Matcher m) {
						String ref = m.group(1).trim();
						return substitution.get(ref);
					}
				});
	}

	public Object substitute(Object o) {
		if (o == null)
			return null;

		try {
			return substitute((String) o);
		} catch (ClassCastException c) {
		}

		try {
			return substitute((Map) o);
		} catch (ClassCastException c) {
		}

		try {
			return substitute((List) o);
		} catch (ClassCastException c) {
		}

		return o;
	}

	/**
	 * Substitutes given map for references
	 * 
	 * @param map
	 *            to be substituted
	 * @return Substituted map
	 */
	public Map substitute(final Map map) {
		Map substitutedMap = new HashMap();
		Collection<String> keys = map.keySet();
		for (String key : keys) {
			Object value = map.get(key);
			substitutedMap.put(key, substitute(value));
		}
		return substitutedMap;
	}

	/**
	 * Substitutes given list for references
	 * 
	 * @param list
	 *            to be substituted
	 * @return substituted list
	 */
	public <T> List substitute(final List<T> list) {
		List<T> substitutedList = new ArrayList<T>(list.size());
		for (int ix = 0; ix < list.size(); ix++) {
			T temp = list.get(ix);
			if (temp instanceof Map<?, ?>) {
				temp = (T) substitute((Map) temp);
			} else if (temp instanceof String) {
				temp = (T) substitute((String) temp);
			}
			substitutedList.add(ix, temp);
		}
		return substitutedList;

	}

	/**
	 * Returns Substituter format string
	 * 
	 */
	public String toString() {
		return StringUtils.mapString(this, substitution);

	}
}
