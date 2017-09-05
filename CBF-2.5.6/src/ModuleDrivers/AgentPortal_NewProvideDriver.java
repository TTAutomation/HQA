package ModuleDrivers;

import static cbf.engine.TestResultLogger.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

//import com.sun.org.apache.bcel.internal.generic.GOTO;

import cbf.engine.TestResult.ResultType;
import cbf.reporting.ExcelReporter;
import cbf.utils.DataRow;
import cbfx.BaseWebDriver.BaseWebModuleDriver;


public class AgentPortal_NewProvideDriver extends BaseWebModuleDriver{
	
	public void LaunchApp(DataRow input, DataRow output) throws IOException
	{
		uiDriver.launchApplication(input.get("url"));
		if(uiDriver.checkPage("Sales portal"))
				//if(uiDriver.checkPage(input.get("pageName")))
		{			
			passed("Launching AgentPortal App", "AgentPortal App should be launched successfully", "AgentPortal App launched successfully");
		}
		else
		{
			log("Launching AgentPortal App", ResultType.FAILED, "AgentPortal App should be launched successfully", "AgentPortal App not launched successfully", true);
		}
	}
	
	public void Personaldetails(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		uiDriver.selectfromList("Agent_Title", "Mr");
		
		uiDriver.setValue("Agent_FirstName", input.get("FirstName"));
		uiDriver.setValue("Agent_LastName", input.get("LastName"));
		uiDriver.setValue("Agent_PhoneNumber", input.get("MobileNumber"));
		
		//uiDriver.setValue("Agent_HomeNumber", input.get("HomeNumber"));
				
		//uiDriver.setValue("pd_password", input.get("password"));
		//uiDriver.setValue("pd_confirm password", input.get("confirm password"));
		
		//uiDriver.click("Agent_Creditcheck");
		
		uiDriver.selectfromList("Agent_DOB_Day", input.get("DOB_Date"));
		uiDriver.selectfromList("Agent_DOB_Month", input.get("DOB_Month"));
		uiDriver.selectfromList("Agent_DOB_Year", input.get("DOB_year"));
		
		log("Personal Details Completed", ResultType.PASSED, "Personal Details should be Completed successfully", "Personal Details Completed successfully", true);
		
		uiDriver.setValue("Agent_Email", input.get("Email"));
		uiDriver.setValue("Agent_Confirm_Email", input.get("Confirm email"));
		
		//uiDriver.selectfromList("Creditcheck_Month", input.get("Creditcheck_Month"));
		//uiDriver.selectfromList("Creditcheck_year", input.get("Creditcheck_year"));
		
		//uiDriver.click("Creditcheck_Confirmaddress");
		
		//uiDriver.click("Creditcheck");
		log("Personal Details Completed", ResultType.PASSED, "Personal Details should be Completed successfully", "Personal Details Completed successfully", true);		
		uiDriver.click("Agent_Personaldetails_Next");
		//passed("Personal Details are entered successfully", "Personal Details are entered successfully", "Personal Details are entered successfully");
		Thread.sleep(5000);
		//uiDriver.click("Agent_customerdetails_correct");
		//Thread.sleep(5000);

	}
	
	
	public void AppointmentandOrdercompletion(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		//Thread.sleep(10000);
		
		//uiDriver.click("select Date");
		//Thread.sleep(5000);
		
	//	uiDriver.click("Date");
	//	uiDriver.click("Agent_Date_radio_button");
		//uiDriver.click("date Confirm");
		
		//Thread.sleep(5000);
		//uiDriver.click("Package Next");
		//Thread.sleep(5000);
		//uiDriver.click("Agent_Personaldetails_Next");
		//Thread.sleep(5000);
		//passed("Appointment is booked", "Appointment is booked successfully", "Appointment is booked successfully");
		uiDriver.setValue("Agent_AccountNumber", input.get("AccountNumber"));
		uiDriver.setValue("Agent_Bank_SortCode1", input.get("Sortcode1"));
		uiDriver.setValue("Agent_Bank_SortCode2", input.get("Sortcode2"));
		uiDriver.setValue("Agent_Bank_SortCode3", input.get("Sortcode3"));
		
		//uiDriver.click("Agent_Split connectin fee_No");
		
		log("Account Details Completed", ResultType.PASSED, "Account Details should be Completed successfully", "Account Details Completed successfully", true);
		
		uiDriver.click("Agent_Personaldetails_Next");
		//passed("Credit Check is successfull", "Credit Check is successfull", "Credit Check is successfull");
		Thread.sleep(10000);
		uiDriver.click("Agent_Check_AllCheckBoxes");
		uiDriver.click("Agent_Personaldetails_Summary");
		uiDriver.click("Agent_Summary_Window_OK_Button");
		
		uiDriver.setValue("Agent_Enquiry_Password", input.get("password"));
		uiDriver.setValue("Agent_Confirm_Enquiry_Password", input.get("confirm password"));
		uiDriver.click("Agent_TandC");
		
		uiDriver.click("Agent_Confirm Order");
		
		uiDriver.click("Agent_Confirm_Next");
		
		String ordref=uiDriver.getAttribute("Agent_Order_Reference","innerText");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Your Order Reference is:"+ordref);
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		log("Order Completed", ResultType.PASSED, "Order should be Completed successfully", "Order Completed successfully", true);
		passed("Order Completed", "Order Reference Number is : "+ ordref," ");
		/*
		uiDriver.click("Agent_Confirm check box");
		
		uiDriver.click("Agent_Confirm Order");
		Thread.sleep(20000);
		//passed("order placed successfully", "Order placed successfully", "Order placed successfully");
		uiDriver.click("Order Submit_Next");
		Thread.sleep(10000);
		uiDriver.checkTextOnPage(input.get("Order confrimation message"), 10);
		*/
		
	}

