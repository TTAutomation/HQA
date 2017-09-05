/******************************************************************************
$Id : ExcelReporter.java 3/3/2016 6:00:45 PM
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

package cbf.reporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cbf.model.ResultReporter;
import cbf.model.TestCase;
import cbf.model.TestIteration;
import cbf.engine.TestResult;
import cbf.engine.TestResult.ResultType;
import cbf.model.TestStep;
import cbf.harness.ResourcePaths;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbf.utils.Utils;



/**
 * 
 * Implements ResultReporter and generates Excel reports
 * 
 */
public class ExcelReporter implements ResultReporter {
	List<String> testCases = new ArrayList<String>();
	
	
	/**
	 * Constructor to initialize parameters
	 * 
	 * @param params
	 *            map containing parameters
	 */
	public ExcelReporter(Map params) {
		filePath = (String) params.get("filepath");
		if (filePath.equals("")) {
			filePath = ResourcePaths.getInstance().getRunResource(
					"ExecutionReport" + params.get("pattern") + ".xls", "");
		}
		templatePath = ResourcePaths.getInstance().getFrameworkResource(
				"Resources", "ReportTemplate.xls");
		SHN_SUMMARY = "Summary";
		SHN_DETAILS = "Details";
		SHN_COVER = "Cover";
		LINK_COL = "A";
	}

	/*
	 * Function: open(public) Goal: Makes the engine ready for use Out Params:
	 * Boolean - Didn't the engine manage to start?
	 */

	public void open(Map headers) {
		logger.trace("Report: open");
		File srcFile = new File(templatePath);
		File targetFile = new File(filePath);
		boolean isExists = targetFile.exists();
		if (!isExists) {
			try {
				FileUtils.copyFile(srcFile, targetFile);
			} catch (Exception e) { // FIXME: specific exception
				logger.handleError(
						"Error in making a new report file from template", e,
						srcFile, targetFile);
			}
		}

		openFile(targetFile);

		if (!isExists) { // new file
			writeHeaders(headers);
		}
	}

	/*
	 * Function: close(public) Goal: Stops the engine In Params: None Out
	 * Params: None
	 */
	public void close() {
		logger.trace("Report: Close");

		evaluateFormulae();
		try {
			outputStream = new FileOutputStream(new File(filePath));
			newWorkbook.write(outputStream);

		} catch (IOException e) {
			logger.handleError("Error closing excel", e);
		}
		try {
			((FileInputStream) newWorkbook).close();
			((FileOutputStream) newWorkbook).close();
			testCases.size();
		} catch (IOException e) {
			logger.handleError("Error closing excel", e);
		}
	}

	public void start(TestResult result) {
		report("START", result, result.entityDetails);
	}

	public void log(TestResult result, ResultType rsType, Map details) {
		report("DETAILS", result, details);
	}

	public void finish(TestResult result, ResultType rsType, Object details) {
		report("FINISH", result, details);
	}

	private Workbook newWorkbook = null;
	private Sheet oSummarySheet, oDetailsSheet, oCoverSheet;
	private int detailsRowNum, summaryRowNum;
	private CreationHelper createHelper;
	private FileInputStream inputStream;
	private FileOutputStream outputStream;

	/*
	 * 'Function Name: openFile 'Description: This Function opens a created
	 * Result File 'Parameter: FilePath - Path of the result file
	 */
	private void openFile(File targetFile) {
		try {
			//
			inputStream = new FileInputStream(targetFile);

			String fileName = targetFile.getName();

			// Find the file extension by spliting file name in substing and
			// getting only extension name

			String fileExtensionName = fileName
					.substring(fileName.indexOf("."));

			// Check condition if the file is xlsx file

			if (fileExtensionName.equals(".xlsx")) {
				newWorkbook = new XSSFWorkbook(inputStream);
			} else if (fileExtensionName.equals(".xls")) {
				newWorkbook = new HSSFWorkbook(inputStream);
			}
			oSummarySheet = newWorkbook.getSheet(SHN_SUMMARY);
			oDetailsSheet = newWorkbook.getSheet(SHN_DETAILS);
			oCoverSheet = newWorkbook.getSheet(SHN_COVER);

			summaryRowNum = oSummarySheet.getLastRowNum() + 1;
			detailsRowNum = oDetailsSheet.getLastRowNum() + 1;
		} catch (IOException e) {
			logger.handleError("Error while accessing workbook ", e);
		}
	}

