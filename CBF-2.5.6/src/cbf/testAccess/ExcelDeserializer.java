/******************************************************************************
$Id : ExcelDeserializer.java 3/3/2016 6:00:53 PM
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

package cbf.testAccess;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cbf.model.DataAccess;
import cbf.model.TestCase;
import cbf.model.TestIteration;
import cbf.model.TestStep;
import cbf.utils.DTAccess;
import cbf.utils.DataRow;
import cbf.utils.ExcelAccess;
import cbf.utils.FileUtils;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbf.utils.Substitutor;
import cbf.utils.Utils;

/**
 * 
 * Extends Utils class and generates ExcelTestCase
 * 
 */
public class ExcelDeserializer extends Utils {

	/**
	 * Returns TestCase object
	 * 
	 * @param dtAccess
	 *            object of DataAccess
	 * @param testName
	 *            name of TestCase
	 * @param serializedFileName
	 *            name of TestCase file
	 * @param params
	 * @return object of TestCase
	 * @throws FileNotFoundException
	 */
	public static TestCase deserialize(DataAccess dtAccess, String testName,
			String serializedFileName, Map params) throws FileNotFoundException {
		if (!FileUtils.fileExists(serializedFileName)) {
			throw new FileNotFoundException(serializedFileName);
		}
		return new ExcelDeserializer(dtAccess, testName, serializedFileName,
				params).deserialize();
	}

	private ExcelDeserializer(DataAccess dtAccess, String testName,
			String serializedFileName, Map params) {
		this.dtAccess = dtAccess;
		this.testName = testName;
		this.sSerializedFileName = serializedFileName;
		this.params = params;

		this.tcDataAccess = new DTAccess(serializedFileName);
	}

	private TestCase deserialize() {
		final Map references;
		if (ExcelAccess.isSheetExists(sSerializedFileName, SHN_REFERENCES)) {
			List<Map> refRows = tcDataAccess.readSheet(SHN_REFERENCES);
			references = makeReferences(refRows);
		} else {
			references = null;
		}
		List<Map> vars = new ArrayList<Map>();
		if (ExcelAccess.isSheetExists(sSerializedFileName, SHN_VARIABLES)) {
			vars = tcDataAccess.readSheet(SHN_VARIABLES);
		}
		if (references != null && !(vars.isEmpty())) {
			for (Map variable : vars) {
				if (references.containsKey(variable.get("name"))) {
					logger.handleError("Clash in variable and reference names:"
							+ variable.get("name"));
				}
			}
		}
		final List<Map> variables = vars;
		// fetch iteration rows
		List<Map> iterRows = null;
		if (ExcelAccess.isSheetExists(sSerializedFileName, SHN_ITERATIONS)) {
			iterRows = tcDataAccess.readSheet(SHN_ITERATIONS);

			iterRows = substitute(iterRows, references);
			int rowSize = iterRows.size();
			int count = 0;
			for (Map iterRow : iterRows) {
				boolean isSelected = true;
				if (!iterRow.isEmpty()) {
					isSelected = s2B(
							(String) params.get("enableIterationSelection"),
							true);
					if (isSelected) {
						isSelected = s2B((String) iterRow.get("_selectYN"),
								true);
					}
					if (!isSelected) {
						count++;
					}
				}
			}

			if (count == rowSize) {
				iterRows = new ArrayList<Map>();
				iterRows.add(new HashMap());
			}

		} else {
			iterRows = new ArrayList<Map>();
			iterRows.add(new HashMap());
		}

		List<Map> stepRows = tcDataAccess.readSheet(SHN_STEPS);
		final String sName = this.testName;
		stepRows = substitute(stepRows, references);

		final List<TestIteration> iterations = makeIterations(iterRows,
				references, stepRows);
		if (iterations.size() == 0) {
			logger.handleError("No iterations are selected from the "
					+ SHN_ITERATIONS + " sheet");
			return null;
		}

		return new TestCase() {
			public TestIteration iteration(int iterationIx) {
				return iterations.get(iterationIx);
			}

			public int iterationCount() {
				return iterations.size();
			}

			public Map masterReferences() {
				return references;
			}

			public String name() {
				return sName;
			}

			public List<Map> variables() {
				return variables;
			}

		};
	}

