/******************************************************************************
$Id : DbAccess.java 3/3/2016 6:00:01 PM
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


package cbf.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cbf.model.DataAccess;
import cbf.utils.DBUtils;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

/**
 * Implements DataAccess and provides module data
 * 
 */
public class DbAccess implements DataAccess {

	/**
	 * Constructor to initialize parameters
	 * 
	 * @param params
	 *            map containing parameters
	 */
	public DbAccess(Map params) {
		this.params = params;
		dbUtils = new DBUtils(params);
	}

	/**
	 * Returns row data for the selected rows
	 * 
	 * @param moduleCode
	 *            contains moduleCode value
	 * @param componentCode
	 *            contains componentCode value
	 * @param rowSelector
	 *            contains row selection value
	 * @return list of Map
	 */
	public List<Map> selectRows(String moduleCode, String componentCode,
			String rowSelector) {
		logger.trace("SelectRows(" + componentCode + "-" + rowSelector + ")");

		String tableName = moduleCode + "__" + componentCode;

		try {
			List<Map> list = dbUtils.runQuery("Select * from " + tableName
					+ " where [_rowId]='" + rowSelector + "';",
					new ArrayList<Object>(Integer.valueOf(rowSelector)));
			return list;

		} catch (SQLException e) {
			if (!dbUtils.checkExists(tableName)) {
				logger.handleError("Table " + tableName
						+ " does not exist for component: - " + componentCode);
				return null;
			}

			logger.handleError("Error in executing query : ", e, rowSelector,
					moduleCode, componentCode);
			return null;
		}
	}

	protected void finalize() {
		dbUtils.disconnect();
	}

	/**
	 * Returns DbAccess format string
	 */
	public String toString() {
		return StringUtils.mapString(this, params);
	}

	private LogUtils logger = new LogUtils(this);
	private String moduleCode;
	private DBUtils dbUtils;
	private Map params;

}
