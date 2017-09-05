/******************************************************************************
$Id : ExcelAccess.java 3/3/2016 6:01:04 PM
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.DefaultFormatter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * 
 * Handles all the function related to Excel Data sheet like reading
 * sheet,reading row,etc..
 * 
 */
public class ExcelAccess {
	private ExcelAccess() {
	}

	/**
	 * 
	 * Handles row data
	 * 
	 */
	public interface RowHandler {

		/**
		 * Accesses row and returns boolean result
		 * 
		 * @param rowAccess
		 *            object of RowAccess
		 * @param rowIx
		 *            index of row to be accessed
		 * @return boolean result
		 */
		boolean handleRow(RowAccess rowAccess, int rowIx);
	}

	/**
	 * 
	 * Accesses row of sheet and sets respective values
	 * 
	 */
	public interface RowAccess {
		String[] get();

		void set(String[] values);

		void set(int colIx, String value);
	};

	/**
	 * Accesses the particular sheet of workbook
	 * 
	 * @param fileName
	 *            name of workbook
	 * @param sheetName
	 *            name of excel sheet
	 * @param rowHandler
	 *            object of RowHandler
	 * @return number of rows
	 */
	public static int accessSheet(String fileName, String sheetName,
			RowHandler rowHandler) {


			Workbook newWorkbook = null;
		newWorkbook = getWorkbook(fileName);

		Sheet sheet = newWorkbook.getSheet(sheetName);
		return access(sheet, rowHandler);
		

	}

	public static Workbook getWorkbook(String fileName) {
		Workbook newWorkbook = null;
		try {

			File file = new File(fileName);
			String fileName1 = file.getName();
			FileInputStream inputStream = new FileInputStream(file);
			String fileExtensionName = fileName1.substring(fileName1
					.indexOf("."));
			if (fileExtensionName.equals(".xlsx")) {
				newWorkbook = new XSSFWorkbook(inputStream);
			} else if (fileExtensionName.equals(".xls")) {
				newWorkbook = new HSSFWorkbook(inputStream);
			}
		} catch (IOException e) {
			logger.handleError("Failed to access file: ", fileName, e);
		}
		return newWorkbook;

	}
	
	
	
	
	
	/**
	 * Accesses the particular sheet of uiMap(locators) workbook
	 * 
	 * @param fileName
	 *            name of workbook
	 * @param rowHandler
	 *            object of RowHandler
	 * @return number of Locators
	 */

	public static int accessLocatorSheet(String fileName, RowHandler rowHandler) {

		int numberofLoc = 0;
		Workbook workbook = getWorkbook(fileName);
		int sheetNo = workbook.getNumberOfSheets();

		for (int shNo = 0; shNo < sheetNo; shNo++) {
			Sheet sheet = workbook.getSheetAt(shNo);
			numberofLoc = numberofLoc + access(sheet, rowHandler);






		}

		return numberofLoc;

	}

	private static class SimpleRowAccess implements RowAccess {
		final ArrayList<Cell> cells;

		SimpleRowAccess(ArrayList<Cell> cells) {
			this.cells = cells;
		}

		public String[] get() {
			String[] colValues = new String[cells.size()];
			for (int j = 0; j < cells.size(); j++) {

				if (cells.get(j) == null) {
					colValues[j] = "";
					continue;
				}

				switch (cells.get(j).getCellType()) {
				case Cell.CELL_TYPE_STRING:
					colValues[j] = cells.get(j).getStringCellValue();
					break;

				case Cell.CELL_TYPE_NUMERIC:
					DateFormat dateFormat = new SimpleDateFormat(
							"MM/dd/yyyy HH:mm:ss");
					Date today = Calendar.getInstance().getTime();

					if (DateUtil.isCellDateFormatted(cells.get(j))) {
						colValues[j] = dateFormat.format(today);
					} else {
						DataFormatter dataFormatter = new DataFormatter();
						colValues[j] = dataFormatter.formatCellValue(cells
								.get(j));
					}

					break;

				case Cell.CELL_TYPE_BOOLEAN:
					colValues[j] = Boolean.toString(cells.get(j)
							.getBooleanCellValue());
					break;

				case Cell.CELL_TYPE_BLANK:
					colValues[j] = cells.get(j).getStringCellValue().trim();
					break;

				}
			}
			return colValues;
		}

		public void set(String[] values) {
			for (int i = 0; i < cells.size(); ++i) {
				if (i >= values.length)
					break;
			}
		}

		public void set(int colIx, String value) {

		}
	}

	/**
	 * Accesses sheet and returns the row count
	 * 
	 * @param sheet
	 *            name of sheet
	 * @param rowHandler
	 *            object of RowHandler
	 * @return count of rows
	 */
	public static int access(Sheet sheet, RowHandler rowHandler) {
			int count = 0;
		int rowCount = 0;
		int rowStart = sheet.getFirstRowNum();
		int rowEnd = sheet.getLastRowNum();

		ArrayList<Cell> cellArr = new ArrayList<Cell>();
		for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
			Row r = sheet.getRow(rowNum);
			if (r == null) {
				break;
			}
			int lastColumn = r.getLastCellNum();
			for (int cn = 0; cn < lastColumn; cn++) {
				Cell c = r.getCell(cn);

				cellArr.add(c);
			}
			boolean rc = rowHandler.handleRow(new SimpleRowAccess(cellArr),
					rowCount);
			if (!rc)
				break;
			++count;
			cellArr.clear();
			rowCount++;
		}

