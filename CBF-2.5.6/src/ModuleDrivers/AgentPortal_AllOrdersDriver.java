package ModuleDrivers;

import static cbf.engine.TestResultLogger.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
//import com.sun.org.apache.bcel.internal.generic.GOTO;
import cbf.engine.TestResult.ResultType;
import cbf.reporting.ExcelReporter;
import cbf.utils.DataRow;
import cbfx.BaseWebDriver.BaseWebModuleDriver;
import cbfx.selenium.WebUIDriver;


public class AgentPortal_AllOrdersDriver extends BaseWebModuleDriver{

	/**/String ordref="";
	int AddressIndex=0;
	static String OrderName="";
	static String LLUCMDID="";	
	static String NGACMDID="";
	static String CRDDate="";
	static String CMMDDID="";
	Select sel;
	static String Environment="";
	static int Counter=0;
	//AgentPortal_AllOrdersDriver Agent = new AgentPortal_AllOrdersDriver();
	public void LaunchApp(DataRow input, DataRow output) throws Exception
	{
		Environment = input.get("environment");
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
		uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
	}

	public void Personaldetails(DataRow input, DataRow output) throws Exception
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
			//log("Credit Check Details Completed", ResultType.PASSED, "Credit Check Details should be Completed successfully", "Credit Check Details Completed successfully", true);
			uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
		}

		//personal details information submission
		uiDriver.selectfromList("Agent_Title", "Mr");

		uiDriver.setValue("Agent_FirstName", input.get("FirstName"));
		uiDriver.setValue("Agent_LastName", input.get("LastName"));
		uiDriver.setValue("Agent_PhoneNumber", input.get("MobileNumber"));

		uiDriver.selectfromList("Agent_DOB_Day", input.get("DOB_Date"));
		uiDriver.selectfromList("Agent_DOB_Month", input.get("DOB_Month"));
		uiDriver.selectfromList("Agent_DOB_Year", input.get("DOB_year"));		
		System.out.println("input.get(Email) ---- " + input.get("Email"));
		if(input.get("Email").equals("") || input.get("Confirm email").equals("")){
			String Email = uiDriver.toGenerateEmailAddress();
			uiDriver.setValue("Agent_Email",Email);
			uiDriver.setValue("Agent_Confirm_Email",Email);
		}else{
			uiDriver.setValue("Agent_Email", input.get("Email"));
			uiDriver.setValue("Agent_Confirm_Email", input.get("Confirm email"));
		}
		//uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
		if(uiDriver.isElementPresent("//*[@id=\"addressList_id\"]/option[1]") && Environment.equalsIgnoreCase("VIL")){
			uiDriver.clickOperation("//*[@id=\"addressList_id\"]/option[1]");
		}

		log("Personal Details Completed", ResultType.PASSED, "Personal Details should be Completed successfully", "Personal Details Completed successfully", true);		
		uiDriver.click("Agent_Personaldetails_Next");


		//Checking Credit Check confirmation button.
		List<WebElement> CC_Yes_Button= uiDriver.webDr.findElements(By.id("customer-consent"));
		if(!(CC_Yes_Button.isEmpty()))
		{
			log("Personal Details Completed", ResultType.PASSED, "Personal Details should be Completed successfully", "Personal Details Completed successfully", true);
			uiDriver.click("Agent_CC_Yes_Button"); 

		}

		uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
		Thread.sleep(5000);
		//Now control flows to Appointment and Order Completion.
	}

	public void AppointmentandOrdercompletion(DataRow input, DataRow output) throws Exception
	{		
		try
		{
			//Checking and booking Appointment if available.
			Actions action = new Actions(uiDriver.webDr);	
			System.out.println("This is first line.");
			List<WebElement> WE_Date=uiDriver.webDr.findElements(By.tagName("span"));
			int Appt_Next_button=0;
			System.out.println("This is second line."+WE_Date);		
			int counter=0;
			for(WebElement ele:WE_Date)
			{
				counter = counter+1;
				String eleHref=ele.getAttribute("class");
				if(eleHref.equals("dateactive"))
				{
					System.out.println("Entered into IF loop.");
					action.moveToElement(ele).click().perform();
					Appt_Next_button=1;
					break;	
				}
			}
			Thread.sleep(5000);
			//Checking and booking respective slot if available. 
			WebUIDriver.toBookInstallationSlot(Appt_Next_button);	
			
			if(uiDriver.isElementPresent("//*[@id=\"newlineapp\"]/div[2]/div/div/ul/li"))
			{
				System.out.println("Entered Counter loop.");
				List<WebElement> WE_Date1=uiDriver.webDr.findElements(By.tagName("span"));
				uiDriver.clickOperation("//*[starts-with(@id,\"fibrenav\")]");
				Thread.sleep(2000);
				for(WebElement ele:WE_Date1)
				{
					counter = counter+0;
					String eleHref=ele.getAttribute("class");
					if(eleHref.equals("dateactive"))
					{
						action.moveToElement(ele).click().perform();
						Appt_Next_button=1;
						break;	
					}
				}
				WebUIDriver.toBookInstallationSlot(Appt_Next_button);
			}
			
		} catch (Exception e) {
			uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
			log("Appointment skipped for Non-NewLine order", ResultType.FAILED, "Non-NewLine Order should be Completed successfully", "Non-NewLine Order Completed successfully", true);
		}
		Thread.sleep(25000);
		//Account Information submission.
		uiDriver.setValue("Agent_AccountNumber", input.get("AccountNumber"));
		uiDriver.setValue("Agent_Bank_SortCode1", input.get("Sortcode1"));
		uiDriver.setValue("Agent_Bank_SortCode2", input.get("Sortcode2"));
		uiDriver.setValue("Agent_Bank_SortCode3", input.get("Sortcode3"));
		uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
		uiDriver.click("Agent_Personaldetails_Next");
		Thread.sleep(3000);
		/*
		uiDriver.click("Agent_Check_AllCheckBoxes");
		log("Questions Details Completed", ResultType.PASSED, "Questions Details should be Completed successfully", "Questions Details Completed successfully", true);
		uiDriver.click("Agent_Personaldetails_Summary");
		uiDriver.click("Agent_Summary_Window_OK_Button");*/		
		if(uiDriver.isElementPresent("//*[starts-with(@id,\"tbox_content\")]/button")){
			uiDriver.clickOperation("//*[starts-with(@id,\"tbox_content\")]/button");
			Thread.sleep(2000);
		}		

		if(uiDriver.isElementPresent("//input[@id=\"checkall\"]")){
			uiDriver.click("Agent_Check_AllCheckBoxes");
			log("Questions Details Completed", ResultType.PASSED, "Questions Details should be Completed successfully", "Questions Details Completed successfully", true);
			uiDriver.click("Agent_Personaldetails_Summary");
			uiDriver.click("Agent_Summary_Window_OK_Button");
		}	

		uiDriver.setValue("Agent_Enquiry_Password", input.get("password"));
		uiDriver.setValue("Agent_Confirm_Enquiry_Password", input.get("confirm password"));
		uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
		uiDriver.click("Agent_TandC");

		if(OrderName.trim().equals("FTTP")){
			System.out.println("Entered into FTTP UFOTC loop.");
			uiDriver.clickOperation("//*[@id=\"ufotandc\"]");}
		log("Summary Details Completed", ResultType.PASSED, "Summary Details should be Completed successfully", "Summary Details Completed successfully", true);
		uiDriver.click("Agent_Confirm Order");
		uiDriver.click("Agent_Confirm_Next");
		ordref=uiDriver.getAttribute("Agent_Order_Reference","innerText");
		uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Your Order Reference is:"+ordref);
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		log("Order Completed", ResultType.PASSED, "Order should be Completed successfully", "Order Completed successfully", true);
		passed("Order Completed", "Order Reference Number is : "+ ordref," ");
		//Transaction completes here.
		if(!ordref.equals(""))
		{
			ExcelReporter.toCreatePlaceOrdersReferenceNumber(OrderName,ordref,Environment);

			/*if(Environment.equalsIgnoreCase("eve") &&(OrderName.equals("Fast Broadband"))){
				AgentPortal_AllOrdersDriver.toCreateTestResults(ordref); 
				String[] command = {"cmd.exe", "/c", "Start", "C:\\SoapUI\\TestData Automation\\LLU_KCI.bat","C:\\SoapUI\\TestData Automation\\LLU_KCI.bat"};
				//new String[] { "cmd.exe", "/c", "server.bat" }
				Process p =  Runtime.getRuntime().exec(command); 
			}
			else if(Environment.equalsIgnoreCase("VIL")){
				AgentPortal_AllOrdersDriver.toCreateTestResults(ordref); 
			}*/
			if(Environment.equals("VIL")){
				System.out.println("Environment VIL ----------- " +Environment );
				AgentPortal_AllOrdersDriver.toCreateTestResults(ordref,Environment); 
			}			
			else if(Environment.equals("EVE")){
				AgentPortal_AllOrdersDriver.toCreateTestResults(ordref,Environment); 
				System.out.println("Environment EVE ----------- " +Environment );
				System.out.println("OrderName ----------- " +OrderName );
				System.out.println("Counter ----------- " +Counter );
				
				/*if(OrderName.trim().equals("Fast Broadband") && Counter==1)
				{
					System.out.println("Entered -- " + OrderName);
					String[] command = {"cmd.exe", "/c", "Start", "C:\\SoapUI\\TestData Automation\\LLU_KCI.bat","C:\\SoapUI\\TestData Automation\\LLU_KCI.bat"};
					//new String[] { "cmd.exe", "/c", "server.bat" }
					Process p =  Runtime.getRuntime().exec(command); 
					Thread.sleep(180000);
				}
				else if(OrderName.trim().equals("Fibre Broadband")  && Counter==1)
				{
					System.out.println("Entered -- " + OrderName);
					String[] command = {"cmd.exe", "/c", "Start", "C:\\SoapUI\\TestData Automation\\SIM2_KCI.bat","C:\\SoapUI\\TestData Automation\\SIM2_KCI.bat"};
					//new String[] { "cmd.exe", "/c", "server.bat" }
					Process p =  Runtime.getRuntime().exec(command); 
					Thread.sleep(180000);
				}
				else if(OrderName.trim().equals("Faster 150 Fibre")  && Counter==1)
				{
					System.out.println("Entered -- " + OrderName);
					String[] command = {"cmd.exe", "/c", "Start", "C:\\SoapUI\\TestData Automation\\SIM2_KCI.bat","C:\\SoapUI\\TestData Automation\\SIM2_KCI.bat"};
					//new String[] { "cmd.exe", "/c", "server.bat" }
					Process p =  Runtime.getRuntime().exec(command); 
					Thread.sleep(180000);
				}*/
			}

		}
		else
		{
			uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
			log("Order Completed", ResultType.ERROR, "Order should be Completed successfully", "Order not Completed successfully", true);
		}
	}

	public void SimplyBroadband(DataRow input, DataRow output) throws Exception
	{

		Actions action = new Actions(uiDriver.webDr);
		//Thread.sleep(2000);
		//Home Page -> Post Code and Agent ID submission.
		uiDriver.setValue("Agent_Postcode_NewProvide", input.get("Postcode"));
		uiDriver.setValue("Agent_AgentID_NewProvide", input.get("AgentID"));
		//log("Address Selection Completed", ResultType.PASSED, "Address Selection should be Completed successfully", "Address Selection successfully", true);
		uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
		uiDriver.click("Agent_Checkavailability_NewProvide");
		Thread.sleep(3000);
		if(uiDriver.isElementPresent("//*[@class=\"box_t1 thinbox\"]/ul/li[1]")){
			
			String ActualMsg = uiDriver.toGetText("//*[@class=\"box_t1 thinbox\"]/ul/li[1]");			
			String ExpectedMsg = "We cannot find that address, please check the postcode and if the issue persists advise the customer we cannot identify that address.";
			System.out.println(ActualMsg);
			System.out.println(ExpectedMsg);
			if(ActualMsg.trim().equalsIgnoreCase(ExpectedMsg.trim()))
			{
				System.out.println("If");
				throw new Exception("Getting error message upon enetering the Postcode.Check the TestData.");
			}
			else
			{
				System.out.println("Else");
			}
		}
		sel = new Select(uiDriver.webDr.findElement(By.id("bt-address")));
		//Address Tab ->Address Selection.
		if(!input.get("Address").equals("")){
			uiDriver.selectfromList("Agent_AddressTable", input.get("Address"));
			System.out.println("Given Address is selected.");
		}
		else{
			AddressIndex = 2;
			System.out.println("Input Address not available in TD file.");			
			sel.selectByIndex(AddressIndex);
		}
		//
		AddressIndex = uiDriver.toSelectAddressindexinMPFAgentPortal();
		if(!input.get("Package").equals("FTTP")){
			uiDriver.click("Agent_Lived in this address for more than 30 days");}

		//log("Address Selection Completed", ResultType.PASSED, "Address Selection should be Completed successfully", "Address Selection successfully", true);
		uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
		uiDriver.click("Agent_AddressNext");
		Thread.sleep(7000);
		//uiDriver.waitForBrowserStability("30");
		if((uiDriver.isElementPresent("//*[@id=\"page-middle\"]/div[1]/div[3]")==true || uiDriver.isElementPresent("//*[@id=\"page-middle\"]/div[1]/div[2]/h2")==true) && !uiDriver.webDr.getTitle().contentEquals("Packages Available") )
		{
			//log("Address Selection page",ResultType.,"Got the error message as - "+ uiDriver.toGetText("//*[@id=\"page-middle\"]/div[1]/div[3]"),"",true);
			System.out.println("Got error message upon selecting the given address - " + uiDriver.toGetText("//*[@id=\"page-middle\"]/div[1]/div[3]"));
			if(uiDriver.isElementPresent("//*[@class=\"btn-back\"]")==true){
				uiDriver.clickOperation("//*[@class=\"btn-back\"]");  ////*[@id=\"page-middle\"]/div[1]/div[2]/ul/li
				Thread.sleep(7000);
			}
			do{
				AddressIndex = AddressIndex+1;
				System.out.println("Address Index -2  ----  " + AddressIndex);
				//sel.selectByIndex(addressIndex);
				if(uiDriver.isElementPresent("//*[@class=\"btn-back\"]")==true)
					uiDriver.clickOperation("//*[@class=\"btn-back\"]");
				Thread.sleep(7000);				
				Select sel1 = new Select(uiDriver.webDr.findElement(By.id("bt-address")));
				//Thread.sleep(500);
				sel1.selectByIndex(AddressIndex);
				if(!input.get("Package").equals("FTTP")){
					uiDriver.click("Agent_Lived in this address for more than 30 days");}
				uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
				uiDriver.click("Agent_AddressNext");
				Thread.sleep(2000);
				//uiDriver.toClickMPFLineRadioButton(input.get("Package"));
			}while(uiDriver.isElementPresent("//*[@id=\"page-middle\"]/div[1]/div[2]/h2")==true || uiDriver.isElementPresent("//*[@id=\"page-middle\"]/div[1]/div[3]/h2")==true);

		}
		/*else
		{
			throw new Exception("Element not found");
		}*/
		System.out.println("()()()()");
		//Lines Tab -> Checking Existing line if available.
		uiDriver.toClickMPFLineRadioButton(input.get("Package"));
		Thread.sleep(20000);
		//Packages Tab -> Checking and Selecting appropriate package.	
		if((input.get("Package").equals("Fibre Broadband") && uiDriver.isElementPresent("//*[@id=\"packageBox-292\"]/div[1]/p[1]/strong")==false)
				|| input.get("Package").equals("Faster 150 Fibre") && uiDriver.isElementPresent("//*[@id=\"packageBox-294\"]/div[1]/p[1]/strong")==false)
		{
			do{
				//d.findElement(By.xpath("//*[@id=\"page-top\"]/div[3]/p[2]/span/a")).click();
				uiDriver.clickOperation("//*[@id=\"page-top\"]/div[3]/p[2]/span/a");
				uiDriver.setValue("Agent_Postcode_NewProvide", input.get("Postcode"));
				uiDriver.setValue("Agent_AgentID_NewProvide", input.get("AgentID"));
				uiDriver.click("Agent_Checkavailability_NewProvide");
				Thread.sleep(2000);
				AddressIndex = AddressIndex+1;
				System.out.println("Address Index -2  ----  " + AddressIndex);
				//sel.selectByIndex(addressIndex);
				//Select sel = new Select(uiDriver.webDr.findElement(By.id("bt-address")));
				sel.selectByIndex(AddressIndex);
				if(!input.get("Package").equals("FTTP")){
					uiDriver.click("Agent_Lived in this address for more than 30 days");
					uiDriver.click("Agent_AddressNext");
					Thread.sleep(2000);}
				uiDriver.toClickMPFLineRadioButton(input.get("Package"));
			}while(uiDriver.isElementPresent("//*[@id=\"packageBox-292\"]/div[1]/p[1]/strong")==false);
		}
		//

		//Contract Length -> Selecting the appropriate contract length.
		System.out.println("input.get(Contract_Length).trim()  ---  " + input.get("Contract_Length").trim());
		if(input.get("Contract_Length").trim().contains("24 month"))
		{
			List<WebElement> conLen=uiDriver.webDr.findElements(By.xpath("//div[2]/div[1]/input"));
			for(WebElement ele:conLen)
			{
				action.moveToElement(ele).click().perform();
			}
		}
		else if(input.get("Contract_Length").trim().contains("18 month"))
		{
			/*List<WebElement> conLen=uiDriver.webDr.findElements(By.xpath("//div[2]/div[5]/input"));  ////*[@id="package-box-gfast30156"]/div[2]/div[4]/input
			for(WebElement ele:conLen)
			{
				action.moveToElement(ele).click().perform();
			}*/
			System.out.println("Entered into 18th month contract.");
			uiDriver.clickOperation("//*[@id=\"package-box-gfast30156\"]/div[2]/div[4]/input");
		}
		else
		{
			List<WebElement> conLen=uiDriver.webDr.findElements(By.xpath("//div[2]/div[9]/input"));
			for(WebElement ele:conLen)
			{
				action.moveToElement(ele).click().perform();
			}
		}
		OrderName = input.get("Package");
		System.out.println("Input Package -------------------------------------- " + input.get("Package"));
		uiDriver.selectPackage_agent(input.get("Package"));
		Thread.sleep(5000);
		uiDriver.takeScreenShots(uiDriver.webDr.getTitle());

		//Options Tab ->Selecting appropriate details.

		/*
		uiDriver.webDr.findElement(By.xpath("//*[@id=\"options\"]/div[2]/div[3]/h2/span")).click();
		//Options Tab ->Checking TV option availability and selecting accordingly.
		boolean TV_Option;
		String IsTvIncluded = input.get("IsTvIncluded");
		System.out.println("IsTvIncluded--------------- " + IsTvIncluded);
		try {
			if(IsTvIncluded.equalsIgnoreCase("No"))
				TV_Option= uiDriver.checkAtttribute("Agent_No TV", "id", "tv_no");	
			else
				TV_Option= uiDriver.checkAtttribute("Agent_Yes TV", "id", "tv_yes");


		} catch (Exception e)
		{
			TV_Option=false;	
			System.out.println("Entered TV Catch block.");
		}

		if(TV_Option)
		{		
			if(IsTvIncluded.equalsIgnoreCase("No"))
				uiDriver.click("Agent_No TV");
			else{
				uiDriver.click("Agent_Yes TV");

			}
		}*/
		System.out.println( input.get("IsTvIncluded")+input.get("IsTvAerialRequire")+input.get("SetupBox")+input.get("BundleBoost")+input.get("TvInstallation")+input.get("TVorRouterLocation")+input.get("IsPowerLineAdapter"));
		uiDriver.toSelectTV(input.get("IsTvIncluded"),input.get("IsTvAerialRequire"),input.get("SetupBox"),input.get("BundleBoost"),input.get("TvInstallation"),input.get("TVorRouterLocation"),input.get("IsPowerLineAdapter"));

		log("Package Selection Completed", ResultType.PASSED, "Package Selection should be Completed successfully", "Package Selection Completed successfully", true);
		uiDriver.takeScreenShots(uiDriver.webDr.getTitle());
		uiDriver.click("Agent_Place Order");

		//Now Control Flows to Personal Details Class

	}

	public static void ProvisioningLLUorder(){

		try{
			String CRDDate="";
			int j= AgentPortal_AllOrdersDriver.toGetLastRowNumberofAgentPortalOrdersTestResults();
			String CMDID = ExcelReporter.toGetCMDIDfromGivenRowNumber(j);
			//String CMDName = ExcelReporter.toGetCMDNAMEfromGivenRowNumber(j);

			//step1 : Load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  		  
			//step2 : Create  the connection object  
			Connection con=DriverManager.getConnection(  
					"jdbc:oracle:thin:@10.180.4.37:1541:ORDEVE01","dassuch","dassuch");  		
			//step3 : Create the statement object  
			Statement stmt=con.createStatement();  		
			//step4 : Execute query  
			String str1 = "select CONCAT(to_char(c.customerrequireddate, 'YYYY-MM-DD'),'T00:00:00') as CRDDate,C.orderid from OM_BW_USER.CPWPROVCMD p"
					+ "join OM_BW_USER.CPWORDER c "
					+ "on p.ORDERID=c.ORDERID and p.PROVCMDGWYCMDID = ? "
					+ " and c.orderplacementdate like sysdate";
			PreparedStatement ps = con.prepareStatement(str1);			
			ps.setString(1, CMDID);
			//ps.setString(1, str.trim());
			Thread.sleep(75000);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
			{
				CRDDate = rs.getString("CRDDate");
				ExcelReporter.toSetCRDfromGivenRowNumber(2, CRDDate);
			}
			con.close();
		}
		catch(Exception e){
			System.out.println(e);
		}

	}

	public static String CharInsertion(String Str,int Index)
	{
		String str=Str;
		String b=str.substring(0,Index-1 );
		String c=str.substring(Index-1,str.length());
		str=b+"L"+c;
		return str.trim();		
	}

	public static int toGetLastRowNumberofAgentPortalOrdersTestResults() throws IOException{
		int rownum=0;
		InputStream myxls = new FileInputStream(System.getProperty("user.dir")+"\\TestResults\\Orders_AgentPortal.xlsx");
		Workbook book = new XSSFWorkbook(myxls);
		Sheet sheet = book.getSheet("Results");
		//System.out.println(sheet.getLastRowNum());
		rownum = sheet.getPhysicalNumberOfRows();
		//int rowsNum = sheet.getLastRowNum();
		System.out.println("++++++++++++++++++++++++++++-------------- "  + rownum);
		return (rownum+1);
	}



	public static void toCreateTestResults(String ordref,String Environment)
	{
		//Connection	con;
		try{  
			
			//Xls_Reader HomePagexls = new Xls_Reader("D:\\Workspace\\Practice\\TestResults\\MAHomeMover.xlsx");
			//str to CLI
			System.out.println("ordref*+ -------------  "  +ordref);
			//step1 : Load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  		  
			//step2 : Create  the connection object  
			
			
			 /*con=DriverManager.getConnection(  
					"jdbc:oracle:thin:@10.180.4.39:1541:ORDVIL01","readonly","r3ad0nly");*/
		
				Connection	con=DriverManager.getConnection(  
						"jdbc:oracle:thin:@10.180.4.37:1541:ORDEVE01","dassuch","dassuch");  	
					
			//step3 : Create the statement object  
		//	Statement stmt=con.createStatement(); 
			
			//step4 : Execute query  
			/*String str1 = "select * from OM_BW_USER.CPWPROVCMD where ResourceID= ? "
					+ "and LASTUPDATED like sysdate and PROVCMDGWYCMDID != '0'";*/
			String str1 = "select * from OM_BW_USER.CPWPROVCMD where ResourceID= ? "
					+ "and PROVCMDGWYCMDID != '0'";
			//
			//String str = "01235352218";
			System.out.println(str1);
			PreparedStatement ps = con.prepareStatement(str1);
			String ref = CharInsertion(ordref.trim(),2);
			System.out.println("Reference ------------------- " + ref);
			ps.setString(1, ref);
			//ps.setString(1, str.trim());
			Thread.sleep(120000);
			ResultSet rs = ps.executeQuery();

			//int i=2;
			int j= AgentPortal_AllOrdersDriver.toGetLastRowNumberofAgentPortalOrdersTestResults();
			//System.out.println("Last Row is ---------------  " + j);
			int i =j+1;
			System.out.println("Last Row is ---------------  " + j + " -----------  " + i);		
			
			/*if(!rs.next()){
			log("CommandID fetching.", ResultType.ERROR, "CommandID fetching from DB should Success.", "CommandID fetching from DB is not Success.", true);
		}*/
			while(rs.next())		
			{
				CMMDDID = rs.getString("PROVCMDGWYCMDID");
				String cmdname = rs.getString("PROVCMDNAME");
				System.out.println("1. DB Data is --- " + rs.getString("ORDERID") + "----------" + rs.getString("PROVCMDNAME")+"------------------"+ rs.getString("PROVCMDGWYCMDID"));
				ExcelReporter.toCreatePlaceOrdersTestResultReport(i,OrderName,ExcelReporter.currentDate(),ordref, rs.getString("ORDERID"), rs.getString("PROVCMDNAME"), rs.getString("PROVCMDGWYCMDID"), rs.getString("CLI"));

				if(cmdname.trim().equalsIgnoreCase("LLUProvideNew") || cmdname.trim().equalsIgnoreCase("LLUProvideTakeover")){

					//ExcelReporter.toSetLLUCMDIDfromGivenRowNumber(2,CMDID);
					LLUCMDID = CMMDDID;
				}
				else
				{
					//ExcelReporter.toSetNGACMDIDfromGivenRowNumber(2,CMDID);
					NGACMDID = CMMDDID;
				}
				i++;
				Counter =1;
			}
			String strCRDDATE = "select CONCAT(to_char(c.customerrequireddate, 'YYYY-MM-DD'),'T00:00:00') as CRDDate,C.orderid from OM_BW_USER.CPWPROVCMD p "
					+ "join OM_BW_USER.CPWORDER c "
					+ "on p.ORDERID=c.ORDERID where p.PROVCMDGWYCMDID = ? "
					+ "and c.orderplacementdate like sysdate";
			PreparedStatement ps1 = con.prepareStatement(strCRDDATE);			
			ps1.setString(1, CMMDDID);
			//ps.setString(1, str.trim());
			//Thread.sleep(75000);
			ResultSet rs1 = ps1.executeQuery();
			if(rs1.next())
			{
				CRDDate = rs1.getString("CRDDate");
				//ExcelReporter.toSetCRDfromGivenRowNumber(2, CRDDate);
			}
			else
			{
				log("CRDdate fetching.", ResultType.ERROR, "CRDdate fetching from DB should Success.", "CRDdate fetching from DB is not Success.", true);
			}
			//step5 : Close the connection object  
			System.out.println("DB Data is --- " +CRDDate);
			ExcelReporter.printCustomerList(LLUCMDID, NGACMDID, CRDDate);
			con.close();  
		}
		catch(Exception e){
			System.out.println(e);}  
	}



	


}
