/******************************************************************************
$Id : Configuration.java 3/3/2016 6:01:03 PM
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Represents a common XML-based configuration. Contains methods to load from
 * files and access configuration items. Sample structure of config file is :
 * <config> <variable> <name>TestCaseAccess</name> <!--<value>
 * <plugin>Excel</plugin> </value>--> <value> <plugin>Designer</plugin>
 * <parameters> <url>http://10.212.21.30:8085/api</url> <dbname>dev1</dbname>
 * <username>tcdemo</username> <password>password</password> </parameters>
 * </value> </variable> </config>
 * 
 */
public class Configuration {

	/**
	 * Constructor to load configuration file
	 * 
	 * @param fileName
	 *            name of Configuration file
	 */
	public Configuration(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
		configMap = load(fileName);
	}

	/**
	 * Returns the value associated with the key
	 * 
	 * @param key
	 *            to retrieve value
	 * @return value as Object
	 */
	public Object get(String key) {
		return configMap.get(key);
	}

	public void set(Map map) {
		configMap = map;
	}

	/**
	 * Returns all the properties of config file
	 * 
	 * @return map TODO: check if this can be removed
	 */
	public Map getAllProperties() {
		return configMap;
	}

	/*
	 * load the file and return map methods below are simply for parsing/loading
	 * the different kinds of nodes
	 */
	private Map<String, Object> load(String fileName)
			throws FileNotFoundException {
		logger.trace("Loading configuration from ", fileName);
		File file = new File(fileName);
		if (!file.exists())
			throw new FileNotFoundException(fileName);

		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(file);
			return getVariables(doc.getDocumentElement());
		} catch (ParserConfigurationException e) {
			logger.handleError("Invalid configuration file ", fileName, e);
		} catch (SAXException e) {
			logger.handleError("Invalid configuration file ", fileName, e);
		} catch (IOException e) {
			logger.handleError("Invalid configuration file ", fileName, e);
		}
		return null;
	}

	private Map<String, Object> getVariables(Node rootNode) {
		Map<String, Object> map = new HashMap<String, Object>();

		for (Node subNode = rootNode.getFirstChild(); subNode != null; subNode = subNode
				.getNextSibling()) {

			if (subNode.getNodeName() == "variable"
					&& subNode.getNodeType() == Node.ELEMENT_NODE) {
				loadVariable(subNode, map);
			}
		}

		return map;
	}

	/*
	 * load variable defined in varNode into map
	 */
	private void loadVariable(Node varNode, Map<String, Object> map) {
		String name = "";
		Object value = "";

		for (Node subNode = varNode.getFirstChild(); subNode != null; subNode = subNode
				.getNextSibling()) {

			if (subNode.getNodeType() == Node.ELEMENT_NODE) {
				if (subNode.getNodeName() == "name")
					name = subNode.getTextContent();

				if (subNode.getNodeName() == "value")
					value = getValue(subNode);
			}
		}
		map.put(name, value);
		logger.trace("Variable added ", name, value);
	}

	private Object getValue(Node valNode) {

		Map<String, Object> subNodesMap = new HashMap<String, Object>();

		for (Node subNode = valNode.getFirstChild(); subNode != null; subNode = subNode
				.getNextSibling()) {

			if (subNode.getNodeName() == "array")
				return getArray(subNode);

			if (valNode.getChildNodes().getLength() == 1)
				return subNode.getTextContent().trim();

			loadSubnode(subNode, subNodesMap);
		}

		return subNodesMap;
	}

	private void loadSubnode(Node mapNode, Map<String, Object> map) {
		for (Node subNode = mapNode.getFirstChild(); subNode != null; subNode = subNode
				.getNextSibling()) {
			map.put(mapNode.getNodeName(), getValue(mapNode));
		}
	}

	private List getArray(Node arrayNode) {
		List array = new ArrayList<Map<String, Object>>();

		for (Node subNode = arrayNode.getFirstChild(); subNode != null; subNode = subNode
				.getNextSibling()) {

			if (subNode.getNodeName() == "value") {
				array.add(getValue(subNode));
			}
		}

		return array;
	}

	/**
	 * Returns Configuration format string
	 */
	public String toString() {
		return StringUtils.mapString(this, fileName);
	}

	private LogUtils logger = new LogUtils(this);
	private String fileName;
	private Map<String, Object> configMap;
}
