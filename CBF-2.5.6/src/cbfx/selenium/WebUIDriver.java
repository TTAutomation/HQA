/******************************************************************************
$Id : WebUIDriver.java 3/3/2016 6:01:28 PM
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

import static cbf.engine.TestResultLogger.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cbf.harness.Harness;
import cbf.plugin.PluginManager;
import cbf.utils.DataRow;
import cbf.utils.LogUtils;
import cbf.utils.StringUtils;
import cbfx.selenium.browsers.Browser;

/**
 * 
 * Extends BaseWebAppDriver and handles all the common functionalities for
 * webControls like setting the TextBox,Selecting an option in Dropdown ,etc..
 * 
 */

public class WebUIDriver {

	/**
	 * Overloaded Constructor to initialize webdriver and selenium
	 * 
	 * @param parameters
	 *            webDriver: object of WebDriver selenium: object of
	 *            WebDriverBackedSelenium
	 */
	public WebUIDriver(Map parameters) {


		Map obj=(Map<String, Object>) Harness.GCONFIG.get("ObjectMap");
		objMap=(ObjectMap) PluginManager
				.getPlugin((String)obj.get("plugin"),null);

		Map params = (Map) parameters.get("Browser");
		browser = (Browser) PluginManager.getPlugin(
				(String) params.get("plugin"),
				(Map<String, Object>) params.get("parameters"));
		this.th = new TableHandler(this);
	}

	private void openBrowser() {
		this.webDr = browser.openDriver();
	}
	/**
	 * Launches the Application in the Browser
	 * 
	 * @param url
	 *            URL of the page to be opened.
	 */
	public void launchApplication(String url) {
		openBrowser();
		webDr.manage().window().maximize();
		webDr.get(url);

	}


	public void launchUrl(String url) {

		//openBrowser();
		webDr.get(url);
	}




	/**
	 * Checks whether page title matches or not
	 * 
	 * @param pageTitle
	 *            title of page opened
	 * @return true or false result
	 */
	public boolean checkPage(String pageTitle) {
		if (pageTitle.equals(webDr.getTitle())) {
			return true;
		}
		return false;
	}

	/**
	 * Identifies the locator as needed by webDriver object
	 * 
	 * @param elementName
	 *            Name of the element whose locator is required
	 * @return Identified locator as Web Element
	 */

	public WebElement getControl(String elementName) {
		String actualLocator = (String) objMap.properties(elementName).get("locator");
		System.out.println("Actual Locator -  " + actualLocator);
		WebElement element = null;
		int index = 1;
		final By[] byCollection = { By.id(actualLocator),
				By.name(actualLocator), By.xpath(actualLocator),
				By.className(actualLocator), By.cssSelector(actualLocator),
				By.tagName(actualLocator), By.linkText(actualLocator),
				By.partialLinkText(actualLocator) };

		for (By by : byCollection) {
			try {
				element = webDr.findElement(by);
				if (!element.equals(null)) {
					break;
				}
			} catch (Exception e) {
				if (index == byCollection.length) {
					logger.handleError("Unable to find element ", elementName,
							e);
				} else {
					index++;
					continue;
				}
			}
		}
		return element;
	}

	/**
	 * Sets the value to the specified UI Element
	 * 
	 * @param elementName
	 *            name of element
	 * @param value
	 *            value to be set on element
	 */
	public void setValue(String elementName, String value) {
		WebElement element = getControl(elementName);
		try {
			String type = element.getAttribute("type");

			if ((type.equals("text")) || type.equals("password")) {
				element.clear();
				element.sendKeys(value);
				logger.trace("Typed text '" + value
						+ "' in the input element '" + elementName + "'");
				return;

			}

			if (type.equals("select-one")) {
				// TODO: Support for other select options?
				Select selectOption = new Select(element);
				selectOption.selectByVisibleText(value);
				logger.trace("Selected  " + value + "' in " + elementName + "'");
				return;

			}

			if (type.equalsIgnoreCase("Calendar")) {
				setCalendar(element, value);
				return;
			}

		} catch (Exception e) {
			logger.handleError("Error while setting value ," + value
					+ " on element " + elementName, e);
		}
	}

	/**
	 * Sets the value to the specified UI Element
	 * 
	 * @param elementName
	 *            name of element
	 * @param input
	 *            input datarow with UI properties
	 */
	public void setValue(String elementName, DataRow input) {
		setValue(elementName, input.get("elementName"));
	}

	// TODO:Should be made as a plugin
	private void setCalendar(WebElement element, String value) {
		element.click();
		webDr.findElement(
				By.linkText(value.substring(0, value.indexOf("/")).toString()))
				.click();
	}

	/**
	 * Sets the value to all the locators on the specified page.
	 * 
	 * @param pageName
	 *            name of the page
	 * @param data
	 *            value to be set on page
	 */
	public void setValues(String pageName, Map data) {
		List<Map> resultMap = new ArrayList();
		resultMap = objMap.ObjectMaps(pageName);
		setValues(data, resultMap);
	}

	private void setValues(Map data, List<Map> uiElements) {
		String elementName = "";
		for (Map uiElement : uiElements) {
			elementName = ((String) uiElement.get("elementName"));
			if (data.containsKey(elementName)) {
				setValue(elementName, (String) data.get(elementName));
			}
		}
	}


	/**
	 * Gets the value of the specified element
	 * 
	 * @param elementName
	 *            whose value has to be obtained
	 */
	public String getValue(String elementName) {
		WebElement element = getControl(elementName);
		try {
			String type = element.getAttribute("type");

			if (type.equals("text") || type.equals("password")) {

				if (element.getText().equals(""))
					return getAttribute(elementName, "value");
				return element.getText();

			}
			if (type.equals("select-one")) {

				Select selectOption = new Select(element);
				return selectOption.getFirstSelectedOption().getText();

			}
			if (type.equals("checkbox") || type.equals("radio")) {

				if (element.isSelected()) {
					return "Yes";
				} else {
					return "No";
				}

			}
		} catch (Exception e) {
			logger.handleError("Error while getting value of element "
					+ elementName, e);
		}

		return null;
	}