	private List<TestIteration> makeIterations(List<Map> iterRows,
			Map references, List<Map> stepRows) {
		List<TestIteration> iterations = new ArrayList();
		boolean isSelected;
		for (Map iterRow : iterRows) {
			isSelected = true;
			if (!iterRow.isEmpty()) {
				isSelected = s2B(
						(String) params.get("enableIterationSelection"), true);
				if (isSelected) {
					// ** isSelected = s2B((String) iterRow.get("_selectYN"),
					// true);
					isSelected = s2B((String) iterRow.get("_selectYN"), true);

				}
			}
			if (isSelected) {
				TestIteration iteration = makeIteration(iterRow, references,
						stepRows);
				iterations.add(iteration);
			}
		}
		return iterations;
	}

	private TestIteration makeIteration(final Map iterRow, Map references,
			List<Map> stepRows) {
		Map iterParamsMap = new HashMap();
		if (iterRow != null) {

			iterParamsMap.put("ITERATION", iterRow);
		}
		List<Map> iterStepRows = substitute(stepRows, iterParamsMap);
		final List<TestStep> steps = new ArrayList<TestStep>();
		int stepIx = 0;
		for (Map stepRow : iterStepRows) {
			boolean isSelected = true;
			if (Utils.string2Bool((String) params.get("enableStepSelection"))) {

				isSelected = s2B((String) stepRow.get("_selectYN"), true);
			}

			List<TestStep> stepsList = null;
			if (isSelected) {
				stepsList = makeTestStep(stepRow, stepIx, iterParamsMap,
						references);
			}
			if (stepsList == null) {
				logger.trace("Step row skipped:"
						+ StringUtils.mapToString(stepRow).replace(",", ";")
						+ ":#" + stepIx);
			} else {
				for (TestStep oneStep : stepsList) {
					steps.add(oneStep);
					logger.trace("Step added:"
							+ StringUtils.mapToString(stepRow)
									.replace(",", ";") + ":#" + stepIx);
				}
			}
		}

		return new TestIteration() {
			public Map parameters() {
				return iterRow;
			}

			public TestStep step(int stepIx) {
				return steps.get(stepIx);
			}

			public int stepCount() {
				return steps.size();
			}
		};

	}

	private boolean s2B(String s, boolean defb) {
		if (s == null || "".equals(s))
			return defb;
		return Utils.string2Bool(s);
	}

	private List<TestStep> makeTestStep(Map stepRow, int iCount,
			Map iterParamsMap, Map references) {
		final String stepName, moduleCode, componentCode, componentOutputValidation;
		List<DataRow> componentParameters = new ArrayList<DataRow>();
		Map paramSpec = null;

		List<TestStep> testStepList = new ArrayList<TestStep>();
		if (Utils.string2Bool((String) params.get("enableStepSelection"))) {

			boolean isExpected;
			isExpected = s2B((String) stepRow.get("_selectYN"), true);
			if (!isExpected)
				logger.trace("Test Step Skipped!!");
			else {

				moduleCode = (String) stepRow.get("moduleCode");
				componentCode = (String) stepRow.get("componentCode");
				if (moduleCode.equals("") && componentCode.equals("")) {
					logger.handleError("Blank module and component codes in test file;\n step skipped at: "
							+ (iCount + 1));
					return null;
				}
				String name = (String) stepRow.get("stepName");
				if (name == null || name == "") {
					name = componentCode;
				}
				stepName = name;
				componentParameters = unionOfMaps(new List[] {
						resolveOfflineRowId(moduleCode, componentCode,
								stepRow.get("offlineRowId") == null ? ""
										: (String) stepRow.get("offlineRowId")),
						resolveInlineRowId(moduleCode, componentCode,
								stepRow.get("inlineRowId") == null ? ""
										: (String) stepRow.get("inlineRowId"),
								iterParamsMap, references),
						resolveInlineParamValues(
								stepRow.get("parameters") == null ? ""
										: (String) stepRow.get("parameters"),
								iterParamsMap, references) });

				final boolean bFailTest = Boolean.parseBoolean((String) stepRow
						.get("failTestIfUnexpected"));

				final boolean bAbortTest = Boolean
						.parseBoolean((String) stepRow
								.get("abortTestIfUnexpected"));

				componentOutputValidation = (String) stepRow
						.get("componentOutputValidation");
				if (componentParameters.size() == 0)
					componentParameters.add(new DataRow());

				for (final DataRow componentParam : componentParameters) {
					TestStep testStep = new TestStep() {
						public boolean abortTestIfUnexpected() {
							return bAbortTest;
						}

						public String componentCode() {
							return componentCode;
						}

						public String componentOutputValidation() {
							return componentOutputValidation;
						}

						public DataRow componentParameters() {
							return componentParam;
						}

						public boolean failTestIfUnexpected() {
							return bFailTest;
						}

						public String moduleCode() {
							return moduleCode;
						}

						public String stepName() {
							return stepName;
						}

					};

					testStepList.add(testStep);
				}
			}
		}

		return testStepList;
	}