		return count;
	}

	/**
	 * Checks existence of excel sheet
	 * 
	 * @param sheetName
	 *            name of excel sheet
	 * @param fileName
	 * @return excel sheet exists or not
	 */
	public static boolean isSheetExists(String fileName, String sheetName) {
		boolean result = false;

		try {
				Workbook workbook = getWorkbook(fileName);

		int numberOfSheet = workbook.getNumberOfSheets();

		String[] sheets = new String[numberOfSheet];

		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			sheets[i] = workbook.getSheetName(i);
		}

		for (String sheet : sheets) {
			if (sheet.equals(sheetName)) {
				result = true;
				return result;
			}
		}

		} catch (Exception e) {
			logger.handleError("Error while checking the existence of sheet :",
					fileName, sheetName, e.getMessage());
		} 
		return result;
	}

	/**
	 * 
	 * Sets and gets the value of rows
	 * 
	 */
	public interface NamedRowAccess {
		/**
		 * Gets Row and returns it
		 * 
		 * @return object of DataRow
		 */
		public DataRow get();

		/**
		 * Sets value of row
		 * 
		 * @param namedValues
		 *            object of DataRow
		 */
		public void set(DataRow namedValues);

		/**
		 * Overloaded method that sets value of particular column
		 * 
		 * @param colName
		 *            name of field
		 * @param value
		 *            value of that field
		 */
		public void set(String colName, String value);
	}

	/**
	 * 
	 * Implementing RowHandler interface and handles row data
	 * 
	 */
	public static abstract class NamedRowHandler implements RowHandler {
		private String[] colNames;
		private Map<String, Integer> colName2IxMap = new HashMap<String, Integer>();

		private final class Access implements NamedRowAccess {
			private RowAccess rowAccess;

			private Access(RowAccess rowAccess) {
				this.rowAccess = rowAccess;
			}

			public DataRow get() {
				String[] values = rowAccess.get();
				HashMap namedValues = new HashMap();
				for (int ix = 0; ix < values.length; ix++) {
					if (ix >= colNames.length) {
						break;
					}
					String colName = colNames[ix];
					namedValues.put(colName, values[ix]);
				}

				return new DataRow(namedValues);
			}

			public void set(DataRow namedValues) {
				String[] values = new String[colNames.length];
				/*
				 * for (<key, value> : namedValues) { set(key, value); }
				 */
			}

			public void set(String colName, String value) {
				int ix = colName2IxMap.get(colName);

			}
		}

		/**
		 * Abstract method of RowHandler
		 * 
		 * @param namedRowAccess
		 *            object of NamedRowAccess
		 * @param rowIx
		 *            index of row
		 * @return boolean result
		 */
		public abstract boolean handleRow(NamedRowAccess namedRowAccess,
				int rowIx);

		/**
		 * Handles row and returns boolean result
		 * 
		 * @param rowAccess
		 *            object of RowAccess
		 * @param rowIx
		 *            index of row
		 * @return boolean result
		 */
		final public boolean handleRow(RowAccess rowAccess, int rowIx) {
			String[] colValues = rowAccess.get();
			if (rowIx == 0) { // header row
				setColNames(colValues);
				return true;
			}
			return handleRow(new Access(rowAccess), rowIx - 1);
		}

		private void setColNames(String[] colNames) {
			this.colNames = colNames;
			for (int ix = 0; ix < colNames.length; ix++)
				colName2IxMap.put(colNames[ix], ix);
		}
	}

	/**
	 * 
	 * Extending NamedRowHandler class and handles row data
	 * 
	 */
	public static abstract class MapReader extends NamedRowHandler {
		/**
		 * Abstract method of NamedRowHandler class
		 * 
		 * @param row
		 *            object of DataRow
		 * @param rowIx
		 *            index of row
		 * @return boolean result
		 */
		public abstract boolean handleRow(DataRow row, int rowIx);

		/**
		 * Overloaded method of NamedRowHandler class
		 * 
		 * @param namedRowAccess
		 *            object of NamedRowAccess
		 * @param rowIx
		 *            index of row
		 * @return boolean result
		 */
		final public boolean handleRow(NamedRowAccess namedRowAccess, int rowIx) {
			return handleRow((DataRow) namedRowAccess.get(), rowIx);
		}
	}

	/**
	 * 
	 * Extends MapHeader class and defines its abstract method
	 * 
	 */
	public static class RowArrayBuilder extends MapReader {
		private List<Map> rows;

		/**
		 * Constructor to initialize List of rows
		 * 
		 * @param rows
		 *            List containing row data in Map
		 */
		public RowArrayBuilder(List<Map> rows) {
			this.rows = rows;
		}

		/**
		 * Abstract method of MapHeader class that is handling row data
		 * 
		 * @param row
		 *            object of DataRow
		 * @param rowIx
		 *            index of row
		 * @return boolean result
		 */
		public boolean handleRow(DataRow row, int rowIx) {
			rows.add(row);
			return true;
		}
	}

	private static LogUtils logger = new LogUtils();
}
