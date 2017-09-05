/******************************************************************************
$Id : ParameterAccess.java 3/3/2016 6:00:21 PM
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cbf.engine.TestResult;
import cbf.engine.TestResultTracker;
import cbf.engine.TestResult.EntityType;
import cbf.engine.TestResult.ResultType;
import cbf.model.TestCase;
import cbf.model.TestStep;
import cbf.utils.DataRow;
import cbf.utils.LogUtils;
import cbf.utils.PersistentMap;
import cbf.utils.Substitutor;
import cbf.utils.UniqueUtils;

/**
 * 
 * Implements interface TestResultTracker.Reporter and provides access to
 * parameters
 * 
 */
public class ParameterAccess implements TestResultTracker.Observer {

	static Map<EntityType, String> entityType2Scope = new HashMap() {
		{
			put(EntityType.TESTCASE, "T");
			put(EntityType.ITERATION, "I");
		}
	};

	/**
	 * Copies parameters
	 * 
	 * @param inputGroupName
	 *            name of input group parameters
	 * @param outputGroupName
	 *            name of output group parameters
	 */
	public void copyRecentParameters(String inputGroupName,
			String outputGroupName) {
		int ix = 0;
		GroupInfo info;
		String[] groupNames = { inputGroupName, outputGroupName };
		for (String groupName : groupNames) {
		
			if (groupName == null) {
				ix = ix + 1;
				continue;
			}

			// FIXME: copy contents group to groups[groupName]
			info = groups.get(groupName);
			if (info == null) {
				logger.handleError("Invalid group name: ", groupName
						+ ": Declare group before use.");
				return;

			}

			Map group = info.values;
			Map params = mostRecentparameters[ix];
			if (params != null) {
				Collection<String> keys = params.keySet();
				for (String key : keys) {
					if (group.containsKey(key))
						group.remove(key);
					group.put(key, params.get(key));
				}
			}
			ix = ix + 1;
			groups.put(groupName, new GroupInfo(info.name, info.scope, group));
			nestedParams.put(groupName, group);
		}
	}

	/**
	 * Starts execution of TestCase
	 * 
	 * @param result
	 *            Object of TestResult
	 */
	public void start(TestResult result) {
		switch (result.entityType) {
		case TESTCASE:
			if (!(result.entityDetails instanceof TestCase))
				/*
				 * postpone till first iteration. TestCase object is not fully
				 * formed
				 */
				return;
			break;
		case ITERATION:
			if (!entities.containsKey(TestResult.EntityType.TESTCASE))
				/*
				 * mimic start of test case. Postponed as above.
				 */
				start(result.parent);
		}

		entities.put(result.entityType, result.entityDetails);

		String scope = entityType2Scope.get(result.entityType);
	}

	/**
	 * Sets Variables scope
	 * 
	 * @param scope
	 *            variables scope
	 */
	public void setGroupsInScope(String scope) {
		Map<String, List<GroupInfo>> temp = new HashMap();
		for (String key : groups.keySet()) {
			GroupInfo info = groups.get(key);
			if (info.scope.equals(scope)) {
				if (temp.containsKey(scope)) {
					List<GroupInfo> groupList = temp.get(scope);
					groupList.add(info);
					temp.put(scope, groupList);
				} else {
					temp.put(key, Arrays.asList(info));
				}
			}
		}

	}

	/**
	 * Logs the details in logger
	 * 
	 * @param result
	 *            object of TestResult
	 * @param rsType
	 *            object of ResultType
	 * @param details
	 *            Map containing details
	 */
	public void log(TestResult result, ResultType rsType, Map details) {
	}

	/**
	 * Finishes TestCase execution
	 * 
	 * @param result
	 *            object of TestResult
	 * @param rsType
	 *            object of ResultType
	 * @param details
	 *            object containing details
	 */
	public void finish(TestResult result, ResultType rsType, Object details) {
		String scope = entityType2Scope.get(result.entityType);
		if (scope != null)
			resetGroupsInScope(scope);

		switch (result.entityType) {
		case TESTCASE:
			destroyVariables();
			break;
		case ITERATION:
			nestedParams.remove("INPUT");
			nestedParams.remove("OUTPUT");
			((Map) nestedParams.get("INPUTS")).clear();
			((Map) nestedParams.get("OUTPUTS")).clear();
			break;
		case COMPONENT:
			saveComponentParameters((TestStep) result.entityDetails,
					(Map) result.miscInfo.get("input"), (Map) result.miscInfo
							.get("output"));
		}
		entities.remove(result.entityType);
	}