	public void SimplyBroadband(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		
		//uiDriver.clicknavigationlinkbyname(input.get("Package"),input.get("SubPackage"));
		
		/*
		uiDriver.click("SimplyBroadband mouseover link");
		uiDriver.waitForBrowserStability("10000");
		uiDriver.click("SimplyBroadband link");
		uiDriver.waitForBrowserStability("10000");
		*/
		//uiDriver.click("Check Availability link");
		
		//uiDriver.click("Don't have a landline number link");
		Thread.sleep(5000);
		//uiDriver.click("Agent_Postcode_NewProvide");
		
		//Home Page
		uiDriver.setValue("Agent_Postcode_NewProvide", input.get("Postcode"));
		uiDriver.setValue("Agent_AgentID_NewProvide", input.get("AgentID"));
		log("Primary Details Completed", ResultType.PASSED, "Primay Details should be Completed successfully", "Primay Details Completed successfully", true);
		uiDriver.click("Agent_Checkavailability_NewProvide");
		Thread.sleep(3000);
		
		
		//Address Tab
		
		uiDriver.selectfromList("Agent_AddressTable", input.get("Address"));

		
		
		uiDriver.click("Agent_Lived in this address for more than 30 days");
		
		log("Address Selection Completed", ResultType.PASSED, "Address Selection should be Completed successfully", "Address Selection successfully", true);
				
		uiDriver.click("Agent_AddressNext");
		Thread.sleep(5000);
		uiDriver.waitForBrowserStability("30");
		Thread.sleep(10000);
		
		//Lines Tab
		
		
		
		uiDriver.click("Agent_Select available line");
		Thread.sleep(10000);
		
		uiDriver.click("Agent_Available line button");
		Thread.sleep(10000);
		
		
					
		uiDriver.selectPackage_agent(input.get("Package"));
		//uiDriver.getControl("Agent_No SIM_Expand");
		Thread.sleep(10000);
		uiDriver.click("Agent_No SIM_Expand");
		
		uiDriver.click("Agent_No SIM");
		uiDriver.click("Agent_No TV_Expand");
		
		uiDriver.click("Agent_No TV");
		
		log("Package Selection Completed", ResultType.PASSED, "Package Selection should be Completed successfully", "Package Selection Completed successfully", true);
		
		uiDriver.click("Agent_Place Order");
		
		
	}
	
	
	
	
}