	private String checkRows(List<?> rows) {
		if (rows == null) {
			return "Table not found";
		}
		switch (rows.size()) {
		case 0:
			return "No rows found";

		default:

			return "";
		}
	}

	private Map getModuleDataRow(String moduleCode, String componentCode,
			String rowSelector) {
		List<Map> rows = dtAccess.selectRows(moduleCode, componentCode,
				rowSelector);
		String sMsg = checkRows(rows);
		if (!(sMsg == null) && !(sMsg.equals(""))) {
			logger.handleError("Failed to get module data row:"
					+ StringUtils.toString(new String[] { moduleCode,
							componentCode, rowSelector }) + ":" + sMsg);
			return null;
		}
		return rows.get(0);
	}

	private Map makeReferences(List<?> refRows) {
		Map oRefs = new HashMap();
		for (int ix = 0; ix < refRows.size(); ix++) {
			Map row = (Map) refRows.get(ix);
			Map masterRow = getModuleDataRow("Master",
					(String) row.get("masterTableName"),
					(String) row.get("rowId"));
			oRefs.put((String) row.get("name"), masterRow);
		}
		return oRefs;
	}

	private Map<String, Map> moduleReferences = new HashMap<String, Map>();

	private Map makeModuleReferences(String sModuleCode) {

		Map references = null;
		if (moduleReferences.containsKey(sModuleCode)) {
			return moduleReferences.get(sModuleCode);
			// Need to discuss
		}
		List<Map> refRows = dtAccess.selectRows(sModuleCode, "References", "");
		if (refRows != null) {
			references = makeReferences(refRows);
		}
		moduleReferences.put(sModuleCode, references);
		return references;
	}

	private List<Map> resolveOfflineRowId(String moduleCode,
			String componentCode, String rowSelector) {
		if (rowSelector.equals("")) {
			return null;
		}
		String[] rowsSel = rowSelector.split(",");
		List<Map> row = new ArrayList<Map>();
		for (String str : rowsSel)
			row.add(getModuleDataRow(moduleCode, componentCode, str));
		if (row.isEmpty()) {
			logger.handleError("Error in resolving offline row reference:"
					+ StringUtils.arrayToString(new String[] { moduleCode,
							componentCode, rowSelector }));
		}
		else{
			row = substitute(row, makeModuleReferences(moduleCode));
			return row;
		}

		return row;
	}

