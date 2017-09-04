package common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//import net.sourceforge.htmlunit.corejs.javascript.EcmaError;

import org.omg.CORBA.portable.ValueBase;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.google.common.collect.Iterables;
import	xlsx.Xls_Reader;




public class WebPage
{

	public static WebDriver d = null;

	public static ArrayList<String> unavailablelist;
	public static CommonMethods cm;
	//static boolean isTestPass;
	static String Date;
	
	public static void createDatabaseInstance() throws SQLException, IOException{
		System.out.println("WebDriver is connecting to  DataBase Instance.");
		Setup.APP_LOGS.info("WebDriver is connecting to  DataBase Instance.");
		
	}
	public static void createInstances()
	{
		cm=new CommonMethods();
		unavailablelist = new ArrayList<String>();
	}


	
	/**
	 * To get the webDriver Instance
	 * Where ever needed in the other class
	 * 
	 *  */
	public static WebDriver getWD()
	{
		return d;
	}
	/**
	 * To create the required browser instance.
	 * 
	 * @param Browser
	 * @return the instance of created web-driver
	 * @throws MalformedURLException
	 */

	public static WebDriver createWebDriverInstance(String Browser) throws MalformedURLException
	{
		System.out.println("Following Browser Name is received from XML file "+Browser);
		if(d==null && Browser.equals("Firefox"))
		{
			String path ="binary/geckodriver.exe";
			System.out.println(path);
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"//binary//geckodriver.exe");
			d = new FirefoxDriver();
			Setup.APP_LOGS.info("--FireFox Browser has opened ");
		}
		else if(d==null &&  Browser.equals("Chrome"))
		{
			String path ="binary/chromedriver.exe";
			System.setProperty("webdriver.chrome.driver", path);
			ChromeOptions options = new ChromeOptions();
			DesiredCapabilities caps = DesiredCapabilities.chrome();
			//caps.setJavascriptEnabled(false);
			d = new ChromeDriver(caps);
			Setup.APP_LOGS.info("--Chrome Browser has opened ");
		}

		