	/**
	 * Clears the value of specified element
	 * 
	 * @param elementName
	 *            element to be cleared
	 */
	public boolean clear(String elementName) {
		try {
			getControl(elementName).clear();
			return true;
		} catch (Exception e) {
			logger.handleError("Failed to clear  " + elementName, e);
			return false;
		}

	}



	public boolean selectfromList(String elementName,String listitem) {
		try {
			WebElement listelement=getControl(elementName);
			List<WebElement> listelements=listelement.findElements(By.tagName("option"));
			for (WebElement eachlistelements : listelements) {
				if(eachlistelements.getText().trim().equalsIgnoreCase(listitem))
				{
					eachlistelements.click();
					break;
				}
			}
			return true;
		} catch (Exception e) {
			logger.handleError("Failed to selectfromlist  " + elementName, e);
			return false;
		}

	}

	/**
	 * Navigates to the Menu
	 * 
	 * @param menuList
	 *            list of menu items
	 */
	public void navigateMenu(String menuList) {
		String[] aMenu;
		aMenu = menuList.split(",");
		try {
			for (int i = 0; i < aMenu.length; i++) {
				if (!(aMenu[i].matches(""))) {
					getControl(aMenu[i]).click();
					logger.trace("Clicked on menu: " + aMenu[i] + ".");
					waitForBrowserStability("1000");
				}
			}
		} catch (Exception e) {
			if (menuList.contains(","))
				menuList = menuList.replaceAll(",", "-->");
			logger.handleError("Failed while Navigating to  " + menuList);
		}
	}

	/**
	 * Checks the presence of element till timeout
	 * 
	 * @param elementName
	 *            name of the element
	 * @param timeInSec
	 *            time limit
	 * @return boolean result
	 */
	public boolean checkElementPresent(String elementName, int timeInSec) {
		WebDriverWait waitForElement = new WebDriverWait(webDr,
				TimeUnit.MILLISECONDS.toSeconds(500));
		WebElement webElement = getControl(elementName);
		boolean result = false;
		try {
			for (int second = 0;; second++) {
				if (second >= timeInSec * 2) {
					logger.trace("TimeOut waiting for " + elementName + " "
							+ (second / 2) + " Seconds");
					break;
				}
				try {
					if (webElement.isDisplayed()) {
						result = true;
						break;
					}
				} catch (Exception e) {
					logger.handleError("Failed to display element for locator",
							elementName, e);
				}
				waitForElement.until(ExpectedConditions
						.visibilityOf(webElement));
			}
		} catch (Exception e) {
			logger.handleError(
					"Error: Caused while Verifying the Presence of Element \" "
							+ elementName + " \"", e);
		}

		return result;
	}

	/**
	 * Check for the presence of the specified text on page till timeout
	 * 
	 * @param text
	 *            text to be verified
	 * @param timeInSec
	 *            time-limit(in seconds)
	 * @return boolean Result
	 */
	public boolean checkTextOnPage(String text, int timeInSec) {
		boolean result = false;
		WebDriverWait waitForPage = new WebDriverWait(webDr,
				TimeUnit.MILLISECONDS.toSeconds(500));

		try {
			for (int second = 0;; second++) {
				if (second >= timeInSec * 10) {
					logger.trace("TimeOut for " + second);
					break;
				}
				if (webDr.findElement(By.tagName("body")).getText()
						.contains(text)) {
					result = true;
					break;
				}
				waitForPage.until(ExpectedConditions.visibilityOf(webDr
						.findElement(By.tagName("body"))));
			}
		} catch (Exception e) {
			logger.handleError(
					"Error: Caused while Verifying the Presence of Text \" "
							+ text + " \"", e);
		}
		return result;
	}

	/**
	 * Clicks on the specified element
	 * 
	 * @param elementName
	 *            element to be clicked
	 */

	public void click(String elementName) {
		try {
			getControl(elementName).click();
		} catch (Exception e) {
			logger.handleError("Failed to click on the element ", elementName,
					" ", e);
		}

	}

	public void clickOperation(String XpathelementName) {
		try {
			webDr.findElement(By.xpath(XpathelementName)).click();
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.handleError("Failed to click on the element ", XpathelementName,
					" ", e);
		}
	}
	
	
	public void clearOperation(String XpathelementName) {
		try {
			webDr.findElement(By.xpath(XpathelementName)).clear();
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.handleError("Failed to clear on the element ", XpathelementName,
					" ", e);
		}
	}
	
	public void SendKeysOperation(String XpathelementName,String Data) {
		try {
			webDr.findElement(By.xpath(XpathelementName)).sendKeys(Data);;
			Thread.sleep(1000);
		} catch (Exception e) {
			logger.handleError("Failed to enter the data on the element ", XpathelementName,
					" ", e);
		}
	}



	public String toGetText(String XpathelementName) {
		String text="";
		try {
			text= webDr.findElement(By.xpath(XpathelementName)).getText().trim();
		} catch (Exception e) {
			logger.handleError("Failed to get text from the element ", XpathelementName,
					" ", e);
		}
		return text;
	}

	public boolean isElementPresent(String Xpathelement) throws Exception{
		try {
			Thread.sleep(1000);
			webDr.findElement(By.xpath(Xpathelement));
			System.out.println("Given - " + Xpathelement + " is available.");
			return true;
		} catch (org.openqa.selenium.NoSuchElementException e) {
			System.out.println("Given - " + Xpathelement + " is not available.");
			return false;
		}
	}

	/**
	 * Mouse overs on the specified menu and clicks on the sub menu.
	 * 
	 * @param menu
	 *            menu of the elements
	 * @param subMenu
	 *            element to be clicked
	 */

