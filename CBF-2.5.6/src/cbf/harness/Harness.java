/******************************************************************************
$Id : Harness.java 3/3/2016 6:00:21 PM
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

package cbf.harness;

import java.io.FileNotFoundException;
import java.util.Map;

import cbf.engine.Engine;
import cbf.engine.TestResult;
import cbf.engine.TestResultTracker;
import cbf.engine.TestCaseRunner.TCMaker;
import cbf.model.AppDriver;
import cbf.plugin.ReportingManager;
import cbf.utils.Configuration;
import cbf.utils.FileUtils;
import cbf.utils.LogUtils;
import cbf.utils.Substitutor;

/**
 * 
 * Main class for Running Engine from Harness. Defines the runTestCase()
 * function.
 * 
 */
public class Harness {
	public static ResourcePaths resourcePaths;
	public static Configuration GCONFIG;
	public static AppDriver appDriver;

	/**
	 * Initializes logs, reporters in the results folder
	 * 
	 * @param runMap
	 * @param runName
	 */
	public Harness(Map<String, String> runMap, String runName) {
		logger.trace(runMap.get("configFilePath"), runName);

		loadConfiguration(runMap.get("configFilePath"));
		substitute(runMap);
		initializeResourcePaths();
		makeReporter();
		loadAppDriver();
		loadEngine(runName);
	}

	/**
	 * Alternate constructor. runName defaults to 'Dummy'
	 * 
	 * @param runMap
	 */
	public Harness(Map<String, String> runMap) {
		this(runMap, "Dummy");
	}

	/**
	 * Executes test and returns result
	 * 
	 * @param instanceName
	 *            name of Instance
	 * @return Result message
	 */
	public TestResult runTest(TCMaker tcMaker, String instanceName) {
		return engine.runTestCase(tcMaker, instanceName);
	}

	/**
	 * Finalize method to call garbage collector
	 */
	public void finalize() {
		//reportingManager.close();
		//appDriver.recover(); // TODO : remove it from here
	}

	/**
	 * adds reporter to the tracker report list
	 */
	public void addTracker(TestResultTracker.Observer reporter) {
		tracker.addReporter(reporter);
	}

	private void loadAppDriver() {
		try {
			appDriver = new AppLoader().loadApp(GCONFIG);
		} catch (Exception e) {
			logger.handleError("Failed to LoadAppDriver ", e);
		}
	}

	private void loadEngine(String runName) {
		try {
			tracker = new TestResultTracker(reportingManager.getReporters());
			engine = new Engine(runName, (AppDriver) appDriver, tracker);
		} catch (Exception e) {
			logger.handleError("Failed to load engine: ", e);
		}
	}

	private void initializeLogs(String resultsFolder) {
		logger.trace("InitializeLogs(" + resultsFolder + ")");
		logger.initialize(resultsFolder, ResourcePaths.getInstance()
				.getFrameworkResource("Resources", "logConfig.xml") );
	}

	private void makeReporter() {
		// Add desired Reporters using ManageReporters
		Object reporterObj = GCONFIG.get("ResultReporter");

		reportingManager = new ReportingManager(reporterObj, GCONFIG);
	}

	private void loadConfiguration(String configFileName) {
		try {
			GCONFIG = new Configuration(configFileName);

		} catch (FileNotFoundException e) {
			logger.handleError("Configuration file not exist ", configFileName,
					e);
		}
	}

	private void substitute(Map<String, String> runMap) {
		GCONFIG.set(new Substitutor(runMap).substitute(GCONFIG
				.getAllProperties()));
		// TODO : Substitute Map is returning as a map while GCONFIG is used
		// everywhere that is an object of Configuration class.
	}

	private void initializeResourcePaths() {
		String workHome = null,runHome = null, autoHome = null;
		try{
			workHome = (String) GCONFIG.get("WorkHome");
			autoHome = (String) GCONFIG.get("AutoHome");
			runHome = (String) GCONFIG.get("RunHome");
		}catch(Exception e){
			System.out.println("Please check the config files for workHome, autoHome and runHome");
		}
		
		logger.trace("AutoHome:", autoHome);
		if (autoHome == null || autoHome.equals("")) {
			logger.handleError("AUTO_HOME is invalid:", autoHome);
			return;
		} // TODO: handle invalid folder. Defaulting possibilities

		if (runHome == null || !(FileUtils.makeFolder(runHome))) {
			logger.handleError("Can't create/access run home folder: ", runHome);

		}

		if (workHome == null || !(FileUtils.makeFolder(workHome))) {
			logger.handleError("Can't access work home folder: ", workHome);
		}
		resourcePaths = ResourcePaths.getInstance(autoHome, workHome, runHome);

		initializeLogs(runHome);
	}

	private LogUtils logger = new LogUtils(this);
	private Engine engine = null;
	private ReportingManager reportingManager;
	private TestResultTracker tracker;
}