	/**
	 * Declares TestCase variables
	 * 
	 * @param testCase
	 *            object of TestCase
	 */
	public void declareVariables(TestCase testCase) {
		if (testCase.variables() == null) {
			return;
		}
		List<Map> variables = testCase.variables();
		for (Map variable : variables) {
			addGroup((String) variable.get("name"), (String) variable
					.get("scope"));
		}
	}

	// ' Resolve references in an object
	// ' Any combo of Dictionary/Array/String etc can be passed as parameter

	/**
	 * Resolves map data
	 * 
	 * @param data
	 *            Map containing data
	 */
	public Map resolve(Map data) {
		try {
			return new Substitutor(nestedParams).substitute(data);
		} catch (Exception e) {
			logger.handleError("Exception : ", e);
		}
		return data;
	}

	/**
	 * Returns entity
	 * 
	 * @param enType
	 *            object of EntityType
	 * @return entity object
	 */
	public Object getEntity(EntityType enType) {
		return entities.get(enType);
	}


	private void destroyVariables() {
		// destroy all variables in scope 'T' or 'I'
		// Keep G variables
		for (String key : groups.keySet()) {
			GroupInfo info = groups.get(key);
			if (!(info.scope.equals("G"))) {
				info.values.remove(key);
			}
		}
	}

	private GroupInfo addGroup(String groupName, String scope) {
		if (groups.containsKey(groupName)) {
			logger.handleError("Duplicate variable names: ", groupName);
			return null;
		}
		if ((scope == null ? "" : scope).equals("")) {
			scope = "I";
		}

		Map group = null;
		switch (scope.charAt(0)) {
		case 'G':
			String globalDataFolder = ResourcePaths.getInstance().getRunResource("Params", "");
			group = new PersistentMap(groupName,globalDataFolder);
			break;
		case 'I':
			group = new DataRow();
			break;
		case 'T':
			group = new DataRow();
			break;
		default:
			logger.handleError("Invalid scope: " + scope + " : for variable: "
					+ groupName);
			break;
		}
		GroupInfo info = new GroupInfo(groupName, scope, group);
		groups.put(groupName, info);
		nestedParams.put(groupName, group);// ' make it available for resolution
		return info;
	}

	private void resetGroupsInScope(String scope) {
		Collection<String> keys = groups.keySet();
		for (String key : keys) {
			GroupInfo info = groups.get(key);
			if (info.scope.equals(scope)) {
				info.values.clear();
			}
		}
	}

	private void saveComponentParameters(TestStep step, Map input, Map output) {
		nestedParams.put("INPUT", input);
		nestedParams.put("OUTPUT", output);
		mostRecentparameters = new DataRow[] { (DataRow) input,
				(DataRow) output };
		((Map) nestedParams.get("INPUTS")).put(step.moduleCode() + "-"
				+ step.componentCode(), input);
		((Map) nestedParams.get("OUTPUTS")).put(step.moduleCode() + "-"
				+ step.componentCode(), output);
	}

	// Information about a variable group
	// ([name, scope, values-of-vars-in-the-group])
	private static class GroupInfo {
		public final String name; // groupName
		public final String scope; // I/T/G
		public final Map values; // values of variables

		public GroupInfo(String n, String sc, Map vals) {
			name = n;
			scope = sc;
			values = vals;
		}
	}

	// Collection of all parameters which are resolved at run time
	private Map<String, Object> nestedParams = new HashMap<String, Object>() {
		{
			put("INPUTS", new HashMap<String, Map>());
			put("OUTPUTS", new HashMap<String, Map>());
			put("CONFIG", Harness.GCONFIG.getAllProperties());
			put("UNIQUE", new Substitutor.Substitution() {
				public String get(String ref) {
					return UniqueUtils.getInstance().uniqueString(ref);
				}
			});
		}
	};

	private LogUtils logger = new LogUtils(this);

	// Input/Output used by the most recent component
	// Used to resolve references to INPUT/OUTPUT variables
	private DataRow[] mostRecentparameters;

	// Variable groups as declared in Variables sheet (groupName => groupInfo)
	private Map<String, GroupInfo> groups = new HashMap();

	private Map entities = new HashMap();
}