	public void mouseOverAndClick(String menu, String subMenu) {
		try {
			Mouse mouse = mouseOver(menu);
			Locatable hoverSubItem = (Locatable) getControl(subMenu);
			mouse = ((HasInputDevices) webDr).getMouse();
			mouse.click(hoverSubItem.getCoordinates());
		} catch (Exception e) {
			logger.handleError("Falied to click ", subMenu, " on", menu, " ", e);
		}
	}

	/**
	 * Mouse overs on the specified element
	 * 
	 * @param elementName
	 *            name of element
	 */

	public Mouse mouseOver(String elementName) {
		try {
			WebElement link = getControl(elementName);
			Locatable hoverItem = (Locatable) link;
			Mouse mouse = ((HasInputDevices) webDr).getMouse();
			mouse.mouseMove(hoverItem.getCoordinates());
			return mouse;
		} catch (Exception e) {
			logger.handleError("Failed to hover on ", elementName, e);
			return null;
		}

	}

	public void mouseHoverandClick(String menuName,String menuLocator, String subMenuName,String subMenuLocator) {
		try {

			WebElement element = webDr.findElement(By.xpath(menuLocator)); 
			Actions action = new Actions(webDr); 
			action.moveToElement(element).moveToElement(webDr.findElement(By.xpath(subMenuLocator))).click().build().perform();
		} catch (Exception e) {
			logger.handleError("Failed to Mousehover and click ", subMenuName, " on", menuName, " ", e);
		}
	}

	/**
	 * Performs the drag and drop operation
	 * 
	 * @param fromLocator
	 *            Contains the locator of source element
	 * @param toLocator
	 *            Contains the locator of destination element
	 */
	public void dragAndDrop(String fromLocator, String toLocator) {
		try {
			WebElement from = getControl(fromLocator);
			WebElement to = getControl(toLocator);
			Actions builder = new Actions(webDr);
			Action dragAndDrop = builder.clickAndHold(from).moveToElement(to)
					.release(to).build();
			dragAndDrop.perform();
		} catch (Exception e) {
			logger.handleError("Failed to drag drop elements ", fromLocator,
					" , ", toLocator, " ", e);
		}
	}

	/**
	 * Switch to another window
	 * 
	 * @param title
	 *            Contains title of the new window
	 * @return true or false
	 */
	public boolean switchToWindow(String title) {
		Set<String> availableWindows = webDr.getWindowHandles();
		if (availableWindows.size() > 1) {
			try {
				for (String windowId : availableWindows) {
					if (webDr.switchTo().window(windowId).getTitle()
							.equals(title))
						return true;
				}
			} catch (Exception e) {
				logger.handleError("No child window is available to switch ", e);
			}
		}

		return false;
	}





	public boolean switchToFrame(String frame) {




		try {

			webDr.switchTo().frame(frame);
			return true;

		} catch (Exception e) {
			logger.handleError("No child window is available to switch ", e);
		}


		return false;
	}

	public boolean switchBackToMainFrame() {		

		try {

			webDr.switchTo().defaultContent();
			return true;

		} catch (Exception e) {
			logger.handleError("No child window is available to switch ", e);
		}


		return false;
	}


	/**
	 * Uploads a file
	 * 
	 * @param filePath
	 *            Contains path of the file to be uploaded
	 * @param browse
	 *            Contains locator of the browse button
	 * @param upload
	 *            locator of the upload button
	 */
	public void fileUpload(String filePath, String browse, String upload) {
		WebElement element = getControl(browse);
		try {
			element.sendKeys(filePath);
			if (upload != null) {
				click("upload");
			}
		} catch (Exception e) {
			logger.handleError("Invalid File Path: ", filePath, e);
		}

	}

	/**
	 * Invokes enter/tab/F5 key
	 * 
	 * @param keyEvent
	 *            key to be invoked
	 * 
	 */
	// TODO:Check for other useful key events
	public void sendKey(String keyEvent) {
		try {
			Robot key = new Robot();
			if (keyEvent.equalsIgnoreCase("enter")) {
				key.keyPress(KeyEvent.VK_ENTER);
				key.keyRelease(KeyEvent.VK_ENTER);
				waitForBrowserStability("1000");
				return;
			}
			if (keyEvent.equalsIgnoreCase("tab")) {
				key.keyPress(KeyEvent.VK_TAB);
				key.keyRelease(KeyEvent.VK_TAB);
				waitForBrowserStability("5000");
				return;
			}
			if (keyEvent.equalsIgnoreCase("F5")) {
				key.keyPress(KeyEvent.VK_F5);
				key.keyRelease(KeyEvent.VK_F5);
				waitForBrowserStability("1000");
				return;
			}
		} catch (AWTException e) {
			logger.handleError("Error caused while clicking on '", keyEvent, e);
		}
	}

	/**
	 * Compares actual and expected text from the application
	 * 
	 * @param elementName
	 *            element for which text is to be checked
	 * @param expectedText
	 *            expected text
	 * @return boolean result
	 */
	public boolean verifyText(String elementName, String expectedText) {
		String actualText = getValue(elementName);
		if (actualText.equalsIgnoreCase(expectedText)) {
			return true;
		}
		return false;
	}

	/**
	 * Verify Tooltip's text
	 * 
	 * @param elementName
	 *            Give element for which tooltip is visible
	 * @param expected
	 *            expected tooltip text
	 */

	public void verifyTooltip(String elementName, String expected) {
		WebElement element = getControl(elementName);
		String str = null;
		try {
			if (!element.getAttribute("title").isEmpty()) {
				str = element.getAttribute("title");
				if (str.contains(expected)) {
					passed("verify tooltip: " + str + "for " + element,
							"Tooltip's text should match with " + str + "for "
									+ element, "Tooltip's text matches with "
											+ str + "for " + element);
				} else {
					failed("verify tooltip: " + str,
							"Tooltip's text should match with " + str,
							"Tooltip's text doesn't match with " + str);
				}
			} else {
				failed("verify tooltip: " + str + "for " + element, str
						+ " not Visible", "Tooltip" + str
						+ " is not visible for " + element);
			}
		} catch (Exception e) {
			logger.handleError("Error while verifying tool tip", e);
		}

	}

