package ModuleDrivers;

import static cbf.engine.TestResultLogger.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByPartialLinkText;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

//import com.sun.org.apache.bcel.internal.generic.GOTO;

import cbf.engine.TestResult.ResultType;
import cbf.reporting.ExcelReporter;
import cbf.utils.DataRow;
import cbf.utils.LogUtils;
import cbfx.BaseWebDriver.BaseWebModuleDriver;


public class SF_MPFNewProvideDriver extends BaseWebModuleDriver{
	
	public void LaunchApp(DataRow input, DataRow output) throws IOException
	{
		uiDriver.launchApplication(input.get("url"));
		if(uiDriver.checkPage("Login | Salesforce"))
				
			{			
				log("Salesforce Portal Launch", ResultType.PASSED, "Salesforce Portal Launch should be Completed successfully", "Salesforce Portal Launch Completed successfully", true);
			}
		
		else
			{
				log("Salesforce Customer Portal App", ResultType.FAILED, "Salesforce Portal App should be launched successfully", "Salesforce Portal App not launched successfully", true);
			}
		
	}
	
	
	public void SFLogin(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		Actions action = new Actions(uiDriver.webDr);
		String parentWindow=uiDriver.webDr.getWindowHandle();
		try{	
			//Login to SF portal
		WebElement WE_UserName = uiDriver.webDr.findElement(By.id("username"));
		WE_UserName.sendKeys(input.get("UserName"));
		cbf.utils.SleepUtils.sleep(3);
		
		
		WebElement WE_Password = uiDriver.webDr.findElement(By.id("password"));
		WE_Password.sendKeys(input.get("Login Password"));
		
		WebElement WE_Remember = uiDriver.webDr.findElement(By.id("rememberUn"));
		WE_Remember.click();
		uiDriver.clickButton("input", "submit", "Log In to Sandbox");
		
		//Login to SF Portal is completed.
		cbf.utils.SleepUtils.sleep(7);
		log("Salesforce Login", ResultType.PASSED, "Salesforce Login Should be Completed successfully", "Salesforce Login Completed successfully", true);
		
		}catch (Exception e) {
			log("Salesforce Login", ResultType.FAILED, "Salesforce Login should be successful", "Salesforce Login Failed", true);
		}
		
		
		
		//Creating Opportunities.
		try
		{
		uiDriver.moveToElement("Opportunities", "linktext");
		uiDriver.moveToElement("new","name");
		cbf.utils.SleepUtils.sleep(3);
		
		List<WebElement> Prod_Rcrd_Type =uiDriver.webDr.findElements(By.xpath("//table/tbody/tr[1]/td[2]/div/select"));
		
		for(WebElement ele:Prod_Rcrd_Type)
		{
				
				Select select=new Select(ele);
				
				if(input.get("Record Type").equalsIgnoreCase("Connect"))
				{
					select.selectByIndex(0);	
				}
				else{
				select.selectByIndex(1);
				}
				cbf.utils.SleepUtils.sleep(3);
				break;
			}
	//Update this with MoveToElement and make sure it works			
		List<WebElement> WE_Opp_Continue=uiDriver.webDr.findElements(By.tagName("input"));
		
		for(WebElement ele:WE_Opp_Continue)
		{
			
			String eleValue=ele.getAttribute("value");
			
				if(eleValue.equals("Continue"))
				{
			
					action.moveToElement(ele).click().perform();
					cbf.utils.SleepUtils.sleep(3);
					break;	
				}
		}
		
				
		WebElement WE_Opp_Name=uiDriver.webDr.findElement(By.name("opp3"));
		WE_Opp_Name.sendKeys(input.get("Opportunity Name"));
		
		WebElement WE_Opp_Cmp_Name=uiDriver.webDr.findElement(By.id("opp4"));
		WE_Opp_Cmp_Name.sendKeys(input.get("Company Name"));
		
		WebElement WE_Opp_Cmp_Name_Search=uiDriver.webDr.findElement(By.id("opp4_lkwgt"));
		WE_Opp_Cmp_Name_Search.click();
		
		cbf.utils.SleepUtils.sleep(2);
		
		Set<String> handles =  uiDriver.webDr.getWindowHandles();
		   for(String windowHandle  : handles)
		       {
		       if(!windowHandle.equals(parentWindow))
		          {
		    	   	uiDriver.switchToWindow(windowHandle);
		    	   	
		    	   	uiDriver.webDr.switchTo().frame("resultsFrame");
		    	   	System.out.println("FRAME SWITCHING");
		    		uiDriver.moveToElement("//div/div[2]/div/div[2]/table/tbody/tr[2]/th/a", "xPath");
		    	   	break;
		    	   	
		          }
		       }
		
		   uiDriver.webDr.switchTo().window(parentWindow); //control back to parent window
				
		WebElement WE_Opp_Close_Dt=uiDriver.webDr.findElement(By.id("opp9"));
		WE_Opp_Close_Dt.sendKeys(input.get("Close Date"));
		
		cbf.utils.SleepUtils.sleep(3);
			
		
		List<WebElement> WE_Opp_Stage=uiDriver.webDr.findElements(By.id("opp11"));
		
		for(WebElement ele:WE_Opp_Stage)
		{
			
				Select select=new Select(ele);
				select.selectByValue("Opportunity Identified");
				cbf.utils.SleepUtils.sleep(3);
				
			}
					
		uiDriver.moveToElement("save_new","name");			
				
		cbf.utils.SleepUtils.sleep(3);
		//Opportunity Creation is completed.
		log("Opportunity Creation", ResultType.PASSED, "Opportunity creation should be Completed successfully", "Opportunity Creation Completed successfully", true);
		}catch (Exception e) {
			log("Opportunity Creation", ResultType.FAILED, "Opportunity creation should be Completed successfully", "Opportunity Creation Failed", true);
		}
		
		}
	
