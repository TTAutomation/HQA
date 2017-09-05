/******************************************************************************
$Id : DTAccess.java 3/3/2016 6:01:04 PM
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
import java.util.List;
import java.util.Map;

import cbf.utils.ExcelAccess.MapReader;

/**
 * Implements functionalities like readrows to read all rows from component
 * sheet, read selected rows etc.
 * 
 */
public class DTAccess {
	private LogUtils logger = new LogUtils(this);
	private String filePath;

	/**
	 * 
	 * Interface to select row
	 * 
	 */
	public interface RowSelector {
		/**
		 * Returns true/false depending on row value
		 * 
		 * @param row
		 *            Map of row values
		 * @param rowIx
		 *            index of row
		 * @return row exists or not
		 */
		public boolean select(Map row, int rowIx);
	}

	/**
	 * 
	 * Implements RowSelector interface and selects row by ID
	 * 
	 */
	public static class RowSelectorByRowId implements RowSelector {
		private String rowId;

		/**
		 * Constructor to initialize rowId
		 * 
		 * @param rowId
		 *            contains rowId value
		 */
		public RowSelectorByRowId(String rowId) {
			this.rowId = rowId;
		}

		/**
		 * Returns true/false depending on row value
		 * 
		 * @param row
		 *            Map of row values
		 * @param rowIx
		 *            index of row
		 * @return row exists or not
		 */
		public boolean select(Map row, int rowIx) {
			if (rowId.equals("")) {
				return true;
			} else {
				return rowId.equals((String) row.get("_rowId"));
			}
		}
	}

	/**
	 * Constructor to initialize excelFilePath variable
	 * 
	 * @param excelFilePath
	 *            path of excel file
	 */
	public DTAccess(String excelFilePath) {
		this.filePath = excelFilePath; // FIXME: check for file
		// existence/readability;

	}

	/**
	 * Returns List containing excel sheet rows
	 * 
	 * @param sheetName
	 *            name of excel sheet
	 * @return List of rows
	 */
	public List<Map> readSheet(String sheetName) {
		return readSelectedRows(sheetName, "");
	}

	// FIXME:catch exception and call handleError

	/**
	 * Reads selected rows and returns list
	 * 
	 * @param sheetName
	 *            name of excel sheet
	 * @param selector
	 *            object of RowSeletor
	 * @return list of selected rows
	 */
	public List<Map> readSelectedRows(String sheetName,
			final RowSelector selector) {

		class MyHandler extends MapReader {
			public Map outrow;
			List<Map> rows = new ArrayList();

			@Override
			public boolean handleRow(DataRow row, int rowIx) {
				if (selector == null || selector.select(row, rowIx)) {
					rows.add(row);
				}
				return true;
			}
		}

		MyHandler mh = new MyHandler();
		ExcelAccess.accessSheet(filePath, sheetName, mh);

		return mh.rows;
	}

	// convenience
	/**
	 * Reads selected rows and returns list
	 * 
	 * @param sheetName
	 *            name of excel sheet
	 * @param rowId
	 *            index of row
	 * @return list of selected rows
	 */
	public List<Map> readSelectedRows(String sheetName, String rowId) {
		RowSelector selector = new RowSelectorByRowId(rowId);
		List<Map> selectedRows = null;
		selectedRows = readSelectedRows(sheetName, selector);
		return selectedRows;
	}

	/**
	 * Overriding toString() method and returning DTAccess format string
	 */
	public String toString() {
		return StringUtils.mapString(this, filePath);
	}
}