	/**
	 * Verifies text in alert and clicks on either OK or cancel.
	 * 
	 * @param text
	 *            expected text
	 * @param button
	 *            "OK" or "Cancel" button
	 */

	public void handleAlert(String text, String button) {
		try {
			Alert alert = webDr.switchTo().alert();
			if (!text.equals("")) {
				String alerttext = alert.getText();
				if (alerttext.matches(text)) {
					passed("verify " + text + " in alert", text
							+ " should present in alert", text
							+ " is present in  alert");
				} else {
					failed("verify " + text + " in alert", text
							+ " should present in alert", text
							+ " is not present in  alert");
				}
			}
			if (button.equalsIgnoreCase("yes") || button.equalsIgnoreCase("ok")) {
				alert.accept();
			} else {
				alert.dismiss();
			}
		} catch (Exception e) {
			logger.handleError("Error while verifying text:" + text
					+ " in alert with button", button, e);
		}

	}

	/**
	 * Sets the text in the alert box
	 * 
	 * @param value
	 *            text to be set in alert box
	 */

	public void setTextInAlert(String value) {
		try {
			Alert alt = webDr.switchTo().alert();
			alt.sendKeys(value);
			alt.accept();
		} catch (Exception e) {
			logger.handleError("Failed to set text:" + value + " in alert", e);
		}

	}

	/**
	 * Gets the text from the alert box
	 * 
	 * @return String
	 */

	public String getAlertText() {
		try {
			Alert alt = webDr.switchTo().alert();
			return alt.getText();
		} catch (Exception e) {
			logger.handleError("Failed to get text from alert box", e);
			return null;
		}
	}

	/**
	 * Verify the presence of alert till timeout
	 * 
	 * @param TimeOutinSeconds
	 *            Give max time limit
	 * @return boolean
	 */

	public boolean isAlertPresent(int TimeOutinSeconds) {
		for (int i = 0; i < TimeOutinSeconds; i++) {
			try {
				Thread.sleep(500);
				webDr.switchTo().alert();
				return true;
			} catch (Exception e) {
				logger.handleError("Failed to verify presence of alert", e);
			}
		}
		return false;
	}

	/**
	 * For highlighting an element
	 * 
	 * @param elementName
	 *            Locator
	 */

	public void drawHighlight(String elementName) {
		WebElement element = getControl(elementName);
		try {
			JavascriptExecutor js = ((JavascriptExecutor) webDr);
			js.executeScript("arguments[0].style.border='2px groove green'",
					element);
		} catch (Exception e) {
			logger.handleError("Failed to highlight element", elementName, " ",
					e);
		}

	}

	/**
	 * Retrieve drop down options
	 * 
	 * @param elementName
	 *            name of dropdown
	 * 
	 */
	public List<String> getDropDownOptions(String elementName) {
		List<String> optionsStr = new ArrayList<String>();
		WebElement element = getControl(elementName);
		try {
			List<WebElement> options = element.findElements(By
					.tagName("option"));

			for (WebElement option : options) {
				optionsStr.add(option.getText());

			}

		} catch (Exception e) {
			logger.handleError("Failed to get dropdown list for: ",
					elementName, " ", e);
		}

		return optionsStr;
	}

	/**
	 * checks the presence of specified options in the dropdown
	 * 
	 * @param elementName
	 *            name of element
	 * @param optionsStr
	 *            options to be checked with comma separated values
	 * 
	 * 
	 */
	public boolean checkDropDownOptions(String elementName, String optionsStr) {
		List<Object> flag = new ArrayList<Object>();
		List<String> dropDownOptions = getDropDownOptions(elementName);
		List<String> dropDownOptionsLowerCase = new ArrayList<String>();
		for (String temp : dropDownOptions) {
			dropDownOptionsLowerCase.add(temp.trim().toLowerCase());
		}
		String[] dropDownOptionsActList = optionsStr.split(",");
		try {
			for (int i = 0; i < dropDownOptionsActList.length; i++) {
				if (dropDownOptionsLowerCase.contains(dropDownOptionsActList[i]
						.trim().toLowerCase())) {
					flag.add(true);
				} else {
					flag.add(false);
				}
			}

		} catch (Exception e) {
			logger.handleError("Failed to verify option: ", optionsStr, "for ",
					elementName, " ", e);
		}
		if (flag.contains(false)) {
			return false;
		} else {

			return true;
		}
	}

	/**
	 * Checks the attribute value
	 * 
	 * @param elementName
	 *            name of element
	 * 
	 * @param attribute
	 *            attribute to be set
	 * @param value
	 *            value of the attribute
	 */

	public boolean checkAtttribute(String elementName, String attribute,
			String value) {
		String actualValue = getAttribute(elementName, attribute);
		if (value.equals(actualValue))
			return true;
		return false;
	}

	/**
	 * Sets the attribute value
	 * 
	 * @param elementName
	 *            name of element
	 * 
	 * @param attribute
	 *            attribute to be set
	 * @param value
	 *            value of the attribute
	 */
	public void setAttribute(String elementName, String attribute, String value) {
		try {
			WebElement element = getControl(elementName);
			JavascriptExecutor js = (JavascriptExecutor) webDr;
			js.executeScript(
					"arguments[0].setAttribute(arguments[1], arguments[2])",
					element, attribute, value);

		} catch (Exception e) {
			logger.handleError("Failed to set ", value, " for attribute ",
					attribute, " for the element ", elementName, " ", e);
		}
	}

	/**
	 * Gets the attribute value
	 * 
	 * @param elementName
	 *            name of element
	 * 
	 * @param attribute
	 *            attribute name
	 * */
	public String getAttribute(String elementName, String attribute) {
		WebElement element = getControl(elementName);
		try {
			String value = element.getAttribute(attribute);
			return value;
		} catch (Exception e) {
			logger.handleError("Failed to get value for attribute ", attribute,
					" for the element ", elementName, " ", e);
			return null;
		}
	}

