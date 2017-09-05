
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import cbf.engine.TestResult.ResultType;
import cbf.reporting.ExcelReporter;
import cbf.reporting.Xls_Reader;
import cbf.utils.DataRow;
import cbfx.BaseWebDriver.BaseWebModuleDriver;

public class MyAccountAgentPortal_HomeMoverDriver extends BaseWebModuleDriver {



	String InputCLI="";


	public void LaunchApp(DataRow input, DataRow output) throws IOException
	{
		uiDriver.launchApplication(input.get("url"));
		//System.out.println("---------url------------"+input.get("userId"));
		if(uiDriver.checkPage(input.get("pageName")))
		{			
			passed("Launching MyAccount App", "MyAccount App should be launched successfully", "MyAccount App launched successfully");
		}
		else
		{
			log("Launching MyAccount App", ResultType.FAILED, "MyAccount App should be launched successfully", "MyAccount App not launched successfully", true);
		}
	}

	public void Login(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		//uiDriver.setValue("CSALogin", input.get("userId"));
		uiDriver.webDr.findElement(By.id("txtCsaUsername")).sendKeys(input.get("userId"));
		uiDriver.webDr.findElement(By.id("txtCsaPassword")).sendKeys(input.get("password"));
		Thread.sleep(2000);
		uiDriver.webDr.findElement(By.xpath("//*[@id=\"pgFormat\"]/div[2]/form/table/tbody/tr[1]/td[3]/input")).click();
		uiDriver.waitForBrowserStability("10000");
		log("Login into the MyAccount Agent Portal", ResultType.DONE,"Login Successfully","Login Successfully",true);
		Thread.sleep(5000);
	}


	public void EnterCustomerDetails(DataRow input, DataRow output)
	{
		if(!input.get("AccountNumber").equals(""))
		{
			//System.out.println("22222222222222     " + "entered into Acc if loop");
			uiDriver.webDr.findElement(By.id("txtTrioAcNo")).sendKeys(input.get("AccountNumber"));
			uiDriver.webDr.findElement(By.xpath("//*[@id=\"frmSearchCustAcc\"]/table/tbody/tr/td[3]/input")).click();
			uiDriver.waitForBrowserStability("10000");
		}
		else if(!input.get("CLI").equals(""))
		{
			//System.out.println("22222222222222     " + "entered into CLI if loop");
			InputCLI = input.get("CLI");
			uiDriver.webDr.findElement(By.id("txtTrioCli")).sendKeys(InputCLI);
			uiDriver.webDr.findElement(By.xpath("//*[@id=\"frmSearchCustCLI\"]/table/tbody/tr/td[3]/input")).click();
			uiDriver.waitForBrowserStability("10000");
		}
		else if(!input.get("OnlineUserName").equals(""))
		{
			//System.out.println("22222222222222     " + "entered into UN if loop");
			uiDriver.webDr.findElement(By.id("txtTrioUsername")).sendKeys(input.get("OnlineUserName"));
			uiDriver.webDr.findElement(By.xpath("//*[@id=\"frmSearchCustUser\"]/table/tbody/tr/td[3]/input")).click();
			uiDriver.waitForBrowserStability("10000");
		}
		else
		{
			log("Enter Customer Details in MyAccount Agent portal", ResultType.FAILED, "Customer Details Entered Successfully.", "Customer Details not Entered successfully.", true);
		}
		uiDriver.webDr.findElement(By.xpath("//*[@id=\"frmCustomerInfo\"]/table/tbody/tr[2]/td[7]/div/input")).click();
		uiDriver.waitForBrowserStability("1000");
		if(uiDriver.checkPage("TalkTalk - My Account - Home Summary")==true)
		{
			log("Navigated to Home Summary Page", ResultType.DONE, "Navigated to Home Summary Page Successfully.", "Navigated to Home Summary Page", true);
		}
		else
		{
			log("Navigated to Home Summary Page", ResultType.FAILED, "Navigated to Home Summary Page Successfully.", "Not Navigated to Home Summary Page", true);
		}
	}

