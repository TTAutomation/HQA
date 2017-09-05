/******************************************************************************
$Id : DBUtils.java 3/3/2016 6:01:04 PM
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to handle connection to Microsoft Access Database
 * 
 */
public class DBUtils {

	/**
	 * Constructor to make connection to database dbName name of database
	 */
	public DBUtils(Map params) {
		connection = getConnection(params);
	}

	private Connection connection;

	/**
	 * Checks if the database exists or not
	 * 
	 * @param tableName
	 *            contains name of the table
	 * @return boolean data
	 */

	public boolean checkExists(String tableName) {
		boolean result = false;
		try {
			
			DatabaseMetaData d = connection.getMetaData();
			ResultSet resultSet = d.getTables(null, null, "%", null);

			while (resultSet.next()) {
				String table = resultSet.getString(1);
				if (table.equals(tableName)) {
					result = true;
				}
			}
			resultSet.close();
			return result;
		} catch (Exception e) {
			logger.handleError("Exception caught while accessing database for", tableName,e);
			return result;
		}

	}

	/**
	 * 
	 * Executes the query string and returns a list of Map
	 * 
	 * @param queryString
	 *            contains the SQL query
	 * @param params
	 * @return list of Map
	 * @throws SQLException
	 */
	public List<Map> runQuery(String queryString, List<Object> params)
			throws SQLException {
		ResultSet resultSet = null;
		resultSet = execute(connection, queryString, params);
		List<Map> list = null;
		list = rs2Map(resultSet);
		return list;
	}

	/**
	 * Makes connection to the database
	 * 
	 * @param params
	 *            dbName contains name of the database
	 * @return Connection object or null depending upon whether connection is
	 *         established or not
	 */
	private Connection getConnection(Map params) {

		String dbName = (String) params.get("dbname");
		String userID = (String) params.get("userID");
		String password = (String) params.get("password");
		String url = (String) params.get("url") + ";" + "DBQ=" + dbName + ";"
				+ "Uid=" + userID + ";" + "Pwd=" + password + ";";
		try {
			Class.forName((String) params.get("classname"));
			connection = DriverManager.getConnection(url, userID, password);
			return connection;
		} catch (Exception e) {
			logger.handleError("Error in connecting to the database ", dbName,
					" for user : ", userID, " and password : ", password, e);
			return null;
		}

	}

	/**
	 * Disconnects from the established connection
	 */

	public void disconnect() {
		if (connection == null)
			return;

		try {
			logger.trace("disconnecting");
			connection.close();
		} catch (SQLException e) {
			logger.handleError("Database is already disconnected : ", e);
		} finally {
			connection = null;
		}
	}

	private ResultSet execute(Connection connection, String queryString,
			List<Object> params) throws SQLException {
		PreparedStatement preStmt;
		ResultSet resultSet = null;

		preStmt = connection.prepareStatement(queryString);

		if (params != null) {
			for (Object temp : params) {
				preStmt.setObject(params.indexOf(temp) + 1, temp);
			}
		}
		resultSet = preStmt.executeQuery();

		return resultSet;
	}

	private List<Map> rs2Map(ResultSet resultSet) {
		List<Map> list = new ArrayList<Map>();
		ResultSetMetaData meta = null;
		try {
			meta = resultSet.getMetaData();
		} catch (SQLException e1) {
			logger.handleError("Error in mapping metadata : ", e1);
		}

		try {
			while (resultSet.next()) {
				Map<String, Object> hashMap = new HashMap<String, Object>();
				for (int i = 1; i <= meta.getColumnCount(); i++) {
					String key = meta.getColumnName(i);
					String value = resultSet.getString(key);
					hashMap.put(key, value);
				}
				list.add(hashMap);
			}
		} catch (SQLException e) {
			logger.handleError("Exception caught : ", e);
		}
		return list;
	}

	/**
	 * Overriding toString() method and returning DBUtils format string
	 */
	public String toString() {
		return StringUtils.mapString(this, connection);
	}

	private LogUtils logger = new LogUtils(this);
}