	/**
	 * Executes Javascript
	 * 
	 * @param script
	 *            script to be executed
	 **/
	public void executeJavaScript(String script) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) webDr;
			js.executeScript(script);

		} catch (Exception e) {
			logger.handleError("Falied to execute ", script);
		}
	}

	/**
	 * For Double clicking on any element
	 * 
	 * @param elementName
	 *            name of element
	 */

	public void doubleclick(String elementName) {
		Actions axn = new Actions(webDr);
		try {
			//axn.moveToElement(getControl(elementName)).sendKeys(Keys.ENTER).build().perform();
			Thread.sleep(2000);

			axn.doubleClick(getControl(elementName)).build().perform();
		} catch (Exception e) {
			logger.handleError("Failed to double click on " + elementName, e);
		}
	}


	public void chordiantclick(String elementName) {
		Actions axn = new Actions(webDr);
		try {

			Thread.sleep(2000);
			drawHighlight(elementName);
			Thread.sleep(2000);
			getControl(elementName).sendKeys(Keys.RETURN);
			Thread.sleep(2000);
			//axn.doubleClick(getControl(elementName)).build().perform();
		} catch (Exception e) {
			logger.handleError("Failed to double click on " + elementName, e);
		}
	}

	public void tchordiantclick(String elementName) {
		Actions axn = new Actions(webDr);
		try {

			Thread.sleep(2000);
			drawHighlight(elementName);
			Thread.sleep(2000);
			getControl(elementName).sendKeys(Keys.ENTER);
			Thread.sleep(2000);
			//axn.doubleClick(getControl(elementName)).build().perform();
			//axn.moveToElement(getControl(elementName)).click(getControl(elementName)).build().perform();
			//axn.moveToElement(getControl(elementName)).doubleClick(getControl(elementName)).build().perform();


		} catch (Exception e) {
			logger.handleError("Failed to double click on " + elementName, e);
		}
	}



	/**
	 * For right clicking on any element
	 * 
	 * @param elementName
	 *            name of element
	 */

	public void rightClick(String elementName, int option) {
		Actions action = new Actions(webDr);
		Actions act = null;
		for (int i = 0; i < option - 1; i++) {
			act = action.contextClick(getControl(elementName)).sendKeys(
					Keys.ARROW_DOWN);
			act.build().perform();
		}
		act.sendKeys(Keys.ENTER).build().perform();

	}

	/**
	 * Shut downs the webdriver
	 */
	public void closeBrowsers() {
		browser.closeDriver();
	}

	/**
	 * Takes screenshot
	 */
	public File takescreenshot() {
		try {
			File scrFile = ((TakesScreenshot) webDr)
					.getScreenshotAs(OutputType.FILE);
			return scrFile;
		} catch (Exception e) {
			logger.handleError("Failed to take screenshots", e);
			return null;
		}
	}

	/**
	 * Clear cookies
	 */
	public void clearCookies() {
		try {
			webDr.manage().deleteAllCookies();
		} catch (Exception e) {

			logger.handleError("Failed to clear cookies", e);
		}
	}

	/**
	 * Check the running process and kill it
	 * 
	 * @param serviceName
	 *            Give name of the process that you want to kill
	 * @return Boolean
	 * @throws IOException
	 */

	public void killProcess(String serviceName) throws IOException {
		String line;
		Process p = Runtime.getRuntime().exec(
				System.getenv("windir") + "\\system32\\" + "tasklist.exe");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				if (line.contains(serviceName)) {
					Runtime.getRuntime().exec("taskkill /f /IM " + serviceName);
				}
			}
			input.close();
		} catch (Exception e) {
			logger.handleError("Failed to kill ", serviceName, e);

		}
	}

	/**
	 * Checks whether the page is loaded or not
	 * 
	 * @param maxTimeInSec
	 *            time to wait(In seconds)
	 * @return boolean result
	 */
	public boolean clicknavigationlinkbyname(String menuitem, String submenuitem) {
		boolean bResult = false;

		try {
			//li/a[contains(text(),'SimplyBroadband')]/..//li/a[contains(text(),'SimplyBroadband')]
			String locator_menuitem="//div[@id='tthdrsubnav']/ul/li/a[contains(text(),\'"+menuitem+"\')]";
			WebElement menuitem_link=webDr.findElement(By.xpath(locator_menuitem));
			menuitem_link.click();
			Thread.sleep(3000);
			String locator_submenuitem=locator_menuitem+"/../div[@class='navmenuwrap']//li/a[contains(text(),\'"+submenuitem+"\')]";
			//String locator_submenuitem=locator_menuitem+"/..//li/a[contains(text(),\'"+submenuitem+"\')]";
			WebElement submenuitem_link=webDr.findElement(By.xpath(locator_submenuitem));
			submenuitem_link.click();
			Thread.sleep(3000);	

		} catch (Exception e) {
			logger.handleError("Error while waiting for the page to load ", e);
			bResult = false;
		}
		return bResult;
	}


	public boolean selectPackage_agent(String menuitem) {
		boolean bResult = false;

		try {
			//li/a[contains(text(),'SimplyBroadband')]/..//li/a[contains(text(),'SimplyBroadband')]

			/*String locator_menuitem="//strong[contains(text(),'"+menuitem+"')]/../..//form//button[contains(text(),'Buy')]";
			WebElement menuitem_link=webDr.findElement(By.xpath(locator_menuitem));
			menuitem_link.click();
			Thread.sleep(3000);

			Thread.sleep(3000);*/
			String FastBroadband = "//*[@id=\"package-box-fast\"]/fieldset/div[2]/p/button";
			String FastBroadband1 = "//*[@id=\"package-box-nlfast\"]/fieldset/div[2]/p/button";									
			String FasterFibre = "//*[@id=\"package-box-nlfaster30009\"]/fieldset/div[5]/p/button";
			String FasterFibre2 =" //*[@id=\"package-box-faster30009\"]/fieldset/div[5]/p/button";
			String FasterFibre1 = "//*[@id=\"package-box-faster30009\"]/fieldset/div[8]/p/button";
			String GFast = "//*[starts-with(@id,\"package-box\")]/fieldset/div[4]/p/button";
					//"//*[@id=\"package-box-nlgfast30156\"]/fieldset/div[4]/p/button";
			//String GFast1 = "//*[@id=\"package-box-gfast30156\"]/fieldset/div[4]/p/button";
			String FTTP = "//*[@id=\"package-box-nlfastest\"]/fieldset/div[2]/p/button";

			if(menuitem.equals("Fast Broadband"))
			{
				if(isElementPresent("//*[@id=\"package-box-fast\"]/fieldset/div[2]/p/button")==true){
					webDr.findElement(By.xpath(FastBroadband)).click();Thread.sleep(6000);}
				else if(isElementPresent("//*[@id=\"package-box-nlfast\"]/fieldset/div[2]/p/button")==true){
					webDr.findElement(By.xpath(FastBroadband1)).click();Thread.sleep(6000);}
				else{
					logger.error("Fast BroadBand 'Buy' Button element not found.Check the provided element locator.");}					
			}
			else if(menuitem.equals("Fibre Broadband"))
			{
				try{
					if(isElementPresent("//*[@id=\"previous_fibreInstall_dontKnow_292_30009\"]")==true)
					{
						webDr.findElement(By.xpath("//*[@id=\"previous_fibreInstall_dontKnow_292_30009\"]")).click();
						webDr.findElement(By.xpath(FasterFibre1)).click();Thread.sleep(6000);
					}else
					{
						webDr.findElement(By.xpath(FasterFibre)).click();Thread.sleep(6000);

					}
				}
				catch(Exception e)
				{
					webDr.findElement(By.xpath(FasterFibre2)).click();Thread.sleep(6000);
				}

			}
			else if(menuitem.trim().equals("Faster 150 Fibre"))
			{			
				System.out.println(GFast);
				//webDr.findElement(By.xpath(GFast)).click();
				clickOperation(GFast);
				Thread.sleep(3000);	
			}
			else if(menuitem.equals("FTTP"))
			{			
				webDr.findElement(By.xpath(FTTP)).click();
				Thread.sleep(6000);			
			}
			else
			{
				logger.error("Didn't get the input package from the excel.");
			}

		} catch (Exception e) {
			logger.handleError("Error while waiting for the page to load ", e);
			bResult = false;
		}
		return bResult;
	}

	public static int toGetLastRowNumberofAgentPortalOrdersTestResults() throws IOException{
		int rownum=0;
		InputStream myxls = new FileInputStream(System.getProperty("user.dir")+"\\TestResults\\Orders_AgentPortal.xlsx");
		Workbook book = new XSSFWorkbook(myxls);
		Sheet sheet = book.getSheet("Results");
		//System.out.println(sheet.getLastRowNum());
		int rowsNum = sheet.getPhysicalNumberOfRows();
		System.out.println(rowsNum);
		return (rownum+1);
	}


	/**
	 * Clicks on the button based on the provided properties/attributes. Mostly used for elements which are not recognised using default methods like Click().
	 * 
	 * @param tagName
	 *            Name of the Tag. Such as input, div, etc
	 * @param type
	 * 			  "Type" Attribute. Such as button, radio, submit, etc
	 * @param value
	 * 			  "value" of the Attribute. Such as Next, Save, Save & More, Submit, etc.
	 * @return void
	 * 
	 */


	public void clickButton(String tagName, String type, String value)
	{
		List<WebElement> button= webDr.findElements(By.tagName(tagName));	
		try
		{
			for(WebElement e:button)
			{
				String eleType=e.getAttribute("type");
				String eleValue=e.getAttribute("value");
				if(eleType.equalsIgnoreCase(type) && eleValue.equalsIgnoreCase(value))
				{
					e.click();
					break;
				}
			}

		}
		catch (Exception e) 
		{
			logger.handleError("Error while waiting for the element", e);
		}
	}

	/**
	 * MovesTo and Clicks elements which are not recognised using element properties/attributes.
	 * 
	 * @param elementLocator
	 *            Value of the Element Locator
	 * @param locatorType
	 * 			  Type of the Locator
	 * @return void
	 */


	public void moveToElement(String elementLocator, String locatorType) {

		Actions action = new Actions(webDr);
		try
		{
			//By TagName
			if(locatorType.equalsIgnoreCase("TagName"))
			{
				String Ele= elementLocator;
				WebElement Ele_Option=webDr.findElement(By.tagName(Ele));
				action.moveToElement(Ele_Option).click().perform();
			}
			else
				//By xPath
				if(locatorType.equalsIgnoreCase("xPath"))
				{
					String Ele= elementLocator;
					WebElement Ele_Option=webDr.findElement(By.xpath(Ele));
					action.moveToElement(Ele_Option).click().perform();
				}
				else
					//By Class
					if(locatorType.equalsIgnoreCase("class"))
					{
						String Ele= elementLocator;
						WebElement Ele_option=webDr.findElement(By.className(Ele));
						action.moveToElement(Ele_option).click().perform();
					}
					else	
						//By LinkText
						if(locatorType.equalsIgnoreCase("linktext"))
						{
							String Ele= elementLocator;
							WebElement Ele_option=webDr.findElement(By.linkText(Ele));
							action.moveToElement(Ele_option).click().perform();
						}

						else	
							//By PartialLinkText
							if(locatorType.equalsIgnoreCase("partiallinktext"))
							{
								String Ele= elementLocator;
								WebElement Ele_option=webDr.findElement(By.partialLinkText(Ele));
								action.moveToElement(Ele_option).click().perform();
							}
							else	
								//By Name
								if(locatorType.equalsIgnoreCase("Name"))
								{
									String Ele= elementLocator;
									WebElement Ele_option=webDr.findElement(By.name(Ele));
									action.moveToElement(Ele_option).click().perform();
								}
								else	
									//By ID
									if(locatorType.equalsIgnoreCase("id"))
									{
										String Ele= elementLocator;
										WebElement Ele_option=webDr.findElement(By.id(Ele));
										action.moveToElement(Ele_option).click().perform();
									}


		}catch (Exception e) {
			logger.handleError("Error while waiting for the element", e);
		}

	}





	/**
	 * Checks whether the page is loaded or not
	 * 
	 * @param maxTimeInSec
	 *            time to wait(In seconds)
	 * @return boolean result
	 */
	public boolean waitForBrowserStability(String maxTimeInSec) {
		boolean bResult = false;
		int maxWait = Integer.parseInt(maxTimeInSec);
		int secsWaited = 0;
		try {
			do {
				Thread.sleep(100);
				secsWaited++;
				if (isBrowserLoaded()) {
					bResult = true;
					break;
				}
			} while (secsWaited < (maxWait * 10));
			Thread.sleep(100);
		} catch (Exception e) {
			logger.handleError("Error while waiting for the page to load ", e);
			bResult = false;
		}
		return bResult;
	}


	public void SelectAddress(String tablelocator,String propertyNumber)  {



		try {

			int rownumber=th.GetRow(tablelocator, propertyNumber, "Property Number");
			th.clickrelativecell(tablelocator, Integer.toString(rownumber), "Select address");

			Thread.sleep(100);
		} catch (Exception e) {
			logger.handleError("Error while waiting for the page to load ", e);

		}

	}



	public void GetRowandClick(String tablelocator,String columnnumber,String SearchString,int targetcol)  {



		try {

			int rownumber=th.GetRow(tablelocator, SearchString,columnnumber);
			th.CellClick(tablelocator, rownumber,targetcol );

			Thread.sleep(100);

		} catch (Exception e) {
			logger.handleError("Error while waiting for the page to load ", e);

		}


	}

	/**
	 * Checks if body of the page is loaded or not
	 * 
	 * @return Boolean Result
	 */
	public boolean isBrowserLoaded() {
		boolean isBrowserLoaded = false;
		try {
			long timeOut = 5000;
			long end = System.currentTimeMillis() + timeOut;

			while (System.currentTimeMillis() < end) {

				if (String.valueOf(
						((JavascriptExecutor) webDr)
						.executeScript("return document.readyState"))
						.equals("complete")) {
					isBrowserLoaded = true;
					break;

				}
			}
		} catch (Exception e) {
			logger.handleError("Failed to check for the browser to load", e);
		}
		return isBrowserLoaded;
	}

	public void toSelectItemFromList(String Xpathelement, String XlData)
	{
		try{
			Select sel = new Select(webDr.findElement(By.xpath(Xpathelement)));
			sel.selectByVisibleText(XlData);
		}catch (Exception e) {
			logger.handleError("Failed to select option from the list.", e);
		}

	}

	public int toSelectAddressindexinMPFAgentPortal() throws Exception
	{
		int addressIndex=0;
		Select sel = new Select(webDr.findElement(By.id("bt-address")));
		List<WebElement> list = sel.getOptions();
		for(int i=0;i<list.size();i++){
			if(list.get(i).getText().equals(sel.getFirstSelectedOption().getText())){
				System.out.println("The index of the selected option is: "+i);
				addressIndex = i;
				break;
			}
		}
		return addressIndex;
	}
	
	@SuppressWarnings("unused")
	public  static void takeScreenShots(String name) throws Exception
	{
		boolean makeDir = false;
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy") ;
		String curDate =dateFormat.format(date);
		File Path = new File(System.getProperty("user.dir")+ "//Screenshots//"+curDate);
		makeDir = Path.mkdir();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy,HH-mm");
		String formattedDate = sdf.format(date);
		formattedDate.toString();
		// edited for the taking screen shot in remote webdriver
		//Augmenter augmenter = new Augmenter(); 
		File scr = ((TakesScreenshot)webDr).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scr, new File(Path+"//"+ name +"_"+ formattedDate +".jpeg"));
	}
	
	public String toGetPageTitle(){
		String title = "";
		title = webDr.getTitle();
		return title;
	}

	public void toSelectTV(String IsTvIncluded,String IsTvAerialRequire,String SetupBox,String BundleBoost,String TvInstallation,String TVorRouterLocation,String IsPowerLineAdapter) throws Exception
	{
		//if(isElementPresent("//*[@id=\"tv_yes\"]")==false)
		webDr.findElement(By.xpath("//*[@id=\"options\"]/div[2]/div[3]/h2/span")).click();
		//String IsTvInclude = IsTvIncluded;
		if(IsTvIncluded.equalsIgnoreCase("No"))
			clickOperation("//input[@id=\"tv_no\"]");
		else{
			clickOperation("//input[@id=\"tv_yes\"]");
			Thread.sleep(1000);

			if(IsTvAerialRequire.equals("Yes"))
			{
				clickOperation("//*[@id=\"tv_aerial_yes\"]");
				Thread.sleep(1000);
			}
			else if(IsTvAerialRequire.equals("No")){
				clickOperation("//*[@id=\"tv_aerial_no\"]");
			}
			else
			{
				logger.error("Input not found for TV Aerial field.");			
			}
			if(SetupBox.equals("TV Box")){
				clickOperation("//label[text()=\"TV Box\"]/ancestor::div[starts-with(@class,'grid')]/div[2]/input");
				Thread.sleep(5000);
				//if(toGetText("//*[@id=\"tbox_content_5ch2nb8ybvq\"]/div/h3").equals("Select your bundle boost"))
				//{
					if(BundleBoost.equalsIgnoreCase("Sky Cinema"))
						clickOperation("//*[@id=\"tbox_content\"]/div/div/div[1]/div[1]/input");
					else if(BundleBoost.equalsIgnoreCase("Sky Sports"))
						clickOperation("//*[@id=\"tbox_content\"]/div/div/div[2]/div[1]/input");
					Thread.sleep(1000);
					clickOperation("//*[@id=\"tbox_content\"]/div/div/div[5]/button");
				//}
			}
			else if(SetupBox.equalsIgnoreCase("TV Plus Box")){
				clickOperation("//label[text()=\"TV Plus Box\"]/ancestor::div[starts-with(@class,'grid')]/div[2]/input");
				//if(toGetText("//*[@id=\"tbox_content_zojct7sdej\"]/div/h3").equals("Select your bundle boost"))
				//{
					if(BundleBoost.equalsIgnoreCase("Entertainment"))
						clickOperation("//*[@id=\"tbox_content\"]/div/div/div[1]/div/input");
					else if(BundleBoost.equalsIgnoreCase("Sky Sports"))
						clickOperation("//*[@id=\"tbox_content\"]/div/div/div[2]/div/input");
					Thread.sleep(1000);
					clickOperation("//*[@id=\"tbox_content\"]/div/div/div[5]/button");
				//}
			}
			else if(SetupBox.equalsIgnoreCase("Own Box")){
				clickOperation("//label[text()=\"Own Box\"]/ancestor::div[starts-with(@class,'grid')]/div[2]/input");
			}
			else{
				logger.error("Input not found for SetupBox field.");
			}
			if(TvInstallation.equals("Self Install")){
				clickOperation("//*[@class=\"selfInstall\"]");
			}
			else if(TvInstallation.equals("Engineer Install")){
				clickOperation("//*[@class=\"engineerInstall\"]");
			}
			else{
				logger.error("Input not found for TvInstallation field.");
			}

			if(TVorRouterLocation.equals("In the same room")){
				clickOperation("//*[@id=\"in-stock\"]/div[1]/input");
			}
			else if(TVorRouterLocation.equals("Over 3 metres apart")){
				clickOperation("//*[@id=\"in-stock\"]/div[2]/input");

			}
			else if(TVorRouterLocation.equals("In different rooms")){
				clickOperation("//*[@id=\"in-stock\"]/div[3]/input");
			}
			else if(TVorRouterLocation.equals("Decline powerline Adapter")){
				clickOperation("//*[@id=\"in-stock\"]/div[4]/input");
			}
			if(IsPowerLineAdapter.equals("Yes")){
				clickOperation("//*[@id=\"option_30076\" and @name=\"equip\"]");
			}
			else if(IsPowerLineAdapter.equals("No")){
				clickOperation("//*[@id=\"option_nopla\" and @name=\"equip\"]");
			}
		}
	}


	public String toGenerateEmailAddress(){
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmssms");
		String formattedDate = sdf.format(dt);	
		String Email = "TTAutomation"+formattedDate+"@email.com";
		System.out.println(Email);
		return Email;
	}

	public static void toBookInstallationSlot(int Appt_Next_button) throws Exception{
		List<WebElement> WE_Slot=webDr.findElements(By.tagName("input"));
		System.out.println("This is first line."+WE_Slot);
		Actions action = new Actions(webDr);
		for(WebElement ele:WE_Slot)
		{
			System.out.println(ele);	
			String eleId=ele.getAttribute("class");
			if(eleId.equals("radio"))
			{
				System.out.println("This is first RADIO line inside if condition.");
				System.out.println("Element is in enable state ---- " + ele.isEnabled());
				if(ele.isEnabled()==true){
					action.moveToElement(ele).click().perform();
					break;	
				}
			}
		}	
		//takeScreenShots(webDr.getTitle());			
		if(Appt_Next_button==1)
		{
			WebElement Appt_Next_Btn=webDr.findElement(By.tagName("button"));
			//log("Appointment Details Completed", ResultType.PASSED, "Appointment Details should be Completed successfully", "Appointment Details Completed successfully", true);
			action.moveToElement(Appt_Next_Btn).click().perform();
		}
	}









	public void toClickMPFLineRadioButton(String Package) throws Exception 
	{
		if(!Package.equals("FTTP")){
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++   " + Package);
			boolean Lines_Tab_Link;
			try {
				Lines_Tab_Link= checkAtttribute("Agent_LinesTab_Enabled", "class", "color3");	
			} catch (Exception e){
				Lines_Tab_Link=false;	
			}
			//Lines Tab -> Selecting Existing line if available.
			if(Lines_Tab_Link)
			{
				click("Agent_Select available line");
				Thread.sleep(1000);
				click("Agent_Available line button");
			}
			//Packages Tab -> Checking and Selecting appropriate package.
			Thread.sleep(10000);}
		else{
			// to provide permission to install FTTP order
			toProvidePermissionToInstall();
		}
	}

	public void toProvidePermissionToInstall() throws Exception
	{
		if(isElementPresent("//*[@id=\"wayleave_permission\"]/h2")==true){
			clickOperation("//*[@id=\"legalOwnerRadio_Yes\"]");
			Thread.sleep(2000);
			System.out.println("Clicked Yes Raido Button in permission page.");
			clickOperation("//*[@id=\"legalOwnerCheck_Yes\"]");
			Thread.sleep(2000);
			System.out.println("Accepted the TC.");
			Thread.sleep(2000);
			clickOperation("//*[@id=\"permission_yes\"]");
			Thread.sleep(5000);
		}else
		{
			logger.warning("Application not navigated to Permission to Install Page.");
		}

	}


	/**
	 * Overriding toString() method to return WebUIDriver format string
	 */
	public String toString() {
		return StringUtils.mapString(this);
	}

	public static WebDriver webDr;
	private static ObjectMap objMap;
	private Browser browser;
	public TableHandler th;
	private LogUtils logger = new LogUtils(this);
	private BaseWebModuleDriver basewebmoddr;
}