	public void AppointmentandOrdercompletion(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		//Add Product starts here.
				try
				{
				String Prod_Env= input.get("Product Env Name");
				System.out.println("PROD ENV: "+Prod_Env);
				if(Prod_Env.equalsIgnoreCase("UAT"))
				{
					
					uiDriver.moveToElement("//span[contains(text(),'Salesforce UAT 2.2')]/../../td[1]", "xPath");
				}
				else{
					uiDriver.moveToElement("//span[contains(text(),'Salesforce SIT 2.2')]/../../td[1]", "xPath");
				}
				cbf.utils.SleepUtils.sleep(10);
				uiDriver.moveToElement("j_id0:form1:avBlock:j_id37:noLandlineSection:noLandline", "id");
				cbf.utils.SleepUtils.sleep(10);
				WebElement WE_Prod_PostCode=uiDriver.webDr.findElement(By.xpath("//div/div[2]/table/tbody/tr[1]/td/div/input"));
				WE_Prod_PostCode.sendKeys(input.get("Post Code"));
				cbf.utils.SleepUtils.sleep(3);
				uiDriver.moveToElement("//div/div/div[3]/table/tbody/tr/td[2]/input[1]", "xpath");
				cbf.utils.SleepUtils.sleep(10);
				
				List<WebElement> Rows_count=uiDriver.webDr.findElements(By.xpath("//table/tbody/tr[1]/td/table/tbody/tr"));//
				
				for(WebElement e:Rows_count)
				{
					System.out.println("The TOTAL ROWS ARE: "+Rows_count.size());
					System.out.println("THE ROW IS : "+Rows_count);
					String Address_ALK=e.findElement(By.xpath("./td[12]")).getAttribute("innerText");
						
					System.out.println("THE ALK IS : "+Address_ALK);
					if(Address_ALK.equalsIgnoreCase(input.get("ALK")))
					{
						System.out.println("THE ALK IS : "+Address_ALK);
						WebElement Click_Ele=e.findElement(By.xpath("./td[1]"));
						System.out.println("THE CLICK ELE IS : "+Click_Ele);
						Click_Ele.click();
						break;
						
					}
									
				}
				
				
				cbf.utils.SleepUtils.sleep(17);
					
				//ORDER TYPE SELECTION
				
				String ORD_TYPE= input.get("Order Type Name");
				System.out.println("ORDER TYPE: "+ORD_TYPE);
				if(ORD_TYPE.equalsIgnoreCase("MPF"))
				{
					
							
				List<WebElement> Product_Option=uiDriver.webDr.findElements(By.tagName("a"));
				for(WebElement e:Product_Option)
				{
					String Product=e.getAttribute("innerText");
					if(input.get("Product Name").equalsIgnoreCase("Complete Business Broadband"))
					{
					if(Product.equalsIgnoreCase("Select"))
					{
						uiDriver.moveToElement("//tr/td/table/tbody/tr[3]/td[1]/a", "xPath");
						break;
					}
					}
					else
					{
						if(input.get("Product Name").equalsIgnoreCase("Simply Business Broadband"))
						{
						if(Product.equalsIgnoreCase("Select"))
						{
							uiDriver.moveToElement("//tr/td/table/tbody/tr[4]/td[1]/a", "xPath");
							break;
						}
						}
					}
									
				}
				
				
				cbf.utils.SleepUtils.sleep(17);
				
				}
				
				else {
				
					if(ORD_TYPE.equalsIgnoreCase("SIM"))
					{
						
								
					List<WebElement> Product_Option=uiDriver.webDr.findElements(By.tagName("a"));
					for(WebElement e:Product_Option)
					{
						String Product=e.getAttribute("innerText");
						if(input.get("Product Name").equalsIgnoreCase("Complete Fibre Business Broadband"))
						{
						if(Product.equalsIgnoreCase("Select"))
						{
							uiDriver.moveToElement("//div[3]/div[2]/table/tbody/tr/td/table/tbody/tr[1]/td[1]/a", "xPath");
							break;
						}
						}
						else
						{
							if(input.get("Product Name").equalsIgnoreCase("Simply Fibre Business Broadband"))
							{
							if(Product.equalsIgnoreCase("Select"))
							{
								uiDriver.moveToElement("//div[3]/div[2]/table/tbody/tr/td/table/tbody/tr[2]/td[1]/a", "xPath");
								break;
							}
							}
						}
										
					}
					
					
					cbf.utils.SleepUtils.sleep(17);
					
					}
					
				}
				
				uiDriver.clickButton("input", "button", "Next");
				cbf.utils.SleepUtils.sleep(7);
				
				List<WebElement> Prod_Cntct_Saluation=uiDriver.webDr.findElements(By.tagName("select"));
				
				for(WebElement ele:Prod_Cntct_Saluation)
				{
					
						Select select=new Select(ele);
						select.selectByValue("Mr");
						cbf.utils.SleepUtils.sleep(3);
						break;
					}
			//-----------------------------------------------------------------------------------------------
				//Selecting Address.
				uiDriver.clickButton("input", "button", "Find Address");
				cbf.utils.SleepUtils.sleep(10);
			
				
				List<WebElement> Prod_Address_Sel=uiDriver.webDr.findElements(By.tagName("select"));
				
				for(WebElement ele:Prod_Address_Sel)
				{
						String eleId=ele.getAttribute("id");
						if(eleId.contains("addresesList")){
						Select select=new Select(ele);
						select.selectByIndex(1);
						cbf.utils.SleepUtils.sleep(3);
						break;
					}
						}
				
			//--------------------------------------------------------------------------------------------------*/	
				cbf.utils.SleepUtils.sleep(3);
				
				uiDriver.clickButton("input", "button", "Next");
				cbf.utils.SleepUtils.sleep(37);
				
				WebElement Prod_CLI=uiDriver.webDr.findElement(By.id("j_id0:form1:appDetailsBlock:numberDetailsSection:newNumberSection:newcli"));
				Prod_CLI.sendKeys(input.get("CLI Number"));
				
				
				List<WebElement> Prod_Apntmnt=uiDriver.webDr.findElements(By.tagName("select"));
				
				for(WebElement ele:Prod_Apntmnt)
				{
					
						Select select=new Select(ele);
						select.selectByIndex(1);
						cbf.utils.SleepUtils.sleep(3);
						break;
					}
				
				uiDriver.clickButton("input", "button", "Next");
				cbf.utils.SleepUtils.sleep(20);
				
				uiDriver.clickButton("input", "button", "Next");
				cbf.utils.SleepUtils.sleep(20);
				
				
		List<WebElement> Prod_Entry_Pref=uiDriver.webDr.findElements(By.tagName("select"));
				
				for(WebElement ele:Prod_Entry_Pref)
				{
					
						Select select=new Select(ele);
						select.selectByValue("Normal Entry");
						cbf.utils.SleepUtils.sleep(3);
						break;
					}
				cbf.utils.SleepUtils.sleep(7);
							
				List<WebElement> Prod_Directory_Line=uiDriver.webDr.findElements(By.xpath("//div[3]/div[2]/table/tbody/tr[1]/td/div/select"));
				
				for(WebElement ele:Prod_Directory_Line)
				{
					
						Select select=new Select(ele);
						select.selectByValue("NormalUse");
						cbf.utils.SleepUtils.sleep(3);
						break;
					}
				
				
				uiDriver.clickButton("input", "button", "save");
				System.out.println("COMPLETED PROD ADDITION");
				cbf.utils.SleepUtils.sleep(27);
				
				log("Addition of Product", ResultType.PASSED, "Product Addition should be Completed successfully", "Product Addition Completed successfully", true);
				
				System.out.println("SUBMIT ORDER ITEMS STARTS HERE");
				
				uiDriver.clickButton("input", "button", "Submit Order Items");
				cbf.utils.SleepUtils.sleep(7);
				
				List<WebElement> Submit_Bill_Acct=uiDriver.webDr.findElements(By.tagName("select"));
				
				for(WebElement ele:Submit_Bill_Acct)
				{
					
						Select select=new Select(ele);
						select.selectByIndex(2);
						cbf.utils.SleepUtils.sleep(3);
						break;
					}
				cbf.utils.SleepUtils.sleep(7);
				
				
				WebElement Bill_Acct_Pwd=uiDriver.webDr.findElement(By.id("j_id0:j_id33:billingDetailsBlock:generalDetailsSection:j_id69"));
				Bill_Acct_Pwd.sendKeys(input.get("Password"));
				
				cbf.utils.SleepUtils.sleep(1);
				List<WebElement> Submit_Payment_Type=uiDriver.webDr.findElements(By.id("j_id0:j_id33:billingDetailsBlock:paymentDetailsSection:paymentTypeSection:j_id86"));
				
				for(WebElement ele:Submit_Payment_Type)
				{
					
						Select select=new Select(ele);
						select.selectByIndex(1);
						cbf.utils.SleepUtils.sleep(3);
						break;
					}
				cbf.utils.SleepUtils.sleep(7);
				
				
				WebElement Bill_Payer_Name=uiDriver.webDr.findElement(By.id("j_id0:j_id33:billingDetailsBlock:paymentDetailsSection:j_id88"));
				Bill_Payer_Name.sendKeys("Payer1");
				
				WebElement Bill_County_Name=uiDriver.webDr.findElement(By.id("j_id0:j_id33:billingDetailsBlock:billingAddressDetailsSection:billingCounty"));
				Bill_County_Name.sendKeys("Chesire");
				
				
				List<WebElement> Bill_Cntct_Salutaion=uiDriver.webDr.findElements(By.id("j_id0:j_id33:billingDetailsBlock:billingContactDetailsSection:BillingSalutation"));
				
				for(WebElement ele:Bill_Cntct_Salutaion)
				{
					
						Select select=new Select(ele);
						select.selectByIndex(1);
						cbf.utils.SleepUtils.sleep(3);
						break;
					}
				cbf.utils.SleepUtils.sleep(7);
				
				uiDriver.clickButton("input", "button", "Next");
				cbf.utils.SleepUtils.sleep(17);
				
				uiDriver.clickButton("input", "button", "Next");
				cbf.utils.SleepUtils.sleep(17);
				
				uiDriver.clickButton("input", "radio", "Yes");
				cbf.utils.SleepUtils.sleep(10);
				
				uiDriver.clickButton("input", "button", "Next");
				cbf.utils.SleepUtils.sleep(20);
				
				uiDriver.clickButton("input", "button", "Submit");
				cbf.utils.SleepUtils.sleep(20);
				log("Order Submission", ResultType.PASSED, "Order Submission should be Completed successfully", "Order Submission Completed successfully", true);

			}
			catch (Exception e) {
			log("Order Submission", ResultType.FAILED, "Order Submission should be Completed successfully", "Order Submission Failed", true);
			}

	}
}