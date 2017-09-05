/******************************************************************************
$Id : TestCaseRunner.java 3/3/2016 6:00:13 PM
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

package cbf.engine;

import static cbf.engine.TestResultLogger.*;
import cbf.engine.TestResult.ResultType;
import cbf.engine.TestResultTracker.Trackable;
import cbf.harness.ParameterAccess;
import cbf.model.AppDriver;
import cbf.model.TestCase;
import cbf.model.TestIteration;
import cbf.model.TestStep;
import cbf.utils.DataRow;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbf.utils.Utils;


/**
 * Executes TestCase i.e, steps and returns TestResult object to tracker for reporting
 * 
 */
public class TestCaseRunner {
	TestResultTracker oResultsTracker;
	AppDriver oAppDriver;
	private ParameterAccess paramsAccess;
	private LogUtils logger = new LogUtils(this);

	private BuiltinComponentsDriver biComponentsDriver;

	/**
	 * Constructor to initialize AppDriver, TestResultTracker and
	 * TestResultLogger objects
	 * 
	 * @param oAppDriver
	 *            AppDriver object necessary for execution
	 * @param oResultTracker
	 *            TestResultTracker object for tracking execution results
	 */
	public TestCaseRunner(AppDriver oAppDriver,
			TestResultTracker oResultTracker) {
		this.oAppDriver = oAppDriver;
		this.oResultsTracker = oResultTracker;
	}

	/**
	 * Deserializes the test file, resolves references and makes executable TestCase object
	 */
	public interface TCMaker {

		/**
		 * Makes TestCase object
		 * 
		 * @return TestCase object
		 * @throws Exception
		 *             Occurred in making TestCase object
		 */
		public TestCase make() throws Exception;
	}

	/**
	 * Executes TestCase
	 * 
	 * @param tcMaker
	 *            TCMaker object
	 * @param name
	 *            TestCase name
	 * @return call to track function with trackable object, entityType, entityName and entity object
	 */
	public TestResult runTestCase(final TCMaker tcMaker, String name) {
		return oResultsTracker.track(new Trackable() {
			public void run(TestResult result) throws Exception {
				TestCase oTestCase;
				try {
					oTestCase = tcMaker.make();
				} catch (Exception e) {
					error("RunTestCase():", e.toString(),
							"Initialization error in test case:" + e);
					return;
				}

				// fire an artificial log event with the deserialized testCase
				oResultsTracker.log(ResultType.DONE, oTestCase);

				paramsAccess = new ParameterAccess();
				biComponentsDriver = new BuiltinComponentsDriver(paramsAccess);
				paramsAccess.declareVariables(oTestCase);
				oResultsTracker.addReporter(paramsAccess);

				int iterationCount = oTestCase.iterationCount();
				for (int i = 0; i < iterationCount; i++) {
					TestResult iterRs = runIteration(oTestCase.iteration(i));
					if (iterRs.miscInfo.containsKey("abortRs")) {
						try {
							logger.trace("componentDriver.recover()");
							//oAppDriver.recover();
						} catch (Exception e) {
							logger.handleError("Iteration recovery failed:", oTestCase ,e.getMessage());
						}
					}
				}
			}
		}, TestResult.EntityType.TESTCASE, name, tcMaker);
	}

	/*
	* Run test case for the given iteration
	*/
	private TestResult runIteration(final TestIteration iteration) {
		return oResultsTracker.track(new Trackable() {
			public void run(TestResult result) throws Exception {
				int stepCount = iteration.stepCount();
				for (int stepIx = 0; stepIx < stepCount; stepIx++) { // run each
					// step

					TestStep oTestStep = iteration.step(stepIx);
					TestResult stepRs = runStep(oTestStep);

					if (stepRs.miscInfo.containsKey("abortRs")) {
						result.miscInfo.put("abortRs", stepRs);
						logger.trace("aborting at:" + stepIx);
						break;
					}
				} 
			}
		}, TestResult.EntityType.ITERATION, "#IterIx#", iteration);
	}

	/**
	 * Executes TestStep
	 * 
	 * @param oTestStep
	 *            TestStep object
	 * @return call to track function with trackable object, entityType, entityName and entity object
	 */
	public TestResult runStep(final TestStep oTestStep) {
		return oResultsTracker.track(new Trackable() {
			public void run(TestResult result) throws Exception {
				DataRow input = oTestStep.componentParameters();
				if (paramsAccess != null) { // CHECKE: (DataRow) casting
					input = Utils.Map2DataRow(paramsAccess.resolve(input));
				}

				DataRow output = new DataRow();
				TestResult componentRs = runComponent(oTestStep, input, output);
				ResultType componentRsType = componentRs.finalRsType;

				/*
				 * TODO: add outputValidation code here. Enhancement... if
				 * (componentRsType.isPassed()) { // ' Component was performed
				 * properly 'if did not match then componentRsType =
				 * ResultType.FAILED;
				 *///

				//
				boolean bAbortTest = false, bFailTest = false;
				if (!componentRsType.isPassed()) {
					bAbortTest = oTestStep.abortTestIfUnexpected();
					bFailTest = oTestStep.failTestIfUnexpected();

					// indication: unexpected result was ignored as per flag
					String warnMsg = null;
					if (!bFailTest) {
						warnMsg = "Step Failed";
						// FIXME: This causes even some earlier failure also to
						// be ignored
						result.finalRsType = ResultType.WARNING;
					}

					if (bAbortTest) {
						result.miscInfo.put("abortRs", componentRs);
					} else {
						if (warnMsg == null) {
							warnMsg = "Execution continued";
						} else {
							warnMsg = " and execution continued";
						}
					}

					if (warnMsg != null) {
						warning("", "", warnMsg
								+ " despite unexpected result in component");
					}
				}
			}
			
		}, TestResult.EntityType.TESTSTEP, oTestStep.stepName(), oTestStep);
	}

	/**
	 * Executes component functionality
	 * 
	 * @param oTestStep
	 *            TestStep with step details and component details like module code, component code, parameters etc
	 * @param input
	 *            DataRow of input parameters
	 * @param output
	 *            empty DataRow passed to capture any runtime output during execution of component
	 * @return call to track function with trackable object, entityType, entityName and entity object
	 */
	public TestResult runComponent(final TestStep oTestStep, final DataRow input,
			final DataRow output) {
		final String moduleCode = oTestStep.moduleCode();
		final String componentCode = oTestStep.componentCode();
		return oResultsTracker.track(new Trackable() {
			public void run(TestResult result) throws Exception {
				result.miscInfo.put("input", input);

				try {
					performComponent(moduleCode, componentCode, input, output);
					result.miscInfo.put("output", output);
				} catch (Exception e) {
					error("", "", "Error during perform:"
							+ StringUtils.toString(e));
				}
			}
		}, TestResult.EntityType.COMPONENT, moduleCode + "-" + componentCode,
		oTestStep);
	}

	private void performComponent(String moduleCode, String componentCode,
			DataRow input, DataRow output) throws Exception {
		// TODO: Support for built-in _FW module
		logger.trace("performComponent()");
		logger.trace("appdriver.perform( "+moduleCode+"_"+componentCode+"_"+StringUtils.toString(input).replace(",", ";")+" ) ");
		if (moduleCode.equals("_FW"))
			biComponentsDriver.perform(componentCode, input, output);

		else
			oAppDriver.perform(moduleCode, componentCode, input, output);

	}

	/**
	 * Returns TestCaseRunner format string
	 */
	public String toString() {
		return StringUtils.mapString(this,oAppDriver,oResultsTracker);
	}
}
