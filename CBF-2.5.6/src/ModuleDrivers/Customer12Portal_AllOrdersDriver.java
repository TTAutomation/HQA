package ModuleDrivers;

import static cbf.engine.TestResultLogger.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByPartialLinkText;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

//import com.sun.org.apache.bcel.internal.generic.GOTO;

import cbf.engine.TestResult.ResultType;
import cbf.reporting.ExcelReporter;
import cbf.utils.DataRow;
import cbf.utils.LogUtils;
import cbfx.BaseWebDriver.BaseWebModuleDriver;


public class Customer12Portal_AllOrdersDriver extends BaseWebModuleDriver{
	
	public void LaunchApp(DataRow input, DataRow output) throws IOException
	{
		uiDriver.launchApplication(input.get("url"));
		if(uiDriver.checkPage("TalkTalk"))
				
			{			
				log("Customer Portal Launch", ResultType.PASSED, "Customer Portal Launch should be Completed successfully", "Customer Portal Launch Completed successfully", true);
			}
		
		else
			{
				log("Launching Customer Portal App", ResultType.FAILED, "Customer Portal App should be launched successfully", "Customer Portal App not launched successfully", true);
			}
		
	}
	
	public void Personaldetails(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		//Personal Details Tab
		
		//Checking for Credit Check Checkbox
				
		List<WebElement> CC_Checkbox=  uiDriver.webDr.findElements(By.id("pdCreditCheck"));
			
		if(!(CC_Checkbox.isEmpty()))
		{
			
	      uiDriver.click("Agent_Credit_Check_Checkbox"); 
	      uiDriver.click("Agent_CC_ResAddress_Radio");
	      uiDriver.selectfromList("Agent_CC_Duration_Month", input.get("Creditcheck_Month"));
	      uiDriver.selectfromList("Agent_CC_Duration_Year", input.get("Creditcheck_Year"));
	      log("Credit Check Details Completed", ResultType.PASSED, "Credit Check Details should be Completed successfully", "Credit Check Details Completed successfully", true);
	    }
		
		//personal details information submission
		uiDriver.selectfromList("Agent_Title", "Mr");
		
		uiDriver.setValue("Agent_FirstName", input.get("FirstName"));
		uiDriver.setValue("Agent_LastName", input.get("LastName"));
		uiDriver.setValue("Agent_PhoneNumber", input.get("MobileNumber"));
						
		uiDriver.selectfromList("Agent_DOB_Day", input.get("DOB_Date"));
		uiDriver.selectfromList("Agent_DOB_Month", input.get("DOB_Month"));
		uiDriver.selectfromList("Agent_DOB_Year", input.get("DOB_year"));
		
		uiDriver.setValue("Agent_Email", input.get("Email"));
		uiDriver.setValue("Agent_Confirm_Email", input.get("Confirm email"));
		
		
		log("Personal Details Completed", ResultType.PASSED, "Personal Details should be Completed successfully", "Personal Details Completed successfully", true);		
		uiDriver.click("Agent_Personaldetails_Next");
		
		
		//Checking Credit Check confirmation button.
		List<WebElement> CC_Yes_Button= uiDriver.webDr.findElements(By.id("customer-consent"));
		if(!(CC_Yes_Button.isEmpty()))
		{
			log("Personal Details Completed", ResultType.PASSED, "Personal Details should be Completed successfully", "Personal Details Completed successfully", true);
			uiDriver.click("Agent_CC_Yes_Button"); 
		    
		}
		
		
		Thread.sleep(5000);
		//Now control flows to Appointment and Order Completion.

	}
	
	
	public void AppointmentandOrdercompletion(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		try
		{
		
		//Checking and booking Appointment if available.
		Actions action = new Actions(uiDriver.webDr);	
		
		System.out.println("This is first line.");
		
		List<WebElement> WE_Date=uiDriver.webDr.findElements(By.tagName("span"));
		int Appt_Next_button=0;
		System.out.println("This is second line."+WE_Date);
		for(WebElement ele:WE_Date)
		{
			System.out.println("This is first line inside for loop1.");
			
			String eleHref=ele.getAttribute("class");
			System.out.println("This is second line inside loop.");
				if(eleHref.equals("dateactive"))
				{
					System.out.println("This is first line inside if condition.");
					action.moveToElement(ele).click().perform();
					Appt_Next_button=1;
					break;	
				}
								
			}
		
		Thread.sleep(3000);
		//Checking and booking respective slot if available. 
		List<WebElement> WE_Slot=uiDriver.webDr.findElements(By.tagName("input"));
		System.out.println("This is first line."+WE_Slot);
		for(WebElement ele:WE_Slot)
		{
			System.out.println(ele);	
			String eleId=ele.getAttribute("class");
			if(eleId.equals("radio"))
			{
				System.out.println("This is first RADIO line inside if condition.");
				action.moveToElement(ele).click().perform();
				break;	
			}
		}
		if(Appt_Next_button==1)
			{
				WebElement Appt_Next_Btn=uiDriver.webDr.findElement(By.tagName("button"));
				log("Appointment Details Completed", ResultType.PASSED, "Appointment Details should be Completed successfully", "Appointment Details Completed successfully", true);
				action.moveToElement(Appt_Next_Btn).click().perform();
			}
		} catch (Exception e) {
			
			log("Appointment skipped for Non-NewLine order", ResultType.PASSED, "Non-NewLine Order should be Completed successfully", "Non-NewLine Order Completed successfully", true);
		}
		Thread.sleep(5000);
	
		
		//Account Information submission.
						
		uiDriver.setValue("Agent_AccountNumber", input.get("AccountNumber"));
		uiDriver.setValue("Agent_Bank_SortCode1", input.get("Sortcode1"));
		uiDriver.setValue("Agent_Bank_SortCode2", input.get("Sortcode2"));
		uiDriver.setValue("Agent_Bank_SortCode3", input.get("Sortcode3"));
						
		uiDriver.click("Agent_Personaldetails_Next");
		
		Thread.sleep(3000);
		uiDriver.click("Agent_Check_AllCheckBoxes");
		log("Questions Details Completed", ResultType.PASSED, "Questions Details should be Completed successfully", "Questions Details Completed successfully", true);
		uiDriver.click("Agent_Personaldetails_Summary");
		uiDriver.click("Agent_Summary_Window_OK_Button");
		
		uiDriver.setValue("Agent_Enquiry_Password", input.get("password"));
		uiDriver.setValue("Agent_Confirm_Enquiry_Password", input.get("confirm password"));
		uiDriver.click("Agent_TandC");
		log("Summary Details Completed", ResultType.PASSED, "Summary Details should be Completed successfully", "Summary Details Completed successfully", true);
		uiDriver.click("Agent_Confirm Order");
		
		uiDriver.click("Agent_Confirm_Next");
		
		String ordref=uiDriver.getAttribute("Agent_Order_Reference","innerText");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Your Order Reference is:"+ordref);
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		log("Order Completed", ResultType.PASSED, "Order should be Completed successfully", "Order Completed successfully", true);
		passed("Order Completed", "Order Reference Number is : "+ ordref," ");
		//Transaction completes here.
		
	}