		return d;


	}


	/**
	 * To open the defined URL.
	 * @param URL
	 * @return and return the given URL if needed
	 * @throws Exception
	 */

	public static String openURL(String URL) throws Exception
	{
		getWD().get(URL);
		Thread.sleep(2);
		
		//getWD().manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		return URL;
	}




	/**
	 * To Wait for required amount of time for each action performed in the called method
	 * @param Seconds
	 */

	public static void wait(int Seconds)
	{
		getWD().manage().timeouts().implicitlyWait(Seconds, TimeUnit.SECONDS);
	}

	/**
	 * To Wait the Execution for required amount of seconds
	 * @param Seconds
	 * @throws InterruptedException
	 */


	public static void delay(int Seconds) throws InterruptedException
	{
		Thread.sleep(Seconds * 1000);

	}

	/**
	 * Waits for page to load
	 * @throws Exception
	 */

	public static void waitForPageToLoad() throws Exception
	{
		WebDriverWait wait = new WebDriverWait(getWD(), 9);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.xpath("//*[not (.='')]")));

	}


	/**
	 * Does not wait for the page to load .Just clicks and proceeds for the next
	 * statement.
	 * 
	 * @param elementlocator
	 * @param idtype
	 * @throws Exception
	 */

	public static void click(String elementlocator,String locatortype) throws Exception
	{
		if(locatortype=="id")
		{
			getWD().findElement(By.id(elementlocator)).click();

		}
		else if(locatortype=="X")
		{
			getWD().findElement(By.xpath(elementlocator)).click();
		}
		else if(locatortype=="CSS")
		{
			getWD().findElement(By.cssSelector(elementlocator)).click();
		}
		else if(locatortype=="Class")
		{
			getWD().findElement(By.className(elementlocator)).click();
		}
		else if(locatortype=="link")
		{
			getWD().findElement(By.linkText(elementlocator)).click();
		}
	}
	



	/**
	 * Click on the required element and waits page to load
	 * 
	 * 
	 * @param elementlocator
	 * @param locatortype
	 * @throws Exception
	 */

	public static void clickAndWait(String elementlocator , String locatortype) throws Exception
	{

		if(locatortype=="id")
		{
			getWD().findElement(By.id(elementlocator)).click();

		}

		else if(locatortype=="X")
		{
			getWD().findElement(By.xpath(elementlocator)).click();

		}
		else if(locatortype=="CSS")
		{
			getWD().findElement(By.cssSelector(elementlocator)).click();

		}
		else if(locatortype=="Class")
		{
			getWD().findElement(By.className(elementlocator)).click();

		}
		else if(locatortype=="link")
		{
			getWD().findElement(By.linkText(elementlocator)).click();

		}

		waitForPageToLoad();
	}

	/**
	 * To Find the WebElement in the web page.
	 * @param element
	 * @param idtype
	 * @return
	 */

	public static WebElement getWebElement(String elementlocator, String locatortype)
	{
		WebElement element=null;

		if(locatortype== "X")
		{
			element= getWD().findElement(By.xpath(elementlocator));
		}
		else if(locatortype=="CSS")
		{
			element= getWD().findElement(By.cssSelector(elementlocator));
		}
		else if(locatortype=="id")
		{
			element= getWD().findElement(By.id(elementlocator));
		}
		else if(locatortype=="Class")
		{
			element= getWD().findElement(By.className(elementlocator));
		}
		else if(locatortype=="link")
		{
			element= getWD().findElement(By.linkText(elementlocator));
		}
		return element;

	}


	/**
	 * To Maximize the window.
	 */

	public static void maximize()
	{
		getWD().manage().window().maximize();
	}


	/**
	 * To Close the Instance of the Created WebDriver and assign the NULL.
	 */

	public static void close()
	{
		getWD().close();
		getWD().quit();
		d = null;
	}


	/**
	 * Returns the visible text of the element.
	 * 
	 * @param elementlocator
	 * @param locatortype
	 * @return
	 * @throws Exception
	 */

	public static String getText(String elementlocator , String locatortype) throws Exception
	{
		String Text ="";

		if(locatortype == "id")
		{
			Text =getWD().findElement(By.id(elementlocator)).getText();
			/*if(WebPage.isVisible(elementlocator, locatortype)){
			Text =getWD().findElement(By.id(elementlocator)).getText();
			}
			else
				throw new Exception("Following element locator is not able to locate"+" "+elementlocator);*/
		}

		else if(locatortype == "X")
		{
			Text =getWD().findElement(By.xpath(elementlocator)).getText();
			//System.out.println();
			/*if(WebPage.isVisible(elementlocator, locatortype))
			Text =getWD().findElement(By.xpath(elementlocator)).getText();
			else
				throw new Exception("Following element locator is not able to locate"+" "+elementlocator);*/
		}

		else if(locatortype == "CSS")
		{
			Text =getWD().findElement(By.cssSelector(elementlocator)).getText();
		}

		else if (locatortype == "Class")
		{
			Text =getWD().findElement(By.className(elementlocator)).getText();
		}

		else if(locatortype == "link")
		{
			Text=getWD().findElement(By.linkText(elementlocator)).getText();
		}

		return Text;
	}

	/**
	 * Returns the visible text of the element.
	 * 
	 * @param elementlocator
	 * @param locatortype
	 * @return
	 * @throws Exception
	 */

	public static String getValue(String elementlocator , String locatortype) throws Exception
	{
		String Text =null;

		if(locatortype == "id")
		{
			delay(2);
			Text =getWD().findElement(By.id(elementlocator)).getAttribute("value");
		}

		else if(locatortype == "X")
		{
			delay(2);
			Text =getWD().findElement(By.xpath(elementlocator)).getAttribute("value");
		}

		else if(locatortype == "CSS")
		{
			delay(2);
			Text =getWD().findElement(By.cssSelector(elementlocator)).getAttribute("value");
		}

		else if (locatortype == "Class")
		{
			delay(2);
			Text =getWD().findElement(By.className(elementlocator)).getAttribute("value");
		}

		else if(locatortype == "link")
		{
			delay(2);
			Text=getWD().findElement(By.linkText(elementlocator)).getAttribute("value");
		}

		return Text;
	}

	/**
	 * Check a toggle-button (checkbox/radio)
	 * 
	 * @param elementlocator
	 * @param locatortype
	 */

	public static void check(String elementlocator , String locatortype)
	{
		WebElement element;
		if(locatortype == "id")
		{
			element =getWD().findElement(By.id(elementlocator));
			element.click();
		
		}

		else if(locatortype == "X")
		{
			element =getWD().findElement(By.xpath(elementlocator));
			element.click();
		}

		else if(locatortype == "CSS")
		{
			element =getWD().findElement(By.cssSelector(elementlocator));
			element.click();
		}

		else if (locatortype == "Class")
		{
			element =getWD().findElement(By.className(elementlocator));
			element.click();
		}

		else if(locatortype == "link")
		{
			element =getWD().findElement(By.linkText(elementlocator));
			element.click();
		}
	}

	/**
	 * Uncheck a toggle-button (checkbox/radio)
	 * if element is selected
	 * 
	 * 
	 * @param elementlocator
	 * @param locatortype
	 * @throws Exception
	 */

	public static void uncheck(String elementlocator , String locatortype) throws Exception
	{
		WebElement element =null;
		if(locatortype == "id")
		{
			element =getWD().findElement(By.id(elementlocator));
			if(element.isSelected())
			{
				element.click();
			}
		}

		else if(locatortype == "X")
		{
			element =getWD().findElement(By.xpath(elementlocator));
			if(element.isSelected())
			{
				element.click();
			}
		}

		else if(locatortype == "CSS")
		{
			element =getWD().findElement(By.cssSelector(elementlocator));
			if(element.isSelected())
			{
				element.click();
			}
		}

		else if (locatortype == "Class")
		{
			element =getWD().findElement(By.className(elementlocator));
			if(element.isSelected())
			{
				element.click();
			}
		}

		else if(locatortype == "link")
		{
			element =getWD().findElement(By.linkText(elementlocator));
			if(element.isSelected())
			{
				element.click();
			}
		}

	}

	/**
	 * To Select the visible text option from the Dropdown
	 * present in the webpage.
	 * 
	 * 
	 * @param selectlocator
	 * @param locatortype
	 * @param optionText
	 * @throws Exception
	 */


	public static void select(String selectlocator , String locatortype , String optionText) throws Exception
	{
		WebElement element=null;
		if(locatortype == "id")
		{
			element =getWD().findElement(By.id(selectlocator));
			new Select(element).selectByVisibleText(optionText);
		}

		else if(locatortype == "X")
		{
			element =getWD().findElement(By.xpath(selectlocator));
			new Select(element).selectByVisibleText(optionText);
		}

		else if(locatortype == "CSS")
		{
			element =getWD().findElement(By.cssSelector(selectlocator));
			new Select(element).selectByVisibleText(optionText);
		}

		else if (locatortype == "Class")
		{
			element =getWD().findElement(By.className(selectlocator));
			new Select(element).selectByVisibleText(optionText);
		}

		else if(locatortype == "link")
		{
			element =getWD().findElement(By.linkText(selectlocator));
			new Select(element).selectByVisibleText(optionText);
		}


	}


	/**
	 * to get the selected value from the drop down list.
	 * 
	 * @param selectlocator
	 * @param locatortype
	 * @return
	 */

	public static String isSelected(String selectlocator , String locatortype)
	{
		WebElement element=null;
		if(locatortype == "id")
		{
			element =getWD().findElement(By.id(selectlocator));
		}

		else if(locatortype == "X")
		{
			element =getWD().findElement(By.xpath(selectlocator));
		}

		else if(locatortype == "CSS")
		{
			element =getWD().findElement(By.cssSelector(selectlocator));
		}

		else if (locatortype == "Class")
		{
			element =getWD().findElement(By.className(selectlocator));
		}

		else if(locatortype == "link")
		{
			element =getWD().findElement(By.linkText(selectlocator));
		}
		return new Select(element).getFirstSelectedOption().getText();
	}


	/**
	 * Simulates a user hovering a mouse over the specified element.
	 * 
	 * 
	 * @param elementlocator
	 * @param locatortype
	 */

	public static void mouseOver(String elementlocator,String locatortype)
	{
		WebElement element;
		Actions builder = new Actions(getWD());

		if(locatortype == "id")
		{
			element =getWD().findElement(By.id(elementlocator));
			builder.moveToElement(element).build().perform();
		}

		else if(locatortype == "X")
		{
			element =getWD().findElement(By.xpath(elementlocator));
			builder.moveToElement(element).build().perform();
		}

		else if(locatortype == "CSS")
		{
			element =getWD().findElement(By.cssSelector(elementlocator));
			builder.moveToElement(element).build().perform();
		}

		else if (locatortype == "Class")
		{
			element =getWD().findElement(By.className(elementlocator));
			builder.moveToElement(element).build().perform();
		}

		else if(locatortype == "link")
		{
			element =getWD().findElement(By.linkText(elementlocator));
			builder.moveToElement(element).build().perform();
		}


	}

	/**
	 * Simulates a user hovering a mouse over the specified element.
	 * and click on the particular element.
	 * 
	 * @param elementlocator
	 * @param locatortype
	 */

	public static void mouseOverClick(String elementlocator,String locatortype)
	{
		Actions builder = new Actions(getWD());
		WebElement element;
		if(locatortype == "id")
		{
			element = getWD().findElement(By.id(elementlocator));
			builder.moveToElement(element).click().build().perform();
		}

		else if(locatortype == "X")
		{
			element =getWD().findElement(By.xpath(elementlocator));
			builder.moveToElement(element).click().build().perform();
		}

		else if(locatortype == "CSS")
		{
			element =getWD().findElement(By.cssSelector(elementlocator));
			builder.moveToElement(element).click().build().perform();
		}

		else if (locatortype == "Class")
		{
			element =getWD().findElement(By.className(elementlocator));
			builder.moveToElement(element).click().build().perform();
		}

		else if(locatortype == "link")
		{
			element =getWD().findElement(By.linkText(elementlocator));
			builder.moveToElement(element).click().build().perform();
		}

	}

	/**
	 * Gets whether a toggle-button (checkbox/radio) is checked.
	 * 
	 * 
	 * @param elementlocator
	 * @param locatortype
	 * @return true if the checkbox is checked, otherwise false
	 * @throws Exception
	 */

	public static boolean isChecked(String elementlocator , String locatortype) throws Exception
	{
		WebElement element=null;
		if(locatortype == "id")
		{
			element =getWD().findElement(By.id(elementlocator));

		}

		else if(locatortype == "X")
		{
			element =getWD().findElement(By.xpath(elementlocator));

		}

		else if(locatortype == "CSS")
		{
			element =getWD().findElement(By.cssSelector(elementlocator));

		}

		else if (locatortype == "Class")
		{
			element =getWD().findElement(By.className(elementlocator));

		}

		else if(locatortype == "link")
		{
			element =getWD().findElement(By.linkText(elementlocator));

		}

		return element.isSelected();

	}

	/**
	 * Gets whether a webelement is displayed or not.
	 * 
	 * 
	 * @param elementlocator
	 * @param locatortype
	 * @return true if the specified element is visible,otherwise false
	 * @throws Exception
	 */
	public static boolean isVisible(String elementlocator , String locatortype) throws Exception
	{
		try
		{

			WebElement element=null;
			if(locatortype.equalsIgnoreCase("id"))
			{
				element =getWD().findElement(By.id(elementlocator));
			}

			else if(locatortype.equalsIgnoreCase("X"))
			{
				//SetMesage("executing the X Path block ");
				element =getWD().findElement(By.xpath(elementlocator));

			}

			else if(locatortype == "CSS")
			{
				element =getWD().findElement(By.cssSelector(elementlocator));

			}

			else if (locatortype == "Class")
			{
				element =getWD().findElement(By.className(elementlocator));

			}

			else if(locatortype == "link")
			{
				element =getWD().findElement(By.linkText(elementlocator));

			}
			//System.out.println("End of the is visible method"+" "+element);
			return element.isDisplayed();
		}

		catch(Exception e)
		{ 
			//SetMesage(elementlocator+ "is not visible");
			return false;
		}
	}

		
	
	
	
	
	public static ArrayList<String> getUnAvailableData(){

		return unavailablelist;

	}



	
	public static boolean isMessageValid(String Property, String PropertyType, String msg) throws Exception{
		
		if(WebPage.getText(Property,PropertyType).trim().contentEquals(msg.trim())){
			System.out.println(WebPage.getText(Property,PropertyType)+" "+"is matched with"+" "+msg);
			Setup.APP_LOGS.info(WebPage.getText(Property,PropertyType)+" "+"is matched with"+" "+msg);
			return true;
		}

		else{
			System.out.println(WebPage.getText(Property,PropertyType)+ " "+"is not matched with"+" "+msg);
			Setup.APP_LOGS.info(WebPage.getText(Property,PropertyType)+" "+"is not matched with"+" "+msg);
			String a=msg;
			System.out.println(a);
			unavailablelist.add(msg);
			return false;
		}
	}

	public static boolean compareTwoStrings(String FirstString, String SecondString) throws Exception{
		boolean K=false;
		if(FirstString.trim().contentEquals(SecondString.trim())){
			SetMesage("Two Strings are matched and see the Actual and Expected Strings"+"::"+"Actual String is"+":"+FirstString+""+"..."+"Expected String is"+":"+SecondString);
			K=true;
		}
		else{
			SetMesage("Two Strings do not matched and see the Actual and Expected Strings"+"::"+"Actual String is"+":"+FirstString+""+"..."+"Expected String is"+":"+SecondString);
		}
		return K;
	}
	
	
	
	public static boolean messagesvalid(ArrayList<String> ermsgprpties, String PropertyType, ArrayList<String> UIMessages ) throws Exception{
		try{
			//unavailablelist=new ArrayList<>();
			//System.out.println("executing the method messages valid");
			System.out.println(ermsgprpties);
			System.out.println(ermsgprpties.size());
			System.out.println(PropertyType);
			int k=1;
			//unavailablelist.clear();
			if(ermsgprpties.size()==UIMessages.size()&& ermsgprpties.size()>0 && UIMessages.size()>0){
			for(int i=0;i<ermsgprpties.size();i++){
				//System.out.println("Executig for loop");
				if(isVisible(ermsgprpties.get(i), "X")){
					//System.out.println("message is displayed in the screen");
					for(int j=0;j<UIMessages.size();j++){
						if(isMessageValid(ermsgprpties.get(i), PropertyType, UIMessages.get(j))){
							System.out.println("Validation message is displaying properly");
							Setup.APP_LOGS.info("Validation message is displaying properly");
							break;
						}
						else{
							//unavailablelist.add(WebPage.getText(UIMessages.get(j).trim(), PropertyType.trim()));
							if(j==UIMessages.size()-1){
								System.out.println(UIMessages.get(j-1)+","+UIMessages.get(j));
								unavailablelist.add(UIMessages.get(j).toString());
							k=0;
						}
						}
					}
				}
				else{
					System.out.println("Validation message is not displaying in the grid"+""+"and property is"+""+ermsgprpties.get(i));
					Setup.APP_LOGS.info("Validation message is not displaying in the grid");
					unavailablelist.add(ermsgprpties.get(i).trim());
					k=0;
				}
			}
			Setup.APP_LOGS.info(unavailablelist+""+"Messages which are not displayed properly and properties of undisplayed error messages");
		}
			else{
				Setup.APP_LOGS.info("Given Two lists sizes are not matched:"+""+"First List"+""+ermsgprpties+""+"Second List"+UIMessages);
				k=0;
			}
			if(k==0)
				return false;
			else
				return true;
		}
		catch(Exception e){
			
			System.out.println(e+"exception" );
			return false;
		}

	}


	/*public static String put(String b)
	{
		String c= Setup.VBPFO.getProperty(b);
		return c;
	}*/
	/**
	 * To sendkeys in to any text box present on webelement
	 * 
	 * 
	 * @param elementlocator
	 * @param locatortype
	 * @param valuetotype
	 */

	public static void sendKeys(String elementlocator , String locatortype , String valuetotype)
	{

		if(locatortype == "id")
		{
			getWD().findElement(By.id(elementlocator)).sendKeys(valuetotype);
		}

		else if(locatortype == "X")
		{
			getWD().findElement(By.xpath(elementlocator)).sendKeys(valuetotype);
		}

		else if(locatortype == "CSS")
		{
			getWD().findElement(By.cssSelector(elementlocator)).sendKeys(valuetotype);
		}

		else if (locatortype == "Class")
		{
			getWD().findElement(By.className(elementlocator)).sendKeys(valuetotype);
		}

		else if(locatortype == "link")
		{
			getWD().findElement(By.linkText(elementlocator)).sendKeys(valuetotype);
		}
		else if(locatortype == "name")
		{
			getWD().findElement(By.name(elementlocator)).sendKeys(valuetotype);
		}
	}

	/**
	 * Returns the title of the current active window
	 * 
	 * @return title of the open web page
	 * @throws Exception
	 */

	public static String getTitle() throws Exception
	{
		//Setup.APP_LOGS.info("Actual Page Title is - " + getWD().getTitle());
		return getWD().getTitle();
	}


	/**
	 * To Get the List of WebElement
	 * 
	 * 
	 * @param elementlocator
	 * @param locatortype
	 * @return
	 */

	public static List<WebElement> getWebElementList(String elementlocator , String locatortype)
	{
		List<WebElement> elementlist=null;
		if(locatortype == "id")
		{
			elementlist =getWD().findElements(By.id(elementlocator));

		}

		else if(locatortype == "X")
		{
			elementlist =getWD().findElements(By.xpath(elementlocator));

		}

		else if(locatortype == "CSS")
		{
			elementlist =getWD().findElements(By.cssSelector(elementlocator));

		}

		else if (locatortype == "Class")
		{
			elementlist =getWD().findElements(By.className(elementlocator));

		}

		else if(locatortype == "link")
		{
			elementlist =getWD().findElements(By.linkText(elementlocator));

		}
		return elementlist;
	}


	/**
	 * To get the Xpath count of
	 * any element present in the web page
	 * 
	 * @param elementlocator
	 * @return
	 * @throws Exception
	 */

	public static int getXPathCount(String elementlocator) throws Exception
	{

		return getWD().findElements(By.xpath(elementlocator)).size();
	}


	/**
	 * To switch the cursor the defined iframe located in the web page
	 * 
	 * @param framelocator
	 * @param locatortype
	 */

	public static void switchFrame(String framelocator , String locatortype)
	{
		WebElement element;
		if(locatortype == "id")
		{
			element =getWD().findElement(By.id(framelocator));
			getWD().switchTo().frame(element);
		}

		else if(locatortype == "X")
		{
			element =getWD().findElement(By.xpath(framelocator));
			getWD().switchTo().frame(element);
		}

		else if(locatortype == "CSS")
		{
			element =getWD().findElement(By.cssSelector(framelocator));
			getWD().switchTo().frame(element);
		}

		else if (locatortype == "Class")
		{
			element =getWD().findElement(By.className(framelocator));
			getWD().switchTo().frame(element);
		}

		else if(locatortype == "link")
		{
			element =getWD().findElement(By.linkText(framelocator));
			getWD().switchTo().frame(element);
		}
	}

	/**
	 * To switch the cursor the default web page.
	 */

	public static void switchToDefaultConatiner()
	{
		getWD().switchTo().defaultContent();
	}


	/**
	 * To verify the alert present in the web page
	 * 
	 * 
	 * @return true if alert present , other wise false.
	 * @throws Exception
	 */

	public static boolean isAlertPresent() throws Exception {

		try
		{
			WebDriverWait wait = new WebDriverWait(getWD(), 5);
			if(wait.until(ExpectedConditions.alertIsPresent())!=null){
			SetMesage("Alert is present");
			return true;
			}
			else{
				SetMesage("Alert is not present");
				return false;
			}
		}
		catch(Exception e)
		{
			System.out.println("false");
			return false;
		}

	}


	/**
	 * to get the values in the drop down
	 * @param Property
	 * @param PropertyType
	 * @return
	 */
	public static ArrayList<String> getDropDownValue(String Property , String PropertyType)
	{
		System.out.println(Property);
		System.out.println(PropertyType);
		ArrayList<String> ddlvalues = new ArrayList<String>();
		WebElement SelectDDL = WebPage.getWebElement(Property, PropertyType);
		Select sel = new Select(SelectDDL);
		List<WebElement> values = sel.getOptions();
		for(WebElement opt : values)
		{
			ddlvalues.add(opt.getText().trim());
		}
		return ddlvalues;

	}

	/**
	 * To get the text present on the Alert
	 * or confirmation box.
	 * 
	 * @return
	 * @throws Exception
	 */

	public static String getAlertText() throws Exception{
		String alerttext = null;

		alerttext =getWD().switchTo().alert().getText();

		return alerttext;
	}

	/**
	 * To click OK on the Confirmation box
	 * or accept the alert
	 * 
	 * @throws Exception
	 */

	public static void chooseOkOnNextConfirmation() throws Exception
	{
		getWD().switchTo().alert().accept();
	}

	/**
	 * To Click No on the confirmation box.
	 * or to dismiss alert
	 * 
	 * @throws Exception
	 */

	public static void chooseNoOnNextConfirmation() throws Exception
	{
		getWD().switchTo().alert().dismiss();
	}


	/**
	 * to handle windows
	 * @return
	 */
	/**
	 * To read the Text on the confirmation box.
	 * or to dismiss alert
	 * 
	 * @throws Exception
	 */


	public static String windowHandle()
	{
		String parentWindow = WebPage.getWD().getWindowHandle();

		for(String childWindow: WebPage.getWD().getWindowHandles())
		{
			WebPage.getWD().switchTo().window(childWindow);
		}
		return parentWindow;
	}

	/**
	 * to Switch between windows
	 * @param Window
	 */
	public static void switchWindow(String Window)
	{
		getWD().switchTo().window(Window);
	}

	/**
	 * to execute java scripts
	 * @param Code
	 */

	public static void javaScrpitExcecutor(String Code)
	{
		JavascriptExecutor javascript = (JavascriptExecutor) WebPage.getWD();
		javascript.executeScript(""+Code+"");
	}


	/**
	 * to generate Alert with
	 * user defined message
	 * @param Message
	 * @param secondstoAccept
	 * @throws Exception
	 */
	public static void generateAlertandAccept(String Message, int secondstoAccept) throws Exception
	{
		javaScrpitExcecutor("alert('"+Message+"');");
		delay(secondstoAccept);
		chooseOkOnNextConfirmation();
	}

	
	public static Map<String, Object> getCellDataFromCustomerExceltoMap(String SheetName,String ColName1,String ColName2, String ColName3, int CellNo, xlsx.Xls_Reader xlsobject){
		//List<String> container = new ArrayList<String>();
		Map<String, Object> Usermap= new HashMap<String, Object>();
		String TDuserName=xlsobject.getCellData(SheetName, ColName1, CellNo);
		String TDpassword=xlsobject.getCellData(SheetName, ColName2, CellNo);
		Usermap.put("Loginusername",TDuserName);
		Usermap.put("LoginPassword",TDpassword);
		return Usermap;
	}
	public static void AccountSetup(String optype) throws Exception{
/*		if(optype.contentEquals("Exit"))
		{
			String ExitSetup=Setup.VBPFO.getProperty("ExitSetupWizard");
			WebPage.click(ExitSetup, "X");

		}
		else if (optype.contentEquals("continue")){
			String Continue=Setup.VBPFO.getProperty("Continue");
			WebPage.click(Continue, "X");
		}*/



	}
	public static void selectmenu(){
		//By.id("menu_aSearchPayments");
		WebElement DropDown = d.findElement(By.id("menu_aSearchPayments"));
		//Select CompanyOptions = new Select(d.findElement(By.id("menu_aVendors")));

		//System.out.println("Company Options"+DropDown);
		//DropDown.sendKeys("ALL");
	}


	public static void mouseclick(WebElement a,WebElement b)
	{
		try
		{
			String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
			((JavascriptExecutor) getWD()).executeScript(mouseOverScript,a);
			Thread.sleep(1000);
			((JavascriptExecutor) getWD()).executeScript(mouseOverScript,b);
			Thread.sleep(1000);
			((JavascriptExecutor) getWD()).executeScript("arguments[0].click();",b);


		} catch (Exception e) {

		}
	}
	
	
	
	
	
	public static boolean isDefaultSelectedItemisMatched(String Item, WebElement we){
		try{
			Select sel = new Select(we);
			if(sel.getFirstSelectedOption().getText().equals(Item)){
				WebPage.SetMesage("Yes, Default selected value is"+sel.getFirstSelectedOption().getText());
				return true;
			}
			else{
				WebPage.SetMesage("No, Default selected value is"+sel.getFirstSelectedOption().getText());
				return false;
			}
		}
		catch(Exception e){
			return false;
		}
	}
	
	public static void selectListItem (String elementlocator, String locatortype,String value){
		try{
			Select sel = new Select(getWebElement(elementlocator, locatortype) );
			sel.selectByVisibleText(value);
		}
		catch(Exception e)
		{
			System.out.println("Not able to select");
		}
	}
	public static void selectListItem(WebElement we,String value){
		try{
			Select sel = new Select(we);
			System.out.println(sel);
			sel.selectByIndex(1);
			sel.selectByVisibleText(value);
		}
		catch(Exception e)
		{
			System.out.println("Not able to select");
		}
	}
	public static void fillElement(String elementlocator, String locatortype,String value) throws InterruptedException, Exception{
	
			if(isVisible(elementlocator, locatortype)){
				WebElement webElement = getWebElement(elementlocator, locatortype);
				webElement.click();
				webElement.clear();
				WebPage.delay(1);
				sendKeys(elementlocator, locatortype, value);
				WebPage.delay(1);
				
			}
			else{
				WebPage.SetFailedMesage("Element is not visible");
			}
		
		
	}

	public static void sendKeys(WebElement we, String value){
		we.sendKeys(value);
	}

	
	
	public static List<String> getListItemsText( List<WebElement> WE)
	{
		List<String>a= new ArrayList<>();
		for (WebElement element: WE) {
			a.add(element.getText());
		}
		return a;
	}

	/**
	 * To clear the Data
	 * @param elementlocator
	 * @param locatortype
	 * @throws Exception
	 */
	public static void Clear(String elementlocator,String locatortype) throws Exception
	{
		if(locatortype=="id")
		{
			getWD().findElement(By.id(elementlocator)).clear();
		}
		else if(locatortype=="X")
		{
			getWD().findElement(By.xpath(elementlocator)).clear();
		}
		else if(locatortype=="CSS")
		{
			getWD().findElement(By.cssSelector(elementlocator)).clear();
		}
		else if(locatortype=="Class")
		{
			getWD().findElement(By.className(elementlocator)).clear();
		}
		else if(locatortype=="link")
		{
			getWD().findElement(By.linkText(elementlocator)).clear();
		}
	}

	

	

	public static Iterable<Object> removeDuplicateData(Map<String, Object> map1, Map<String, Object> map2 ){
		Map<String, Object>map3= new HashMap<>();
		map3.putAll(map1);

		Collection<Object> c1 = map1.values();
		Collection<Object> c2 = map2.values();
		Collection<Object> c3 = map3.values();

		c1.removeAll(c2);  // this one removes all the values from c1 which are also in map2.
		c2.removeAll(c3);
		//c1.addAll(c2);
		//System.out.println(c1);

		final Iterable<Object> all =
				Iterables.unmodifiableIterable(
						Iterables.concat(c1, c2));
		System.out.println(all);
		//System.out.println(c2);
		//c2.addAll(c1);



		return all;
	}
	public static void putFailedMessage(String Message) throws Exception
	{
		System.out.println(Message);
		Setup.APP_LOGS.info(Message);
		//throw new Exception(Message);
	}
	public static void SetMesage(String Message) throws Exception
	{
		System.out.println(Message);
		Setup.APP_LOGS.info(Message);
		//throw new Exception(Message);
	}
	public static void SetFailedMesage(String Message) throws Exception
	{
		System.out.println(Message);
		Setup.APP_LOGS.info(Message);
		throw new Exception(Message);
	}
	
	public static void catchBlockData(String methodName, String browser, Exception e, Xls_Reader Excelobject,String ClassName) throws Exception
	{
		System.out.println("method name - " + methodName);
		new CommonMethods().takeAlertScreenShot(methodName);
		if(isAlertPresent())
		{
		  Setup.APP_LOGS.info("Alert is displaying - " + getAlertText());
		  chooseOkOnNextConfirmation();
		}
		boolean isTestPass=false;
		new CommonMethods().reportTestResult(Excelobject,browser,methodName, isTestPass);
		Setup.pdf.table(ClassName,methodName,"Fail");
		//System.out.println(e+"Exception");
		throw e;
	}
	
	public static void tryBlockData(String methodName, String browser, Xls_Reader Excelobject,String ClassName) throws Exception
	{
		if(isAlertPresent())
		{
		  Setup.APP_LOGS.info("Alert is displaying - " + getAlertText());
		  chooseOkOnNextConfirmation();
		}
		
		boolean isTestPass=true;
		new CommonMethods().reportTestResult(Excelobject,browser,methodName, isTestPass);
		Setup.pdf.table(ClassName,methodName,"Pass");	
		//Setup.APP_LOGS.info("Executed " + methodName);
	}
	
	public static void tryBlockDatawithoutcancellinganyAlerts(String methodName, String browser, Xls_Reader Excelobject,String ClassName) throws Exception
	{
		boolean isTestPass=true;
		cm.reportTestResult(Excelobject,browser,methodName, isTestPass);
		Setup.pdf.table(ClassName,methodName,"Pass");	
		//Setup.APP_LOGS.info("Executed " + methodName);
	}
	
	public static void WriteSucessUpdates(String methodName, String browser, Xls_Reader Excelobject,String ClassName) throws Exception
	{
		if(isAlertPresent())
		{
		  Setup.APP_LOGS.info("Alert is displaying - " + getAlertText());
		  chooseOkOnNextConfirmation();
		}
		//cm.takeAlertScreenShot(methodName);
		boolean isTestPass=true;
		cm.reportTestResult(Excelobject,browser,methodName, isTestPass);
		Setup.pdf.table(ClassName,methodName,"Passed");
		//throw e;
	}
	
	

	public static String changeDateFormatToMMDDYYY(String Date) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy");
		String Date1 =sdf1.format(sdf.parse (Date));
		return Date1;
	}
	public boolean compareTwoArrays(List<String> A, List<String>B) throws Exception{
		ArrayList<String> c=new ArrayList<>();
		c.addAll(A);
		if(A.size()==B.size()&& A.size()>0 && B.size()>0){
		Object[] arr1 = {A};  // arr1 contains only one element
		Object[] arr2 = {B};  // arr2 also contains only one element
		if (Arrays.deepEquals(arr1, arr2))
			return true;
		else
			return false;
		}
		else{
		//System.out.println("Arrays Sizes are may not be equal or one of the array has no values"+A+B);
		WebPage.putFailedMessage("Arrays Sizes are may not be equal or one of the array has no values"+A.removeAll(B)+B.removeAll(c));
			return false;
		}
	}
	public static void SelectTextFromDropDown(String Property , String PropertyType, String Text)
	{
	/*	System.out.println(Property);
		System.out.println(PropertyType);
	*/	ArrayList<String> ddlvalues = new ArrayList<String>();
		WebElement SelectDDL = WebPage.getWebElement(Property, PropertyType);
		Select sel = new Select(SelectDDL);
		sel.selectByVisibleText(Text);
	/*	List<WebElement> values = sel.getOptions();
		for(WebElement opt : values)
		{
			ddlvalues.add(opt.getText().trim());
		}
		return ddlvalues;
*/
	}
	
	/**
	 * To Perform Tab Navigation
	 * @throws InterruptedException 
	 */
	
	public static void toPerformTABoperation() throws InterruptedException
	{
		Actions Act=new Actions(getWD());
		Act.sendKeys(Keys.TAB);
		delay(1);
	}
	public static String trimSpaceInTheString(String text) {
		// TODO Auto-generated method stub
		return text.replaceAll("^\\s+","").replaceAll("\\s+$","");
	}
	

	
}