	private void writeHeaders(Map headers) {
		Iterator iterator = headers.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();
			try {
				Cell cell = findCellByName("Hdr" + entry.getKey());
				if (cell != null)
					writeCellValue(oCoverSheet, cell, entry.getValue()
							.toString());
			} catch (Exception e) {
				logger.warning("Hdr" + entry.getKey()
						+ ": failed to write this header: " + e);
			}
		}
	}

	/*
	 * Function: report(public) Goal:Reports an event
	 */
	private void report(String eventType, TestResult result, Object eventData) {
		logger.trace("Report:" + eventType + ":" + StringUtils.toString(result)
				+ ":" + StringUtils.toString(eventData));

		try {
			switch (result.entityType) {
			case ITERATION:
				if (eventType.equals("START")) {
					TestIteration iteration = (TestIteration) eventData;
					String iterName = "";
					Map iterParams = iteration.parameters();
					if (iterParams != null) {
						if (iterParams.containsKey("_rowId"))
							iterName = iterParams.get("_rowId").toString();
						if (iterName == null || iterName.equals("")) {
							iterName = "("
									+ result.parent.childCount
									+ " of "
									+ ((TestCase) result.parent.entityDetails)
											.iterationCount() + ")";
						}
					}
					String tcName = result.parent.entityName;
					testCases.add(tcName);
					if (iterName != null) {
						tcName = tcName + " " + iterName;
					}

					startTestCase(tcName, iteration.stepCount(), "",
							result.startTime); // ' Skip execStatus
				} else if (eventType.equals("FINISH")) {
					String msg = "";
					finishTestCase(result.msRsType.isPassed(), msg,
							result.finishTime, result.childCount, result);
				}
				break;
			case TESTSTEP:
				if (eventType.equals("START")) {
					TestStep testStep = (TestStep) eventData;
					startTestStep(result.parent.childCount, result.entityName,
							testStep.stepName(), result.startTime);
				} else if (eventType.equals("FINISH")) {
					finishTestStep(result.entityName,
							result.msRsType.isPassed(), result.startTime,
							result.finishTime);
				}
				break;
			case COMPONENT:
				if (eventType.equals("DETAILS")) {
					Map attibs = (Map) eventData;
					reportCheck(attibs.get("name").toString(), result, attibs,
							attibs.get("screenDump").toString());
				}
				break;
			}
		} catch (Exception e) {
			logger.handleError("Error in Excel reporting", e);
		}
	}

	private Object hyperlinked(Object main, String sheetName, int rowNum) {
		return new String[] { (String) main,
				sheetName + "!" + LINK_COL + rowNum };
	}

	private void startTestCase(String TestCaseName, int stepsCount,
			String execStatus, Date startTime) {
		Map data = Utils.toMap(new Object[] { "TestCase", TestCaseName,
				"Result",
				hyperlinked("In Progress", SHN_DETAILS, detailsRowNum),
				"StepsCount", stepsCount, "StartTime", startTime });
		if (!execStatus.equals("") && execStatus != "No Run") {
			data.put("ExecStatus", "*");
		}

		writeSumValues(data, false);

		writeDtlValues("TC", Utils.toMap(new Object[] { "Name", TestCaseName }));
	}

	private void finishTestCase(boolean isPassed, String msg, Date finishTime,
			int stepsRun, TestResult result) {
		String sStatus;
		String[] eventReport = { "", "" }, executionLog = { "", "" };
		if (((Map) result.miscInfo.get("EventReport")) != null) {
			eventReport[0] = (String) ((Map) result.miscInfo.get("EventReport"))
					.get("fileName");
			eventReport[1] = (String) ((Map) result.miscInfo.get("EventReport"))
					.get("filePath");
		}
		if (isPassed) {
			sStatus = "Passed";
		} else {
			sStatus = "Failed";
		}
		Map data = Utils.toMap(new Object[] { "Result", sStatus, "ErrMsg", msg,
				"FinishTime", finishTime, "StepsRun", stepsRun, "EventReport",
				eventReport, "ExectionLog", executionLog });
		writeSumValues(data, true);
		summaryRowNum = summaryRowNum + 1;
	}

	private void startTestStep(int stepNum, String stepName,
			String stepDescription, Date startTime) {
		Map data = Utils.toMap(new Object[] { "Ix", stepNum, "Name", stepName,
				"Description", stepDescription });
		writeDtlValues("StepHdr", data);
	}

	private void finishTestStep(String stepName, boolean isPassed,
			Date startTime, Date finishTime) {
		String sStatus;
		if (isPassed) {
			sStatus = "Passed";
		} else {
			sStatus = "Failed";
		}
		writeDtlValues(
				"StepFtr",
				Utils.toMap(new Object[] { "Name", stepName, "Result", sStatus,
						"StartTime", startTime, "FinishTime", finishTime }));
	}

	private void reportCheck(String checkName, TestResult result, Map details,
			String scrDumpPath) {
		String sExpected = "";
		Object sActual;
		try {
			sExpected = details.get("expected").toString();
			sActual = details.get("actual").toString();
		} catch (Exception e) {
			sExpected = "";
			sActual = "";
		}

		// ' Link screen dump
		if (scrDumpPath.equals("true")) {
			if (!sActual.equals("")) {
				String temp[] = {
						sActual + ". Click here to view screenshot.",
						((Map) result.miscInfo.get("screenDump"))
								.containsKey("filePath") ? (String) ((Map) result.miscInfo
								.get("screenDump")).get("filePath") : "" };
				sActual = temp;
			}

		}

		try {
			writeDtlValues(
					"Ck",
					Utils.toMap(new Object[] { "Name", checkName, "Result",
							result.finalRsType.toString(), "Expected",
							sExpected, "Actual", sActual, "Ts", new Date() }));
		} catch (Exception e) {
			logger.handleError(
					"Error while updating report with validation details", e);
		}
	}

	private void writeDtlValues(String tplType, Map data) {
		try {
			writeValuesFromTpl("Dtl" + tplType + "Row", "Dtl" + tplType, data,
					detailsRowNum, oDetailsSheet, true);
			detailsRowNum = detailsRowNum + 1;
		} catch (Exception e) {
			logger.handleError("Failed to write values in DETAILS sheet", e);
		}
	}

	private void writeSumValues(Map data, boolean bClearValues) {
		try {
			writeValuesFromTpl("SumRow", "Sum", data, summaryRowNum,
					oSummarySheet, bClearValues);
		} catch (Exception e) {
			logger.handleError("Failed to write values in SUMMARY sheet", e);
		}
	}

	private void writeValuesFromTpl(String tplRowName, String pfx, Map data,
			int targetRow, Sheet sheet, boolean bClearValues) {
		Set<Object> keys = data.keySet();
		for (Object key : keys) {

			try {
				Cell cell = findCellByName(pfx + key);
				if (cell != null)
					writeCellValue(sheet, cell, data.get(key));
			} catch (Exception e) {
				logger.handleError(
						"Template column:" + pfx + key + ":to:"
								+ data.get(key).toString() + ":didnt happen:"
								+ e.getMessage(), e);
			}
		}

		try {
			copyRow(tplRowName, targetRow, sheet);
		} catch (Exception e) {
			logger.handleError("Error while copying result details", e);
		}

		if (bClearValues) {
			try {
				clearContents(tplRowName, sheet);
			} catch (Exception e) {
				logger.handleError("Error while clearing template values", e);
			}
		}
	}

	private void writeCellValue(Sheet sheet, Cell cell, Object value) {
		try {
			DataFormatter dataFormatter = new DataFormatter();
			dataFormatter.formatCellValue(cell);

			CellStyle style = cell.getCellStyle();
			if (value.equals("Passed")) {
				cell.setCellValue((String) value);
				cell.setCellStyle(style);
			}
			if (value == "Failed") {
				cell.setCellValue((String) value);
				cell.setCellStyle(style);

			}
			if (value == null) {
				cell.setCellValue("--null--");
				return;
			}

			try { // try hyperlink
				String[] vals = (String[]) value;
				if (!vals[0].equals("") && !vals[1].equals(""))
					createHelper = newWorkbook.getCreationHelper();

				if (new File(vals[1]).isFile()) {
					Hyperlink link = createHelper
							.createHyperlink(Hyperlink.LINK_FILE);
					cell.setAsActiveCell();
					cell.setCellValue(String.valueOf(vals[0]));

					try {

						link.setAddress("file:///" + String.valueOf(vals[1]));
					} catch (Exception e) {
						logger.handleError(
								"Error while setting the link to screenshot", e);
					}
					cell.setHyperlink(link);
					cell.setCellStyle(style);

				} else {
					Hyperlink link = createHelper
							.createHyperlink(Hyperlink.LINK_DOCUMENT);
					// link to Detail sheet
					cell.setAsActiveCell();
					link.setAddress(String.valueOf(vals[1]));

					cell.setHyperlink(link);
					cell.setCellStyle(style);

				}
				return;
			} catch (ClassCastException e) {
			}

			try {
				Date today = (java.util.Date) value;
				cell.setCellValue(today);
				cell.setCellStyle(cell.getCellStyle());
				cell.setCellType(cell.getCellType());

				return;
			} catch (ClassCastException e) {
			}

			try {
				String pattern = "(\\d{1})|(\\d{2}) ";
				String value1 = value.toString();

				if (value1.matches(pattern)) {
					try {
						cell.setCellValue(Integer.parseInt(value1));
						cell.setCellType(0);
					} catch (Exception e) {
						cell.setCellValue(value.toString());
						cell.setCellStyle(style);
					}
				}

				else {

					cell.setCellValue(value.toString());
					cell.setCellStyle(style);

				}

				return;
			} catch (ClassCastException e) {
			}

		} catch (Exception e) {
			logger.handleError("Error while writing to cell", e);
			return;
		}

		logger.handleError("Cant write value of this type:", value,
				value.getClass());
	}

	private void copyRow(String tplRowName, int targetRow, Sheet sheet) {

		// retrieve the named range
		int namedCellIdx = newWorkbook.getNameIndex(tplRowName);
		Name aNamedCell = newWorkbook.getNameAt(namedCellIdx);

		// retrieve the cell at the named range and test its contents

		AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula());
		CellReference cellRef1 = aref.getFirstCell();
		Row r = sheet.getRow(cellRef1.getRow());
		Cell firstcell = r.getCell(cellRef1.getCol());

		CellReference cellRef2 = aref.getLastCell();
		Row r1 = sheet.getRow(cellRef2.getRow());
		Cell lastcell = r1.getCell(cellRef2.getCol());

		int startRow = firstcell.getRowIndex();
		int endRow = lastcell.getRowIndex();
		int startCol = firstcell.getColumnIndex();
		int endCol = lastcell.getColumnIndex();

		CellReference[] crefs = aref.getAllReferencedCells();

		List<CellRangeAddress> regionsList = new ArrayList<CellRangeAddress>();
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			regionsList.add(sheet.getMergedRegion(i));
		}

		for (int row = startRow; row <= endRow; row++) {
			for (int j = 0; j < regionsList.size(); j++) {

				CellRangeAddress mergedRegion = regionsList.get(j);

				if ((startRow < mergedRegion.getLastRow() || endRow > mergedRegion
						.getFirstRow()))
					continue;

				try {
					sheet.addMergedRegion(new CellRangeAddress(targetRow,
							targetRow, mergedRegion.getFirstColumn(),
							mergedRegion.getLastColumn()));
				} catch (Exception e) {
					logger.handleError("Error while merging cells", e);
				}

			}

			Row row1 = sheet.createRow(targetRow);
			try {
				for (int col = startCol; col <= endCol; col++) {

					Row r3 = sheet.getRow(crefs[col].getRow());
					Cell cell = r3.getCell(col);

					Cell newCell = row1.createCell(cell.getColumnIndex());
					copyCell(cell, newCell, row1, targetRow, sheet);
				}
			} catch (Exception e) {
				logger.handleError(
						"Error while copying updated result row to target row",
						e);
			}
		}
	}

	private void copyCell(Cell cell, Cell newCell, Row row, int targetRow,
			Sheet sheet) {

		// Copy style from old cell and apply to new cell
		CellStyle newCellStyle = newWorkbook.createCellStyle();
		newCellStyle.cloneStyleFrom(cell.getCellStyle());

		// If there is a cell comment, copy
		if (cell == null) {
			newCell.setCellValue("");
			return;
		}
		if (cell.getCellComment() != null)
			newCell.setCellComment(cell.getCellComment());

		// If there is a cell hyperlink, copy
		if (cell.getHyperlink() != null) {
			newCell.setHyperlink(cell.getHyperlink());
		}
		// Set the cell data type
		newCell.setCellType(cell.getCellType());

		// Set the cell data value

		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			newCell.setCellValue(cell.getStringCellValue());
			newCell.setCellStyle(newCellStyle);
			newCell.setCellType(3);
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(cell.getBooleanCellValue());
			newCell.setCellStyle(newCellStyle);
			newCell.setCellType(4);
			break;
		case Cell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(cell.getErrorCellValue());
			newCell.setCellStyle(newCellStyle);
			newCell.setCellType(5);
			break;
		case Cell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(cell.getCellFormula());
			newCell.setCellType(2);

			try {
				String prefix = "";
				if (sheet.getSheetName().equals("Details")) {
					prefix = "DtlStepFtr";
				} else if (sheet.getSheetName().equals("Summary")) {
					prefix = "Sum";
				}
				Cell refCell = findCellByName(prefix + "StartTime");
				CellReference cellRef = new CellReference(refCell);
				String s[] = cellRef.getCellRefParts();
				String startTimeCol = s[2] + (targetRow + 1);

				CellReference cellRef1 = new CellReference(
						findCellByName(prefix + "FinishTime"));
				String s1[] = cellRef1.getCellRefParts();
				String endTimeCol = s1[2] + (targetRow + 1);
				String formula = endTimeCol + "-" + startTimeCol;
				newCell.setCellFormula(formula);

			} catch (Exception e) {
				logger.handleError("Error while copying Date cell", targetRow,
						cell.getColumnIndex(), e);
			}

			newCell.setCellStyle(newCellStyle);
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				newCell.setCellType(cell.getCellType());
				newCell.setCellValue(cell.getDateCellValue());
				newCell.setCellStyle(newCellStyle);
			} else {
				newCell.setCellType(cell.getCellType());
				newCell.setCellValue(cell.getNumericCellValue());
				newCell.setCellStyle(newCellStyle);
			}
			break;
		case Cell.CELL_TYPE_STRING:
			newCell.setCellValue(cell.getStringCellValue());
			newCell.setCellStyle(newCellStyle);
			newCell.setCellType(1);
			break;
		default:
			newCell.setCellValue("");
			break;
		}
	}

	private void clearContents(String tplRowName, Sheet sheet) {

		int namedCellIdx = newWorkbook.getNameIndex(tplRowName);
		Name aNamedCell = newWorkbook.getNameAt(namedCellIdx);

		AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula());
		CellReference cellRef1 = aref.getFirstCell();
		Row r = sheet.getRow(cellRef1.getRow());
		Cell firstcell = r.getCell(cellRef1.getCol());

		CellReference cellRef2 = aref.getLastCell();
		Row r1 = sheet.getRow(cellRef2.getRow());
		Cell lastcell = r1.getCell(cellRef2.getCol());

		int startRow = firstcell.getRowIndex();
		int endRow = lastcell.getRowIndex();
		int startCol = firstcell.getColumnIndex();
		int endCol = lastcell.getColumnIndex();

		CellReference[] crefs = aref.getAllReferencedCells();

		try {
			for (int col = startCol; col <= endCol; col++) {

				Row r3 = sheet.getRow(crefs[col].getRow());
				Cell cell = r3.getCell(col);
				if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
					logger.trace("Do not clear cell content as cell has formula");
				} else {
					clearCell(cell, sheet);
				}
			}
		} catch (Exception e) {
			logger.handleError("Error while Clearing ", e);
		}
	}

	private void clearCell(Cell cell, Sheet sheet) {
		if (cell == null) {
			return;
		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			try {
				String value = cell.getStringCellValue();
				String pattern = "(\\d{2})(.*)";
				if (value.matches(pattern)) {
					cell.setCellValue("09/15/2015 13:31:32");
				} else {
					cell.setCellValue(" ");
				}
			} catch (Exception e) {
				logger.handleError("Error while clearing String cell", e);
			}
			break;
		case Cell.CELL_TYPE_NUMERIC:
			try {
				if (DateUtil.isCellDateFormatted(cell)) {
					cell.setCellValue(0.0);
				} else {
					DateFormat dateFormat = new SimpleDateFormat(
							"MM/dd/yyyy HH:mm:ss");
					Date today = (java.util.Date) (new Date("01-Jan-01"));
					cell.setCellValue(dateFormat.format(today));
				}
				cell.setCellValue(0.0);
			} catch (Exception e) {
				logger.handleError("Error while clearing Number cell", e);
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			try {
				cell.setCellValue(false);
			} catch (Exception e) {
				logger.handleError("Error while clearing Boolean cell", e);
			}
			break;
		case Cell.CELL_TYPE_BLANK:
			try {
				cell.setCellValue("");
			} catch (Exception e) {
				logger.handleError("Error while clearing Blank cell", e);
			}
			break;
		case Cell.CELL_TYPE_FORMULA:
			try {
				cell.setCellValue(0);
			} catch (Exception e) {
				logger.handleError("Error while clearing Formula cell", e);
			}
			break;
		}
	}

	private Cell findCellByName(String name) {

		int namedCellIdx = newWorkbook.getNameIndex(name);
		if (namedCellIdx == -1)
			return null;
		Name aNamedCell = newWorkbook.getNameAt(namedCellIdx);

		// retrieve the cell at the named range and test its contents
		AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula());
		CellReference[] crefs = aref.getAllReferencedCells();

		try {
			for (int i = 0; i < crefs.length; i++) {
				Sheet sheet = newWorkbook.getSheet(crefs[i].getSheetName());
				Row r = sheet.getRow(crefs[i].getRow());
				Cell cell = r.getCell(crefs[i].getCol());

				if (cell == null) {
					return null;

				} else if ((cell.getCellType() != Cell.CELL_TYPE_BLANK)
						|| !(cell == null)) {
					return cell;
				}
			}

		} catch (Exception e) {
			logger.handleError(
					"Error while copying updated result row to target row", e);
		}

		return null;
	}

	/*
	 * Function: evaluateFormulae(public) Goal: To Evaluate the formulae of
	 * formulae cells in excel Params: None
	 */

	private void evaluateFormulae() {
		FormulaEvaluator evaluator = newWorkbook.getCreationHelper()
				.createFormulaEvaluator();
		for (int sheetNum = 0; sheetNum < newWorkbook.getNumberOfSheets(); sheetNum++) {
			Sheet sheet = newWorkbook.getSheetAt(sheetNum);
			for (Row r : sheet) {
				for (Cell c : r) {
					if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
						evaluator.evaluateFormulaCell(c);

					}
				}
			}
		}

	}

	
	public static void toCreateMyAccountHomeMoverScenarioTestResultReport(int RowNumber,String CLI,String OrderID,String CommandName,String CommandID){
		Xls_Reader HomePagexls = new Xls_Reader(System.getProperty("user.dir")+"\\TestResults\\MyAccountAgentPortalHomeMover.xlsx");
		//HomePagexls.setCellData(sheetName, colName, rowNum, data)
		System.out.println("Entered into toCreateMyAccountHomeMoverScenarioTestResultReport.");
		HomePagexls.setCellData("Results", "CLI", RowNumber, CLI);
		HomePagexls.setCellData("Results", "ORDERID", RowNumber, OrderID);
		HomePagexls.setCellData("Results", "PROVCMDNAME", RowNumber,CommandName);
		HomePagexls.setCellData("Results", "CommandID", RowNumber, CommandID);
		//HomePagexls.setCellData("Results", "CommandID", 10, "Done");
	}
	
	public static void toCreatePlaceOrdersTestResultReport(int RowNumber,String OrderName,String CurrentDate,String APPOrderRef,String OrderID,String CommandName,String CommandID,String CLI){
		Xls_Reader HomePagexls = new Xls_Reader(System.getProperty("user.dir")+"\\TestResults\\Orders_AgentPortal.xlsx");
		//HomePagexls.setCellData(sheetName, colName, rowNum, data)
		System.out.println("Entered into toCreatePlaceOrdersTestResultReport.");
		HomePagexls.setCellData("Results", "OrderPlacedDate", RowNumber, CurrentDate);
		HomePagexls.setCellData("Results", "OrderName", RowNumber, OrderName);
		HomePagexls.setCellData("Results", "OrderReference", RowNumber, APPOrderRef);
		//HomePagexls.setCellData("Results", "OrderID", RowNumber,OrderID);
		HomePagexls.setCellData("Results", "PROVCMDNAME", RowNumber,CommandName);
		HomePagexls.setCellData("Results", "CommandID", RowNumber, CommandID);
		//HomePagexls.setCellData("Results", "CLI", RowNumber, CLI);
		
	}
	
	public static int toGetLastRowNumberofAgentPortalOrdersTestResults(String Sheetname) throws IOException{
		int rownum=0;
		InputStream myxls = new FileInputStream(System.getProperty("user.dir")+"\\TestResults\\Orders_AgentPortal.xlsx");
		Workbook book = new XSSFWorkbook(myxls);
		Sheet sheet = book.getSheet(Sheetname);
		//System.out.println(sheet.getLastRowNum());
		rownum = sheet.getPhysicalNumberOfRows();
		//int rowsNum = sheet.getLastRowNum();
		System.out.println("++++++++++++++++++++++++++++-------------- "  + rownum);
		return (rownum+1);
	}
	
	public static void toCreatePlaceOrdersReferenceNumber(String OrderName,String APPOrderRef,String Environment) throws IOException{
		Xls_Reader HomePagexls = new Xls_Reader(System.getProperty("user.dir")+"\\TestResults\\Orders_AgentPortal.xlsx");
		System.out.println("Entered into toCreatePlaceOrdersTestResultReport.");
		int RowNumber = toGetLastRowNumberofAgentPortalOrdersTestResults("OrderDetails");
		HomePagexls.setCellData("Results", "OrderPlacedDate", RowNumber, currentDate());
		HomePagexls.setCellData("OrderDetails", "OrderName", RowNumber, OrderName);
		HomePagexls.setCellData("OrderDetails", "OrderReference", RowNumber, APPOrderRef);		
		HomePagexls.setCellData("OrderDetails", "Environment", RowNumber, Environment);		
	}
	
	
	
	
	public static void  toSetLLUCMDIDfromGivenRowNumber(int rownumber,String LLUCMDID) throws Exception{
		Xls_Reader HomePagexls = new Xls_Reader("C:\\SoapUI\\TestData Automation\\TestData\\LLUKCI.xls");
		HomePagexls.setCellData("Sheet1", "LLUCommandID", rownumber,LLUCMDID);				
	}
	
	public static void toSetNGACMDIDfromGivenRowNumber(int rownumber,String NGACMDID){
		
		Xls_Reader HomePagexls = new Xls_Reader("C:\\SoapUI\\TestData Automation\\TestData\\LLUKCI.xlsx");
		HomePagexls.setCellData("Sheet1", "NGACommandID", rownumber,NGACMDID);				
	}
	
	public static void toSetCRDfromGivenRowNumber(int rownumber,String CRD){
		
		Xls_Reader HomePagexls = new Xls_Reader("C:\\SoapUI\\TestData Automation\\TestData\\LLUKCI.xlsx");
		HomePagexls.setCellData("Sheet1", "CRDDate", rownumber,CRD);				
	}
	
	public static String toGetCMDIDfromGivenRowNumber(int rownumber){
		String CMDID="";
		Xls_Reader HomePagexls = new Xls_Reader(System.getProperty("user.dir")+"\\TestResults\\Orders_AgentPortal.xlsx");
		CMDID = HomePagexls.getCellData("Results", "CommandID", rownumber);
		return CMDID;		
	}
	
	public static String toGetCMDNAMEfromGivenRowNumber(int rownumber){
		String CMDNAME="";
		Xls_Reader HomePagexls = new Xls_Reader(System.getProperty("user.dir")+"\\TestResults\\Orders_AgentPortal.xlsx");
		CMDNAME = HomePagexls.getCellData("Results", "PROVCMDNAME", rownumber);
		return CMDNAME;		
	}
	
	public static String toSetDatafromGivenRowNumber(int rownumber){
		String CMDNAME="";
		Xls_Reader HomePagexls = new Xls_Reader(System.getProperty("user.dir")+"\\TestResults\\Orders_AgentPortal.xlsx");
		CMDNAME = HomePagexls.getCellData("Results", "PROVCMDNAME", rownumber);
		return CMDNAME;		
	}
	
	public static void printCustomerList(String LLUCMDID,String NGACMDID,String CRDdate) throws Exception{
		FileOutputStream fo = new FileOutputStream("C:\\SoapUI\\TestData Automation\\TestData\\LLUKCI.xls");
		WritableWorkbook ws = jxl.Workbook.createWorkbook(fo);
		//Sheet sh = ws.getSheet("Sheet1");
		WritableSheet sh1 = ws.createSheet("Sheet1", 0);
		Label l4 = new Label(0,0,"LLUCommandID");
		Label l5 = new Label(1,0,"NGACommandID");
		Label l6 = new Label(2,0,"CRDdate");
		Label l1 = new Label(0,1,LLUCMDID);
		Label l2 = new Label(1,1,NGACMDID);
		Label l3 = new Label(2,1,CRDdate);
		sh1.addCell(l4);
		sh1.addCell(l5);
		sh1.addCell(l6);
		sh1.addCell(l1);
		sh1.addCell(l2);
		sh1.addCell(l3);
		ws.write();
		ws.close();
	}
	
	
	public static String currentDate()
	{
		String curDate=null;
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy") ;
		curDate =dateFormat.format(date);
		return curDate;
	}
	
	
	
	
	
	
	
	
	
	
	private Map toMap(Object[] arr) {
		return Utils.toMap(arr);
	}

	/**
	 * Overrides toString() method of Object class to return ExcelReporter
	 * format string
	 */
	public String toString() {

		return StringUtils.mapString(this, filePath);
	}

	private static enum cellType {
		EMPTY, LABEL, NUMBER, NUMERICALFORMULA, DATE, DATEFORMULA,
	};

	private String SHN_SUMMARY, SHN_DETAILS, SHN_COVER, LINK_COL;
	private String filePath, templatePath;
	private LogUtils logger = new LogUtils(this);

}