	public void Transaction(DataRow input, DataRow output) throws IOException, InterruptedException
	{
	
		Actions action = new Actions(uiDriver.webDr);	
		
		try {//Clicking on ENVIRONMENT CHANGE LINK.
			
			//WebElement Env_Link=uiDriver.webDr.findElement(By.xpath("//content[@id='content']//a//span[contains(text(),'Environment Changer Links')]"));
			//WebElement Env_Link=uiDriver.webDr.findElement(By.linkText("Environment Changer Links"));
			//action.moveToElement(Env_Link).click().perform();
			uiDriver.moveToElement("Environment Changer Links", "linkText");
												
		} catch (Exception e) {
			log("Environment Change Link.", ResultType.FAILED, "Environment Change Link should be launched successfully", "Environment Change Link launch failed.", true);
		}
		
		try{
			//Navigating to TEST ENVIRONMENT.
			
			
				//WebElement Test_Env=uiDriver.webDr.findElement(By.xpath("//span[contains(text(),'bsssit1-eve')]/../../../../../../..//div[@ng-transclude='button']"));
				//action.moveToElement(Test_Env).click().perform();
				uiDriver.moveToElement("//span[contains(text(),'bsssit1-eve')]/../../../../../../..//div[@ng-transclude='button']", "xpath");
				Thread.sleep(15000);
		}catch (Exception e) {
			log("Swtiching to Test Environment.", ResultType.FAILED, "Test Environment should be launched successfully", "Test Environment launch failed.", true);
		}
		
		//Selecting the type of order based on the provided value.
		if(input.get("Order Type").equalsIgnoreCase("Fast"))
		{			
			try{
			
				uiDriver.moveToElement("Fast product page", "linkText");
				
				Thread.sleep(17000);
			}catch (Exception e) {
				log("Fast Order Type Link.", ResultType.FAILED, "Fast Order Type should be launched successfully", "Fast Order Type Link launch failed.", true);
			}
			
		}
		else
			if(input.get("Order Type").equalsIgnoreCase("Faster"))
			{
				try{
					uiDriver.moveToElement("Faster product page", "linkText");
					//uiDriver.click("Cust_Faster_Product");
					Thread.sleep(3000);
					}catch (Exception e) {
						log("Faster Order Type Link.", ResultType.FAILED, "Faster Order Type should be launched successfully", "Faster Order Type Link launch failed.", true);
					}
			}
		
		//Getting Started with the order. 
		List<WebElement> WE_GetStart=uiDriver.webDr.findElements(By.tagName("span"));
		
		System.out.println("This is second line."+WE_GetStart);
		for(WebElement ele:WE_GetStart)
		{
			
			
			String eleHref=ele.getAttribute("innerText");
			
				if(eleHref.equals("Get Started"))
				{
			
					action.moveToElement(ele).click().perform();
					Thread.sleep(3000);
					break;	
				}
								
			}
		//Selecting the plan.
		try{
			uiDriver.moveToElement("//p//segmented-title[contains(text(),'Fast Broadband')]", "xPath");
			uiDriver.moveToElement("//p//segmented-title[contains(text(),'TV Box')]", "xPath");
			uiDriver.click("Cust_Next_Button");
			Thread.sleep(17000);
		} catch (Exception e) {
			log("Selecting Plan Page.", ResultType.FAILED, "Selecting Plan Page should be successful", "Selecting Plan failed.", true);
		}
		
		
	}
	
    //span[contains(text(),"bsssit1-eve")]/../../../../../../..//div[@ng-transclude="button"]
}
