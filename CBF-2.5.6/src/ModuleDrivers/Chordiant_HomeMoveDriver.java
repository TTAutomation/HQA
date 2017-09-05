package ModuleDrivers;

import static cbf.engine.TestResultLogger.*;

import java.io.IOException;

import cbf.engine.TestResult.ResultType;
import cbf.utils.DataRow;
import cbfx.BaseWebDriver.BaseWebModuleDriver;


public class Chordiant_HomeMoveDriver extends BaseWebModuleDriver{
	
	public void LaunchApp(DataRow input, DataRow output) throws IOException
	{
		uiDriver.launchApplication(input.get("url"));
		/*if(uiDriver.checkPage(input.get("pageName")))
		{			
			//passed("Launching MyAccount App", "MyAccount App should be launched successfully", "MyAccount App launched successfully");
		}
		else
		{
			//log("Launching MyAccount App", ResultType.FAILED, "MyAccount App should be launched successfully", "MyAccount App not launched successfully", true);
		}*/
	}
	
	
	
	public void Login(DataRow input, DataRow output) throws Exception
	{
		
		Thread.sleep(50000);
		uiDriver.setValue("Chordiant_loginUserName", input.get("UserID"));
		uiDriver.setValue("Chordiant_loginPassword", input.get("Password"));
		uiDriver.click("Chordiant_submit");
		
		//uiDriver.click("Chordiant_Identify Customer");
		//uiDriver.handleAlert("", "yes");
	}
	
	public void Identify_customer(DataRow input, DataRow output) throws Exception
	{
		
		Thread.sleep(10000);
		uiDriver.switchToFrame("advisorDesktop");
		uiDriver.chordiantclick("Chordiant_Identify Customer");
		uiDriver.switchToFrame("cframe_ms__id56");
		uiDriver.setValue("Chordiant_CLI_Search", input.get("CLI_Search"));
		uiDriver.chordiantclick("Chordiant_Search");
		
		Thread.sleep(10000);
		uiDriver.drawHighlight("Chordiant_GeneralEnquiry");
		Thread.sleep(5000);
		uiDriver.chordiantclick("Chordiant_GeneralEnquiry");
		uiDriver.click("Chordiant_GeneralEnquiry");
		
		Thread.sleep(5000);
	//	uiDriver.tchordiantclick("Chordiant_GeneralEnquiry");
		uiDriver.chordiantclick("Chordiant_Confirm");
		
		Thread.sleep(10000);
		uiDriver.drawHighlight("Chordiant_Order Mgmt");
		uiDriver.doubleclick("Chordiant_Order Mgmt");
		
		Thread.sleep(10000);
		uiDriver.drawHighlight("Chordiant_ChangePackage");
		uiDriver.doubleclick("Chordiant_ChangePackage");
		
		
		Thread.sleep(10000);
		uiDriver.switchToFrame("custProfileActivityIframe_activity_0");
		//uiDriver.drawHighlight("Chordiant_LineRental");
		//uiDriver.click("Chordiant_LineRental");
		//uiDriver.doubleclick("Chordiant_LineRental");
		//uiDriver.tchordiantclick("Chordiant_LineRental");
		
		Thread.sleep(10000);
		uiDriver.drawHighlight("Chordiant_Contact details check");
		uiDriver.doubleclick("Chordiant_Contact details check");
		
		Thread.sleep(10000);
		uiDriver.drawHighlight("Chordiant_changefeature");
		uiDriver.chordiantclick("Chordiant_changefeature");
		
		//uiDriver.handleAlert("", "yes");
		Thread.sleep(10000);
		uiDriver.drawHighlight("Chordiant_Care level expand");
		uiDriver.doubleclick("Chordiant_Care level expand");
		
		Thread.sleep(10000);
		uiDriver.drawHighlight("Chordiant_Add Carelevel");
		uiDriver.click("Chordiant_Add Carelevel");
		
		Thread.sleep(10000);
		uiDriver.drawHighlight("Chordiant_ChangePackage_Next");
		uiDriver.doubleclick("Chordiant_ChangePackage_Next");
		
		Thread.sleep(10000);
		uiDriver.drawHighlight("Chordiant_Changepackage_compliance");
		uiDriver.doubleclick("Chordiant_Changepackage_compliance");
		
		Thread.sleep(10000);
		//uiDriver.drawHighlight("Chordiant_Changepackage_Finish");
		//uiDriver.doubleclick("Chordiant_Changepackage_Finish");
	}
	
	
	
	
	public void Personaldetails(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		uiDriver.selectfromList("Title", "Mr");
		
		uiDriver.setValue("FirstName", input.get("FirstName"));
		uiDriver.setValue("LastName", input.get("LastName"));
		uiDriver.setValue("MobileNumber", input.get("MobileNumber"));
		uiDriver.setValue("Email", input.get("Email"));
		uiDriver.setValue("Confirm email", input.get("Confirm email"));
		uiDriver.setValue("pd_password", input.get("password"));
		uiDriver.setValue("pd_confirm password", input.get("confirm password"));
		
		uiDriver.click("Creditcheck");
		
		uiDriver.selectfromList("DOB_Date", input.get("DOB_Date"));
		uiDriver.selectfromList("DOB_Month", input.get("DOB_Month"));
		uiDriver.selectfromList("DOB_year", input.get("DOB_year"));
		
		uiDriver.selectfromList("Creditcheck_Month", input.get("Creditcheck_Month"));
		uiDriver.selectfromList("Creditcheck_year", input.get("Creditcheck_year"));
		
		uiDriver.click("Creditcheck_Confirmaddress");
		
		//uiDriver.click("Creditcheck");
		
		uiDriver.click("Package Next");
		//passed("Personal Details are entered successfully", "Personal Details are entered successfully", "Personal Details are entered successfully");
		Thread.sleep(5000);
		
	}
	
	
	public void AppointmentandOrdercompletion(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		Thread.sleep(10000);
		
		uiDriver.click("select Date");
		Thread.sleep(5000);
		
		uiDriver.click("Date");
		uiDriver.click("Date radio button");
		uiDriver.click("date Confirm");
		
		Thread.sleep(5000);
		//uiDriver.click("Package Next");
		Thread.sleep(5000);
		uiDriver.click("Button Next");
		//passed("Appointment is booked", "Appointment is booked successfully", "Appointment is booked successfully");
		uiDriver.setValue("AccountNumber", input.get("AccountNumber"));
		uiDriver.setValue("Sortcode1", input.get("Sortcode1"));
		uiDriver.setValue("Sortcode2", input.get("Sortcode2"));
		uiDriver.setValue("Sortcode3", input.get("Sortcode3"));
		
		
		
		uiDriver.click("validateddirectdebit");
		//passed("Credit Check is successfull", "Credit Check is successfull", "Credit Check is successfull");
		Thread.sleep(10000);
		uiDriver.click("Confirm Order check box");
		
		uiDriver.click("Confirm Order button");
		Thread.sleep(20000);
		//passed("order placed successfully", "Order placed successfully", "Order placed successfully");
		uiDriver.click("Order Submit_Next");
		Thread.sleep(10000);
		uiDriver.checkTextOnPage(input.get("Order confrimation message"), 10);
		
	}