	public void PerformHomeMove(DataRow input, DataRow output) throws Exception
	{
		uiDriver.mouseHoverandClick("My Details", "//*[@id=\"My_details\"]", "Move Home", "//*[@id=\"Move_home\"]");
			uiDriver.waitForBrowserStability("10000");
		if(uiDriver.checkPage("TalkTalk - My Account - Homemover")==true)
		{
			log("Navigated to Home Mover Page", ResultType.DONE, "Navigated to Home Mover Page Successfully.", "Navigated to Home Mover Page", true);
		}
		else
		{
			log("Navigated to Home Summary Page", ResultType.FAILED, "Navigated to Home Mover Page Successfully.", "Not Navigated to Home Mover Page", true);
		}
		uiDriver.webDr.findElement(By.id("button-confirm")).click();
		uiDriver.waitForBrowserStability("5000");
		uiDriver.webDr.findElement(By.id("postcode")).sendKeys(input.get("PostCode"));
		uiDriver.clickButton("button", "button", "Look up address");
		Thread.sleep(30000);
		uiDriver.toSelectItemFromList("//*[@id=\"address_id\"]", input.get("Address"));
		Thread.sleep(30000);
		//uiDriver.clickButton("button", "button alpha", "Confirm address");
		uiDriver.clickOperation("//*[@id=\"confirmAddress\"]");
		Thread.sleep(60000);
		System.out.println("---------------------------     " + uiDriver.webDr.getTitle());
		//  uiDriver.clickButton("button", "button", "Continue");
		
		if(uiDriver.isElementPresent("//*[@id=\"retainNumber\"]")==true)
		{
			System.out.println("Enter Post code belongs to other exchange.");
			Thread.sleep(2000);
			//action.moveToElement(d.findElement(By.id("retainNumber"))).click();
			uiDriver.clickOperation("//*[@id=\"confirmPopup\"]/div/div[2]/div[2]/button");
			//d.findElement(By.xpath("//*[@id=\"confirmPopup\"]/div/div[2]/div[2]/button")).click();
		}
		else if(uiDriver.isElementPresent("//*[@id=\"errHead\"]")==true)
		{
			uiDriver.toGetText("//*[@id=\"errHead\"]").trim().equals("We're unable to progress with your home move request5");
			System.out.println("Error occured in the application.");
			throw new Exception("Error occured in the application.");
		}		
		uiDriver.clickOperation("//*[@id=\"btnSelectPackage\"]");
		uiDriver.waitForBrowserStability("5000");
		uiDriver.clickOperation("//*[@id=\"enddate1\"]");
		Thread.sleep(25000);
		Actions action = new Actions(uiDriver.webDr);	
		List<WebElement> WE_Date=uiDriver.webDr.findElements(By.tagName("span"));
		for(WebElement ele:WE_Date)
		{				
			String eleHref=ele.getAttribute("class");	
			System.out.println("Class1-------------- " + eleHref);
			if(eleHref.equals("date color2 pointer"))
			{			
				action.moveToElement(ele).click().perform();					
				break;	
			}

		}
		uiDriver.clickButton("button", "submit", "Continue");
		Thread.sleep(30000);
		uiDriver.clickOperation("//*[@id=\"reschedulebt\"]/div");
		Thread.sleep(60000);
		List<WebElement> WE_Date1=uiDriver.webDr.findElements(By.tagName("span"));
		for(WebElement ele:WE_Date1)
		{				
			String eleHref=ele.getAttribute("class");	
			System.out.println("Class2-------------- " + eleHref);
			if(eleHref.equals("date pointer color2"))
			{			
				action.moveToElement(ele).click().perform();	
				Thread.sleep(5000);
				break;	
			}

		}//uiDriver.webDr.findElement(By.xpath("//*[@id=\"whatNextInfo\"]")).getText()
		//if(!uiDriver.toGetText("//*[@id=\"whatNextInfo\"]").equals("That's it. We'll do the rest for you - we don't even need to visit the property.")){
		if(uiDriver.isElementPresent("//*[@id=\"slot-infoORTV\"]/p")==true){
			
			List<WebElement> rdbtns=uiDriver.webDr.findElements(By.xpath("//*[@id=\"slot-infoORTV\"]/p"));
			System.out.println("rdbtns size is --- " + rdbtns.size());
			for(int f=1;f<=rdbtns.size();f++)
			{
				boolean isenabled = uiDriver.webDr.findElement(By.xpath("//*[@id=\"slot-infoORTV\"]/p["+f+"]/input")).isEnabled();
				if(isenabled==true)
				{
					uiDriver.clickOperation("//*[@id=\"slot-infoORTV\"]/p["+f+"]/input");
					//	uiDriver.webDr.findElement(By.xpath("//*[@id=\"slot-infoORTV\"]/p["+f+"]/input")).click();
					Thread.sleep(2000);
					uiDriver.clickOperation("//*[@id=\"hm-eng-note\"]/div[1]/label[2]");
					Thread.sleep(5000);	
					//uiDriver.webDr.findElement(By.xpath("//*[@id=\"hm-eng-note\"]/div[1]/label[2]")).click();
					//uiDriver.clickButton("button", "submit", "Continue");
					uiDriver.clickOperation("//*[@id=\"button-confirmORTV\"]");
					Thread.sleep(7000);	
					break;
				}
				else
				{
					//System.out.println("enabled radio btns are not getting displayed.");
					log("Navigated to choose your go-live date webpage", ResultType.FAILED, "Appointment radio buttons should be in enable state.", "Appointment radio buttons are not in enable state.", true);
				}
			}
		}
		else
		{
			uiDriver.clickOperation("button-confirmORTV");
			Thread.sleep(7000);				
		}		
		uiDriver.clickOperation("//*[@id=\"continue\"]/button");
		Thread.sleep(25000);
		uiDriver.webDr.findElement(By.id("editCustMobileNumber")).sendKeys("07654123980");		
		uiDriver.clickOperation("//*[@id=\"basketContinue\"]");
		Thread.sleep(60000);			
		String Msg = uiDriver.toGetText("//*[@id=\"hm-thankyou\"]/div[3]/div/div/div[1]");		
		if(Msg.trim().equals("Thank you! Your order has been successful and your home move is now confirmed for the dates you have chosen"))
		{
		
			System.out.println("InputCLI-------------  "  +InputCLI);
			MyAccountAgentPortal_HomeMoverDriver.toCreateTestResults(InputCLI);
		}
		else
		{
			log("Navigated to Home Mover order confirmation webpage", ResultType.FAILED, "Message should be display correctly and Home Mover order should be place successfully.", "Message is not getting displayed correctly or Home Mover order is not placed successfully.", true);
		}
	
	}
	
