/******************************************************************************
$Id : PersistentMap.java 3/3/2016 6:01:05 PM
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Extends HashMap and maintains a global map
 * 
 */
public class PersistentMap extends HashMap<String, String> {
	public LogUtils logger = new LogUtils(this);

	/**
	 * Constructor to initialize parameters
	 * 
	 * @param groupName
	 *            name of group of data
	 * @param globalDataFolder
	 */
	public PersistentMap(String groupName, String globalDataFolder) {
		if (!FileUtils.makeFolder(globalDataFolder)) {
			logger.handleError("Can't create/access global data folder: ",
					globalDataFolder);
		}
		globalDataAccess = new PersistentDataAccess(globalDataFolder, groupName);
		// Retrieve list of latest values
		this.putAll(globalDataAccess.retrieve());
	}

	/**
	 * Adds key, value pair to global map
	 * 
	 * @param key
	 *            unique key for map
	 * @param value
	 *            value for that key
	 * @return String value
	 */
	public String put(String key, String value) {

		super.put(key, value);
		globalDataAccess.save(this);

		return value;
	}

	/**
	 * Adds complete map to global map
	 * 
	 * @param m
	 *            Map to be added
	 */
	public void putAll(Map m) {
		super.putAll(m);
		globalDataAccess.save(m);
	}

	/**
	 * Removes specified key from global map
	 * 
	 * @param key
	 *            to be removed
	 * @return value linked to that particular key
	 */
	public String remove(Object key) {
		String value = (String) super.get(key);
		super.remove(key);
		globalDataAccess.save(this);
		return value;
	}

	/**
	 * Overriding toString() method and returning PersistentMap format string
	 */
	public String toString() {
		return "PersistentMap()";
	}

	private class PersistentDataAccess {
		private String fileName;

		public PersistentDataAccess(String folderPath, String dataName) {
			String fileName = folderPath + "/" + dataName + ".dat";
			this.fileName = fileName;
			if (!FileUtils.fileExists(fileName)) {
				file = new File(fileName);
				try {
					file.createNewFile();
				} catch (IOException e) {
					logger.handleError("Can't create persistent data file ",
							fileName, e);
				}
			}
		}

		private void fileName(String fileName) {
			this.fileName = fileName;
		}

		public void save(Map values) {

			try {
				save(fileName, values);
			} catch (IOException e) {
				logger.handleError("Save data failed: ", fileName,values,e);
			}

		}

		private void save(String fileName, Map values) throws IOException {
			PrintWriter out = new PrintWriter(new FileWriter(fileName));
			Collection<String> keys = values.keySet();
			for (String key : keys) {
				if (!key.equals("")) {
					out.write(key + " = " + values.get(key));
					out.println();
				}
			}
			out.close();
		}

		public Map retrieve() {

			try {
				return retrieve(fileName);
			} catch (Exception e) {
				logger.handleError("Retrieve data failed: ", fileName,e);
			}
			return null;
		}

		private Map retrieve(String fileName) throws IOException {
			Map values = new HashMap();
			if (!FileUtils.fileExists(fileName)) {
				return values;
			}
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String text;
			while ((text = reader.readLine()) != null) {
				text = text.trim();

				int separatorIndex = text.contains("=") ? text.indexOf("=") : 0;
				if (separatorIndex != 0)
					values.put(text.substring(0, separatorIndex - 1), text
							.substring(separatorIndex + 1, text.length()));
			}
			reader.close();
			return values;
		}

		public String toString() {
			return StringUtils.mapString(this, fileName);
		}
	}

	private PersistentDataAccess globalDataAccess;

	private File file;
}
