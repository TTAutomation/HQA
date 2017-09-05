/******************************************************************************
$Id : XmlTestSetAccess.java 3/3/2016 6:00:53 PM
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

/*import mx4j.log.Logger;*/

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cbf.model.TestCase;
import cbf.model.TestInstance;
import cbf.model.TestIteration;
import cbf.model.TestSet;
import cbf.utils.StringUtils;

/**
 * 
 * Implements TestSet interface and makes TestInstance
 * 
 */
public class XmlTestSetAccess implements TestSet {
	private static Map<Integer, ArrayList<String>> testInstanceInfo = new HashMap<Integer, ArrayList<String>>();

	/**
	 * Constructor which reads the TestSet sheet and makes a list map for
	 * instances as per the user selection
	 * 
	 */
	public XmlTestSetAccess(Map params) {
		this.params = params;

		try {
			String folderpath = (String) params.get("folderpath");
			File f = new File(folderpath);
			String fileName = null;
			File[] files = f.listFiles();
			for (File file : files) {
				fileName = file.getCanonicalPath();
			}
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(fileName);
			NodeList nodeList = doc.getElementsByTagName("testcase");
			int index = 0;
			for (int s = 0; s < nodeList.getLength(); s++) {

				Element subNode = (Element) nodeList.item(s);
				String testCaseName = subNode.getAttribute("name");
				String folderPath = "";
				ArrayList<String> instanceList = new ArrayList<String>();
				instanceList.add(testCaseName);
				instanceList.add((String) params.get("designerFolder"));
				testInstanceInfo.put(index, instanceList);
				index++;

			}

		} catch (Exception e) {

		}
	}

	public TestInstance testInstance(final int ix) {

		final ArrayList<String> params = testInstanceInfo.get(ix);
		return new TestInstance() {

			public TestCase testCase() {
				return null;
			}

			public String description() {
				return null;
			}

			public String instanceName() {
				return params.get(0);
			}

			public TestIteration[] iterations() {
				return null;
			}

			public String folderPath() {
				return params.get(1);
			}
		};
	}

	/**
	 * Returns number of TestInstances
	 * 
	 * @return TestInsances count
	 */
	public int testInstanceCount() {
		return testInstanceInfo.size();
	}

	/**
	 * Returns XmlTestSetAccess format string
	 */
	public String toString() {
		return StringUtils.mapString(this, params);
	}

	private Map params;

}
