/******************************************************************************
$Id : XmlObjectMap.java 3/3/2016 6:01:29 PM
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

package cbfx.selenium;

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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cbf.harness.ResourcePaths;
import cbf.utils.ExcelAccess;
import cbf.utils.FileUtils;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

public class XmlObjectMap implements ObjectMap {

	public List<Map> uiMap;

	public XmlObjectMap(Map params) {
		uiMap = readLocators();
	}

	public List<Map> readLocators() {
		try {
			String filepath = ResourcePaths.getInstance().getSuiteResource(
					"Plan/AppDriver/OR", "uiMap.xml");
			File file = new File(filepath);
			logger.trace("Loading configuration from ", file);
			if (!file.exists())
				throw new FileNotFoundException();
			try {
				Document doc = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(file);
				return getProperties(doc.getDocumentElement());
			} catch (ParserConfigurationException e) {
				logger.handleError("Invalid configuration file ", e);
			} catch (SAXException e) {
				logger.handleError("Invalid configuration file ", e);
			} catch (IOException e) {
				logger.handleError("Invalid configuration file ", e);
			}
		}

		catch (Exception e) {
			logger.handleError("Error while reading the locators", e);
		}
		return null;
	}

	private  List<Map> getProperties(Node rootNode) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map> imageslocator = new ArrayList<Map>();
		for (Node subNode = rootNode.getFirstChild(); subNode != null; subNode = subNode
				.getNextSibling()) {

			if (subNode.getNodeName() == "image"
					&& subNode.getNodeType() == Node.ELEMENT_NODE) {
				map = loadNode(subNode, map);
				imageslocator.add(map);
			}
		}
		return imageslocator;
	}
	
	private Map loadNode(Node varNode, Map<String, Object> map) {
		Map attributes = new HashMap();

		for (Node subNode = varNode.getFirstChild(); subNode != null; subNode = subNode
				.getNextSibling()) {

			if (subNode.getNodeType() == Node.ELEMENT_NODE) {
				if (subNode.getNodeName() == "attributes") {
					attributes = getValue(subNode);
					return attributes;
			}
			}
		}

		logger.trace("Variable added ", attributes);
		return map;
	}

	public Map getValue(Node valNode) {
		String result = "";
		Map<String, Object> subNodesMap = new HashMap<String, Object>();
		for (Node subNode = valNode.getFirstChild(); subNode != null; subNode = subNode
				.getNextSibling()) {
			if (valNode.getChildNodes().getLength() == 1) {
				result = subNode.getTextContent().trim();
			}
			loadSubnode(subNode, subNodesMap);
		}
		return subNodesMap;

	}
	private void loadSubnode(Node mapNode, Map<String, Object> map) {
		for (Node subNode = mapNode.getFirstChild(); subNode != null; subNode = subNode
				.getNextSibling()) {
			map.put(mapNode.getNodeName(), mapNode.getTextContent().trim());
		}
	}
	public List<Map> ObjectMaps(String pageName) {
		// Auto-generated method stub
		return null;
	}

	public Map properties(String imagetitle) {
		Map result = null;
		try {
			for (Map map : uiMap) {
				String temp = (String) map.get("title");
				if (imagetitle.matches(temp) || imagetitle.equals(temp)) {
					result = map;
					break;
				}
			}
		}

		catch (Exception e) {
			logger.handleError(
					"Error: Trying to retrieve the Locator of Unknown Element \" "
							+ imagetitle + " \"", e);
		}
		return result;
	}

	private LogUtils logger = new LogUtils(this);
}