	public void SimplyBroadband(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		
		uiDriver.clicknavigationlinkbyname(input.get("Package"),input.get("SubPackage"));
		
		/*
		uiDriver.click("SimplyBroadband mouseover link");
		uiDriver.waitForBrowserStability("10000");
		uiDriver.click("SimplyBroadband link");
		uiDriver.waitForBrowserStability("10000");
		*/
		uiDriver.click("Check Availability link");
		
		uiDriver.click("Don't have a landline number link");
		Thread.sleep(5000);
		uiDriver.click("Postcode");
		uiDriver.setValue("Postcode", input.get("Postcode"));
		uiDriver.click("Next Button");
		Thread.sleep(10000);
		//uiDriver.
		uiDriver.selectfromList("Available Address Search list box", input.get("Address"));
		uiDriver.click("Lived in this address for more than 30 days");
		uiDriver.click("Select Address button");
		Thread.sleep(5000);
		uiDriver.waitForBrowserStability("30");
		Thread.sleep(50000);
		uiDriver.checkElementPresent("Package Next", 30);
		uiDriver.click("Package Next");
		Thread.sleep(10000);
		
		uiDriver.click("Button Next");
		passed("Launching MyAccount App", "MyAccount App should be launched successfully", "MyAccount App launched successfully");
		
		Thread.sleep(10000);
		/*
		uiDriver.selectfromList("Title", "Mr");
		
		uiDriver.setValue("FirstName", input.get("FirstName"));
		uiDriver.setValue("LastName", input.get("LastName"));
		uiDriver.setValue("MobileNumber", input.get("MobileNumber"));
		uiDriver.setValue("Email", input.get("Email"));
		uiDriver.setValue("Confirm email", input.get("Confirm email"));
		uiDriver.setValue("pd_password", input.get("password"));
		uiDriver.setValue("pd_confirm password", input.get("confirm password"));
		
		uiDriver.click("Creditcheck");
		
		uiDriver.selectfromList("DOB_Date", input.get("DOB_Date"));
		uiDriver.selectfromList("DOB_Month", input.get("DOB_Month"));
		uiDriver.selectfromList("DOB_year", input.get("DOB_year"));
		
		uiDriver.selectfromList("Creditcheck_Month", input.get("Creditcheck_Month"));
		uiDriver.selectfromList("Creditcheck_year", input.get("Creditcheck_year"));
		
		uiDriver.click("Creditcheck_Confirmaddress");
		
		//uiDriver.click("Creditcheck");
		
		uiDriver.click("Package Next");
		//passed("Personal Details are entered successfully", "Personal Details are entered successfully", "Personal Details are entered successfully");
		Thread.sleep(5000);
		
		
		
		Thread.sleep(10000);
		
		uiDriver.click("select Date");
		Thread.sleep(5000);
		uiDriver.click("Date");
		uiDriver.click("Date radio button");
		uiDriver.click("date Confirm");
		
		Thread.sleep(5000);
		//uiDriver.click("Package Next");
		Thread.sleep(5000);
		uiDriver.click("Button Next");
		//passed("Appointment is booked", "Appointment is booked successfully", "Appointment is booked successfully");
		uiDriver.setValue("AccountNumber", input.get("AccountNumber"));
		uiDriver.setValue("Sortcode1", input.get("Sortcode1"));
		uiDriver.setValue("Sortcode2", input.get("Sortcode2"));
		uiDriver.setValue("Sortcode3", input.get("Sortcode3"));
		
		
		
		uiDriver.click("validateddirectdebit");
		//passed("Credit Check is successfull", "Credit Check is successfull", "Credit Check is successfull");
		Thread.sleep(10000);
		uiDriver.click("Confirm Order check box");
		
		//uiDriver.click("Confirm Order button");
		Thread.sleep(40000);
		//passed("order placed successfully", "Order placed successfully", "Order placed successfully");
		*/
	}
	

}
