/******************************************************************************
$Id : TestResult.java 3/3/2016 6:00:14 PM
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

/**
 * Defines the result object structure with the details like entityType, can be
 * TESTSUITE, TESTCASE, ITERATION, TESTSTEP etc entityName, actual value of the
 * entity like tc name, iter name, step name etc entityDetails, actual entity
 * object which gives complete details of entity finalRsType, final result of
 * the executed entity startTime, execution start time finishTime, execution
 * finish time msRsType, most significant result msDetails, most significant
 * result's details
 */
public class TestResult {

	/**
	 * 
	 * Defines fixed set of entity types used
	 * 
	 */
	public static enum EntityType {
		RUN("RUN"), TESTSUITE("TESTSUITE"), TESTCASE("TestCase"), ITERATION(
				"Iteration"), TESTSTEP("TestStep"), COMPONENT("Component");

		public final String name;

		/**
		 * Overrides toString() method of Object class to return EntityType name
		 */
		public String toString() {
			return name;
		}

		private EntityType(String n) {
			name = n;
		}
	};

	/**
	 * 
	 * Defines set of result types used
	 * 
	 */
	public static enum ResultType {
		PASSED("PASSED", 1), FAILED("FAILED", 3), WARNING("WARNING", 2), ERROR(
				"ERROR", 4), DONE("DONE", 0);

		public final String name;

		/**
		 * Returns ResultType name
		 */
		public String toString() {
			return name;
		}

		public final int level;

		/**
		 * Returns true/false depending on TestCase passed or failed
		 * 
		 * @return true/false
		 */
		public boolean isPassed() {
			return level <= WARNING.level;
		}

		private ResultType(String n, int l) {
			name = n;
			level = l;
		}
	}

	public final EntityType entityType;
	public String entityName; // removed final as values are changed afterwards
	public Object entityDetails; /*
								 * could be changed TestCase object is set after
								 * serialization and well after "start" of
								 * TestCase execution
								 */

	// Parent relationships
	public final TestResult parent;
	public int index;// removed final // index of this object as a child of the
	// parent
	public int value;
	// Child relationships: predominanrly summarized
	public int childCount = 0;
	public final Map<ResultType, Integer> rsTypeCounts = new HashMap<ResultType, Integer>();
	public ResultType msRsType = ResultType.DONE; // most significant result
	public Object msDetails = null; // most significant result's details
	// this is either a child TestResult object or the Map() generated by
	// ResultLogger

	// final result for this entity
	// This is usually updated by add() method below
	// But can be reset explicitly by the runners: e.g. false for
	// FailTestIfUnexpected
	public ResultType finalRsType = ResultType.DONE;

	public Date startTime, finishTime;

	public final Map miscInfo = new HashMap();

	/**
	 * Constructor to initialize TestResult, EntityType and entityDetails
	 * objects
	 * 
	 * @param parent
	 *            parent object for the current entity
	 * @param entityType
	 *            entityType of the current entity like TESTCASE, TESTSTEP etc
	 * @param entityName
	 *            name of entity
	 * @param entityDetails
	 *            entity details object
	 */
	public TestResult(TestResult parent, EntityType entityType,
			String entityName, Object entityDetails) {
		this.entityType = entityType;
		this.entityName = entityName;
		this.entityDetails = entityDetails;

		this.parent = parent;
		this.index = (parent == null ? -1 : this.parent.childCount++);
	}

	/**
	 * Updates TestResult object for msRsType, msDetails etc with the current
	 * execution check details
	 * 
	 * @param rsType
	 *            result type of the current check like PASSED, ERROR etc
	 * @param checkDetails
	 *            check details while execution of component
	 */
	public void add(ResultType rsType, Map checkDetails) {
		add(rsType, (Object) checkDetails);
	}

	/**
	 * Updates TestResult object for msRsType, msDetails etc with the current
	 * execution child results
	 * 
	 * @param rsType
	 *            result type of the current child node under execution like
	 *            PASSED, ERROR etc
	 * @param childResult
	 *            current child object
	 */
	public void add(ResultType rsType, TestResult childResult) {
		add(rsType, (Object) childResult);
	}

	private void add(ResultType rsType, Object details) {
		int rsTypeCount = 0;
		/*
		 * boolean rsT=rsTypeCounts.isEmpty(); if(rsT==false){
		 * System.out.println("In if->"+rsTypeCounts);
		 * System.out.println(rsType); rsTypeCount = rsTypeCounts.get(rsType); }
		 */
		try {
			rsTypeCount = rsTypeCounts.get(rsType);
		} catch (NullPointerException e) {
		}
		rsTypeCounts.put(rsType, rsTypeCount + 1);
		value = rsTypeCounts.get(rsType);
		if (msRsType.level <= rsType.level) {
			msRsType = rsType;
			msDetails = details;
		}
		finalRsType = rsType;
	}

	/**
	 * Overriding toString() method to return TestResult string
	 */
	public String toString() {
		return StringUtils.mapString(this);
	}

	private LogUtils logger = new LogUtils(this);
}
