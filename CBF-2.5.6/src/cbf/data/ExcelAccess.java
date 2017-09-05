/******************************************************************************
$Id : ExcelAccess.java 3/3/2016 6:00:01 PM
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

import java.util.List;
import java.util.Map;

import cbf.harness.ResourcePaths;
import cbf.model.DataAccess;
import cbf.utils.DTAccess;
import cbf.utils.FileUtils;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbf.utils.Utils;

/**
 * Implements DataAccess and provides module data
 * 
 */
public class ExcelAccess implements DataAccess {

	/**
	 * 
	 * Constructor to initialize parameters
	 * 
	 * @param params
	 *            map containing parameters
	 */
	public ExcelAccess(Map params) {
		this.params = params;
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

		logger.trace("SelectRows(" + moduleCode + "-" + componentCode + "-"
				+ rowSelector + ")");

		List<Map> data = null;

		String filePath = getFilePath(moduleCode, componentCode);
		if (filePath == null) { // changed .equals to ==
			logger.handleError("File path is null for ", componentCode);
		}

		DTAccess dtAccess = new DTAccess(filePath);

		// CHECKME: sheetExists call looks dubious
		if (cbf.utils.ExcelAccess.isSheetExists(filePath, componentCode)) {
			data = dtAccess.readSelectedRows(componentCode, rowSelector);
		} else {
			logger.handleError("Sheet " + componentCode
					+ " doesn't exists in the file " + moduleCode + "Data.xls");
		}

		return data;
	}

	private String getFilePath(String moduleCode, String componentCode) {
		String filePath = null;
		if (Utils.string2Bool((String) ResourcePaths.getInstance()
				.getSuiteResource("Plan", ""))) {
			filePath = tryFilePath(moduleCode, componentCode + ".xls");

		}
		if (filePath == null) {
			filePath = tryFilePath(moduleCode, componentCode + ".xlsx");
		}
		if (filePath == null) {
			filePath = tryFilePath("", moduleCode + "Data.xls");
		}
		if (filePath == null) {
			filePath = tryFilePath("", moduleCode + "Data.xlsx");
		}
		if (filePath == null) {
			logger.handleError("Failed to find the data file ", moduleCode,
					componentCode);
		}
		return filePath;
	}

	private String tryFilePath(String branchPath, String fileName) {
		logger.trace("TryFilePath(" + branchPath + "-" + fileName + ")");
		String filePath = (String) params.get("folderpath");
		if (filePath.equals("")) {
			String folderPath = "Plan/Data";
			if (!(branchPath.equals(""))) {
				folderPath = folderPath + "/" + branchPath;
			}
			filePath = resourcePaths.getSuiteResource(folderPath, fileName);
			if ((filePath == null) || !(FileUtils.fileExists(filePath))) {
				logger.trace("Data file " + fileName + " does not exists. ",
						filePath, folderPath);
				return null;
			}
		}
		logger.trace("TryFilePath(" + branchPath + "-" + fileName + ")="
				+ filePath);
		return filePath;
	}

	/**
	 * Returns ExcelAccess format string
	 */
	public String toString() {
		return StringUtils.mapString(this, params);
	}

	private LogUtils logger = new LogUtils(this);
	private ResourcePaths resourcePaths = ResourcePaths.getInstance();
	private Map params;
}
