/******************************************************************************
$Id : ScreenDumpManager.java 3/3/2016 6:00:47 PM
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

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import cbf.engine.TestResult;
import cbf.engine.TestResult.EntityType;
import cbf.engine.TestResult.ResultType;
import cbf.harness.ResourcePaths;
import cbf.model.ResultReporter;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbf.utils.UniqueUtils;
import cbf.utils.Utils;
import cbfx.selenium.BaseWebAppDriver;
import cbfx.selenium.WebUIDriver;

/**
 * 
 * Implements ResultReporter and takes screenshots
 * 
 */
public class ScreenDumpManager implements ResultReporter {
	public LogUtils logger = new LogUtils(this);

	/**
	 * Constructor to initialize folder path
	 * 
	 * @param params
	 *            map containing parameters
	 */
	public ScreenDumpManager(Map params) {
		this.params = params;
		dumpFolder = (String) params.get("folderpath");
		if (dumpFolder.equals("")) {
			dumpFolder = ResourcePaths.getInstance().getRunResource(
					"ScreenShots", "");
		}
		if (!cbf.utils.FileUtils.makeFolder(dumpFolder)) {
			logger
					.handleError(
							"Cant create/access ScreenShots folder; these will not be generated: ",
							dumpFolder);
		}
	}

	/**
	 * Closes the dump manager
	 */
	public void close() {
	}

	/**
	 * Reporter open method
	 * 
	 * @param headers
	 *            contains header info, like run name, config details etc
	 */
	public void open(Map headers) {
	}

	/**
	 * Reporter finish method
	 * 
	 * @param result
	 *            execution details
	 * @param rsType
	 *            result type of the current executed entity
	 * @param details
	 *            execution details of the current executed entity
	 */
	public void finish(TestResult result, ResultType rsType, Object details) {
	}

	/**
	 * Logs Screenshot
	 * 
	 * @param result
	 *            entity details
	 * @param rsType
	 *            result type of the current executed entity
	 * @param details
	 *            execution details of the current executed entity
	 */
	public void log(TestResult result, ResultType rsType, Map details) {
		logger.trace("Report log");
		if (!isScreenDump(rsType, result, details)) {
			return;
		}
		//
		// logger.trace(details.containsKey("screenDump"));
		// logger.trace(details.get("screenDump"));
		//
		if (details.get("screenDump").toString().equalsIgnoreCase("true")) {

			String dumpName = makeDumpName(result, details);
			String fileName = dumpName + ".png";
			filePath = dumpFolder + "/" + fileName;
			dumpScreen(filePath);
			result.miscInfo.put("screenDump", Utils.toMap(new String[] {
					"name", dumpName, "fileName", fileName, "filePath",
					filePath }));
		}
	}

	/**
	 * Starts the screenshot process
	 * 
	 * @param result
	 *            object of TestResult
	 */
	public void start(TestResult result) {
	}

	/*
	 * private void dumpScreen(String fileName) { logger.trace("DumpScreen : " ,
	 * fileName); Rectangle screenRectangle = new
	 * Rectangle(Toolkit.getDefaultToolkit() .getScreenSize()); Robot robot =
	 * null; try { robot = new Robot(); } catch (AWTException e) {
	 * logger.handleError("Exception in ScreenDumpManager " , e); }
	 * BufferedImage image = robot.createScreenCapture(screenRectangle); try {
	 * ImageIO.write(image, "png", new File(fileName)); } catch (IOException e)
	 * { logger.handleError("Exception caught while creating screen dump",
	 * fileName, e); } }
	 */

	private void dumpScreen(String fileName) {
		logger.trace("DumpScreen : " + fileName);
		//Rectangle screenRectangle = new Rectangle(Toolkit.getDefaultToolkit()
			//	.getScreenSize());
		//Robot robot = null;
		try {
			
			File file = ((TakesScreenshot)WebUIDriver.webDr).getScreenshotAs(OutputType.FILE); 
        	FileUtils.copyFile(file, new File(fileName));
			
			
			//robot = new Robot();
		} catch (Exception e) {
			logger.handleError("Exception in ScreenDumpManager "
					+ e.getMessage());
		}

		//File ss = BaseWebAppDriver.uiDriver.takescreenshot(); // To-Do : remove
		// BaseWebAppDriver
		// from here
		//BufferedImage image = robot.createScreenCapture(screenRectangle);
		/*try {
			FileUtils.copyFile(ss, new File(fileName));

			// ImageIO.write(image, "png", new File(fileName));
		} catch (IOException e) {
			logger.handleError("Exception caught while creating screen dump",
					fileName, e.getMessage());
		}*/
	}

	private String makeDumpName(TestResult result, Object eventData) {
		String name;
		name = result.parent.parent.parent.entityName;
		name = name + "_" + result.parent.entityName;// step
		name = name + "_" + result.childCount; // check#. Makes sure that sName
		// is unique, even when
		// checkName isnt
		uniqueSuffix = UniqueUtils.getInstance().uniqueString("3");
		name = name + params.get("pattern") + "_" + uniqueSuffix;

		// ' blank out suspicious characters
		Pattern pattern = Pattern.compile("[^\\w-_]+");
		Matcher m = pattern.matcher(name);
		while (m.find()) {
			m.replaceAll(" ");
		}
		return name;
	}

	// ' Determine if screen dump is needed
	private boolean isScreenDump(ResultType eventType, TestResult result,
			Map eventData) {
		if (result.entityType != EntityType.COMPONENT)
			return false;
		boolean isPassed = result.finalRsType.isPassed();
		if (!isPassed || result.finalRsType == ResultType.PASSED) { // ' Not
			// passed or
			// warning
			return true; // ' Enable screen dump for all failed logs
		}

		return (Boolean) eventData.get("screenDump");
	}

	/**
	 * Returns ScreenDumpManager along with screendump file path format string
	 */
	public String toString() {
		return StringUtils.mapString(this, params);
	}

	private String dumpFolder;
	private String filePath;
	String uniqueSuffix;
	private Map params;
}
