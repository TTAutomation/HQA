/******************************************************************************
$Id : DemoInsuranceDriver.java 9/8/2014 1:21:28 PM
Copyright 2014-2016 IGATE GROUP OF COMPANIES. All rights reserved
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

package ModuleDrivers;

import static cbf.engine.TestResultLogger.*;

import java.io.IOException;

import cbf.engine.TestResult.ResultType;
import cbf.utils.DataRow;
import cbfx.BaseWebDriver.BaseWebModuleDriver;

public class DemoInsuranceDriver extends BaseWebModuleDriver{
	
	public void launchApp(DataRow input, DataRow output) throws IOException
	{
		uiDriver.launchApplication(input.get("url"));
		if(uiDriver.checkPage("Login"))
		{			
			System.out.println("test");
			passed("Launching Insurance App", "Insurance App should be launched successfully", "Insurance App launched successfully");
		}
		else
		{
			log("Launching Insurance App", ResultType.FAILED, "Insurance App should be launched successfully", "Insurance App not launched successfully", true);
		}
	}
	
	public void login(DataRow input, DataRow output)
	{
		uiDriver.setValue("userId", input.get("userId"));
		uiDriver.setValue("clientpassword", input.get("password"));
		uiDriver.click("signInBtn");
		if (uiDriver.checkPage("Client Details")) 
		{
			passed("Logging in to Insurance App", "User should be able to login successfully", "User logged in successfully");
		}
		else
		{
			log("Logging in to Insurance App", ResultType.FAILED, "User should be able to login successfully", "User unable to login", true);
		}
	}
	
	public void addClientInfo(DataRow input, DataRow output) throws InterruptedException
	{
		uiDriver.setValue("clientName", input.get("clientName"));
		if(input.get("sex").equalsIgnoreCase("Female"))
		{
			uiDriver.click("clientSex_Female");
		}
		else
		{
			uiDriver.click("clientSex_Male");
		}
		uiDriver.setValue("clientDOB", input.get("clientDOB"));
		uiDriver.setValue("clientOccupation", input.get("clientOccupation"));
		uiDriver.setValue("clientEmail", input.get("clientEmail"));
		uiDriver.setValue("clientAddress", input.get("clientAddress"));
		if(input.get("clientMaritalStatus").equalsIgnoreCase("On"))
		{
			uiDriver.click("clientMaritalStatus");
		}
		if(input.get("smokingStatus").equalsIgnoreCase("No"))
		{
			uiDriver.click("smokingStatus_No");
		}
		else
		{
			uiDriver.click("smokingStatus_Yes");
		}
		uiDriver.setValue("clientAnnualIncome", input.get("clientAnnualIncome"));
		uiDriver.click("productBtn");
		uiDriver.waitForBrowserStability("5000");
		
		if(uiDriver.checkPage("Product Details"))
		{
			passed("Enter client details", "Client details should be entered successfully", "Client details entered successfully");
		}
		else
		{
			log("Enter client details", ResultType.FAILED, "Client details should be entered successfully", "Client details not entered successfully", true);
		}
	}
	
	public void addProductInfo(DataRow input, DataRow output)
	{
		uiDriver.setValue("productName", input.get("productName"));
		uiDriver.setValue("coverageStartDt", input.get("coverageStartDt"));
		uiDriver.setValue("faceAmount", input.get("faceAmount"));
		uiDriver.setValue("plan", input.get("plan"));
		uiDriver.setValue("term", input.get("term"));
		uiDriver.setValue("paymentFreq", input.get("paymentFreq"));
		uiDriver.click("riderBtn");
		if(uiDriver.checkPage("Rider Details"))
		{
			passed("Enter Product details", "Product details should be entered successfully", "Product details entered successfully");
		}
		else
		{
			log("Enter Product details", ResultType.FAILED, "Product details should be entered successfully", "Product details not entered successfully", true);
		}
	}
	
	public void addRiderInfo(DataRow input, DataRow output)
	{
		if(input.get("wop").equalsIgnoreCase("ON"))
		{
			uiDriver.click("wop");
		}
	}
	
	public void logout(DataRow input, DataRow output)
	{
		uiDriver.click("logoutBtn");
		if(uiDriver.checkPage("Login"))
		{
			passed("Logout", "User should be logged out successfully", "User logged out successfully");
		}
		else
		{
			log("Logout", ResultType.FAILED, "User should be logged out successfully", "User unable to log out", true);
		}
	}

}
