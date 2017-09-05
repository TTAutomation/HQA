/******************************************************************************
$Id : PluginManager.java 3/3/2016 6:00:38 PM
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

package cbf.plugin;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import cbf.harness.ResourcePaths;
import cbf.utils.Configuration;
import cbf.utils.FrameworkException;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

/**
 * Manages all the plugins related functions
 */
public class PluginManager {

	/*
	 * Set by Harness directly prior to using it TODO: refine bootstrapping
	 */

	public static String masterConfigFileName;

	/**
	 * Returns instance of PluginManager
	 * 
	 * @return PluginManager instance
	 */
	public static PluginManager getInstance() {
		if (singleton == null) {
			masterConfigFileName = ResourcePaths.getInstance()
					.getFrameworkResource("Resources", "MasterConfig.xml");
			singleton = new PluginManager(masterConfigFileName);
		}
		return singleton;
	}

	/**
	 * Returns instance of specified Plugin
	 * 
	 * @param usageMap
	 *            parameters of plugin
	 * @return instance of Plugin
	 */

	public static Object getPlugin(Map usageMap) {
		try {
			return getInstance().getPlugin((String) usageMap.get("plugin"),
					(Map) usageMap.get("parameters"));
		} catch (ClassCastException e) {
			logger.handleError(
					"Parameters are not proper in user config file ", e);
		}
		return null;
	}

	/**
	 * Returns instance of specified Plugin
	 * 
	 * @param pluginName
	 *            of Plugin
	 * @param usageParams
	 *            parameters of plugin
	 * @return instance of Plugin
	 */
	public static Object getPlugin(String pluginName,
			Map<String, Object> usageParams) {
		return getInstance().parsePlugin(pluginName, usageParams);
	}

	private PluginManager(String fileName) {
		System.out.println("filename------" + fileName);
		try {
			masterConfig = new Configuration(fileName);
		} catch (FileNotFoundException e) {
			logger.handleError("Plugin Configuration file not exist", fileName,
					e);
		}
	}

	private Object parsePlugin(String pluginName,
			Map<String, Object> usageParams) {
		String className = null;
		Map<String, Object> masterParamsMap = new HashMap<String, Object>();

		try {
			Map<String, Object> masterPluginMap = (Map<String, Object>) masterConfig
					.get(pluginName);
			if(masterPluginMap == null){
				logger.handleError("Plugin details are not mentioned in config files", pluginName, usageParams);
			}
			className = (String) masterPluginMap.get("classname");

			masterParamsMap = (Map<String, Object>) masterPluginMap
					.get("parameters");

		} catch (ClassCastException e) {
			logger.handleError("Class Cast Exception ", e);
			return null;
		}

		Map<String, Object> finalMap = mergeMaps(masterParamsMap, usageParams);
		if (finalMap != null) {
			for (String key : finalMap.keySet()) {
				if (finalMap.get(key).equals("TBD")) {
					logger.handleError(key
							+ " value must be specified in user config for ",
							pluginName);
				}
			}
		}

		try {
			return initializePlugin(className, finalMap);
		} catch (FrameworkException fe) {
			logger.handleError("Error in instantiating plugin", pluginName, fe,
					className, finalMap, usageParams);
			return null;
		}
	}

	public static Object initializePlugin(String className, Map finalMap) {
		try {
			return Class.forName(className).getDeclaredConstructor(Map.class)
					.newInstance(finalMap);
		} catch (ClassNotFoundException e) {
			logger.handleError("Class not found", className, e);
		} catch (NoSuchMethodException e) {
			logger.handleError("No matching constructor found for ", className);
		} catch (Exception e) {
			logger.handleError("Class instantiation error", className, finalMap, e);
		}
		return finalMap;
	}

	private Map<String, Object> mergeMaps(Map masterParamsMap, Map usageParams) {
		Map<String, Object> finalMap;
		if (masterParamsMap == null)
			finalMap = usageParams;

		else {
			finalMap = masterParamsMap;
			if (usageParams != null)
				finalMap.putAll(usageParams);
		}
		return finalMap;
	}

	/**
	 * Returns PluginManager format string
	 */

	public String toString() {
		return StringUtils.mapString(this, masterConfig);
	}

	private static PluginManager singleton = null;
	private static LogUtils logger = new LogUtils();
	private Configuration masterConfig;
}
