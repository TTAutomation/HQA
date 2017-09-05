
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

public class MyAccountDriver extends BaseWebModuleDriver {
	
	public void LaunchApp(DataRow input, DataRow output) throws IOException
	{
		uiDriver.launchApplication(input.get("url"));
		if(uiDriver.checkPage(input.get("pageName")))
		{			
			//passed("Launching MyAccount App", "MyAccount App should be launched successfully", "MyAccount App launched successfully");
		}
		else
		{
			log("Launching MyAccount App", ResultType.FAILED, "MyAccount App should be launched successfully", "MyAccount App not launched successfully", true);
		}
	}
	
	public void Login(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		uiDriver.setValue("userId", input.get("userId"));
		Thread.sleep(2000);
		uiDriver.setValue("password", input.get("password"));
		Thread.sleep(2000);	
		uiDriver.click("loginbutton");
		uiDriver.waitForBrowserStability("10000");
		Thread.sleep(10000);
	}
	public void UIValidation(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		uiDriver.getControl("Homelink");
		uiDriver.getControl("myBills");
		uiDriver.getControl("myProfile");
		uiDriver.getControl("myServices");
		uiDriver.getControl("myOffers");
		uiDriver.getControl("messages_tab");
		uiDriver.getControl("helpsupport");
		uiDriver.getControl("Bills & Payments");
		uiDriver.getControl("Account Profile");
		uiDriver.getControl("Help & Support");
		uiDriver.getControl("Service Management");
		uiDriver.getControl("Messages");
		
		
	}
	
	public void passwordreset(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		uiDriver.click("myProfile");
		Thread.sleep(5000);
		uiDriver.click("MyAccount Login details");
		Thread.sleep(5000);
		uiDriver.setValue("password_reset", input.get("password"));
		uiDriver.setValue("resetPassword_reset", input.get("resetPassword"));
		//uiDriver.
		uiDriver.setValue("SecAnswer", input.get("SecAnswer"));
		uiDriver.click("Contact confirm");
		Thread.sleep(5000);
		uiDriver.click("Popup_Confirmation");
		Thread.sleep(5000);
		uiDriver.click("logout");
		//uiDriver.get("url");
		Thread.sleep(5000);
		uiDriver.click("Logbackintoaccount");
		Thread.sleep(5000);
		//uiDriver.switchToWindow(title)
		uiDriver.setValue("userId", input.get("userId"));
		Thread.sleep(2000);
		uiDriver.setValue("password", input.get("password"));
		Thread.sleep(2000);	
		uiDriver.click("loginbutton");
		uiDriver.waitForBrowserStability("10000");
		Thread.sleep(2000);	
		

	}

}