	private List<Map> resolveInlineRowId(String moduleCode,
			String componentCode, String rowSel, Map iterParamsMap,
			Map references) {
		if (rowSel.equals("")) {
			return null;
		}
		List<Map> rows = new ArrayList<Map>();

		String[] rowsSel = rowSel.split(",");
		String[] sheetNames = { moduleCode + "-" + componentCode, componentCode };
		for (String sheetName : sheetNames) {
			if (ExcelAccess.isSheetExists(sSerializedFileName, sheetName)) {
				for (String str : rowsSel)
					rows.addAll(tcDataAccess.readSelectedRows(sheetName, str));
				String sMsg = checkRows(rows);
				if (!(sMsg.equals(""))) {
					logger.handleError("Error in inline row selection:"
							+ rowSel + ":" + sMsg);
					break;
				}
			}
		}
		if (rows == null || rows.isEmpty()) {
			logger.handleError("Could not resolve inline data ref:" + rowSel);
			return null;
		}

		return substitute(substitute(rows, iterParamsMap), references);

	}

	private List<Map> substitute(List<?> stepRow, Map substitutions) {
		List<Map> result = (List<Map>) stepRow;
		if (substitutions != null)
			result = new Substitutor(substitutions).substitute(stepRow);
		return result;
	}

	private Map substitute(Map stepRow, Map substitutions) {
		Map result = stepRow;
		if (substitutions != null)
			result = new Substitutor(substitutions).substitute(stepRow);
		return result;
	}

	/*
	 * // retain it until actual implementation of substitute private Map
	 * convertDataRowToMap(DataRow o) { Map map = new HashMap();
	 * Collection<String> keys = o.keySet(); for (String key : keys) {
	 * map.put(key, (String) o.get(key)); } return map;
	 * 
	 * }
	 */

	private List<Map> resolveInlineParamValues(String paramValueString,
			Map iterParamsMap, Map references) {
		List<Map> params = new ArrayList<Map>();
		Map map = new HashMap();
		if (paramValueString.equals("")) {
			return null;
		}
		Pattern pattern = Pattern.compile("([^=]*)=([^|]*)\\|?");
		Matcher m = pattern.matcher(paramValueString);
		while (m.find()) {
			map.put(m.group(1).trim(), m.group(2).trim());
		}
		params.add(map);
		params = substitute(params, iterParamsMap);
		params = substitute(params, references);
		return params;
	}

	private DataRow mergeMaps(Map refs, Map params){
		if(params != null){
		Iterator it = params.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        refs.put(pair.getKey(), pair.getValue());
	    }
		}
		return Utils.Map2DataRow(refs);
	}
	
	private <E extends Map> List<DataRow> unionOfMaps(List<E>[] lists) {
		List<DataRow> unionList = new ArrayList<DataRow>();
		DataRow union = new DataRow();
		Map parameters = null;
		List<Map> offlineRefs = (List<Map>) lists[0];
		List<Map> inlineRefs = (List<Map>) lists[1];
		if(lists[2] != null){
			if(lists[2].size() == 1){
				parameters = (Map) lists[2].get(0);
			}
		}
		
		if(offlineRefs != null){
			for (Map map : offlineRefs) {
				unionList.add(mergeMaps(map, parameters));
			}
			return unionList;
		}
		if(inlineRefs != null){
			for (Map map : inlineRefs) {
				unionList.add(mergeMaps(map, parameters));
			}
			return unionList;
		}
		if(parameters != null)
		if(unionList.size() == 0){
			DataRow reference = new DataRow();
			Iterator res = parameters.entrySet().iterator();
		    while (res.hasNext()) {
		        Map.Entry entry = (Map.Entry)res.next();
		        reference.put((String)entry.getKey(), (String)entry.getValue());
		        
		    }
			unionList.add((DataRow) reference);
		}
		return unionList;
	}

	/**
	 * Returns ExcelDeserializer format string
	 */
	public String toString() {
		return StringUtils.mapString(this, dtAccess, tcDataAccess, testName,
				sSerializedFileName, params);
	}

	DataAccess dtAccess;
	DTAccess tcDataAccess;
	String testName, sSerializedFileName;
	Map params;

	private final String SHN_STEPS = "Steps", SHN_REFERENCES = "References",
			SHN_ITERATIONS = "Iterations", SHN_VARIABLES = "Variables";
	private LogUtils logger = new LogUtils(this);

}