	public static void toCreateTestResults(String CLI)
	{
		try{  
			//Xls_Reader HomePagexls = new Xls_Reader("D:\\Workspace\\Practice\\TestResults\\MAHomeMover.xlsx");
			//str to CLI
			System.out.println("InputCLI-------------  "  +CLI);
			//step1 : Load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  		  
			//step2 : Create  the connection object  
			Connection con=DriverManager.getConnection(  
					"jdbc:oracle:thin:@10.180.4.37:1541:ORDEVE01","dassuch","dassuch");  		
			//step3 : Create the statement object  
			Statement stmt=con.createStatement();  		
			//step4 : Execute query  
			String str1 = "select * from OM_BW_USER.CPWPROVCMD where ResourceID in (select RESOURCEID from OM_BW_USER.CPWPROVCMD where CLI= ? "
					+ "and PROVCMDNAME='LLUCease') and GWCMD_SUBDATE like sysdate and PROVCMDGWYCMDID != '0'";
			//
			//String str = "01235352218";
			System.out.println(str1);
			PreparedStatement ps = con.prepareStatement(str1);
			ps.setString(1, CLI.trim());
			//ps.setString(1, str.trim());
			ResultSet rs = ps.executeQuery();
			int i=2;
			while(rs.next())  		
			{
				System.out.println("DB Data is --- " + rs.getString("ORDERID") + "----------" + rs.getString("PROVCMDNAME")+"------------------"+ rs.getString("PROVCMDGWYCMDID") + "--------------- "+ rs.getString("PROVCMDGWYCMDID"));
				ExcelReporter.toCreateMyAccountHomeMoverScenarioTestResultReport(i, CLI, rs.getString("ORDERID"), rs.getString("PROVCMDNAME"), rs.getString("PROVCMDGWYCMDID"));
				i++;
			}
				//step5 : Close the connection object  
			con.close();  

		}
		catch(Exception e){
			System.out.println(e);}  
	}




}
