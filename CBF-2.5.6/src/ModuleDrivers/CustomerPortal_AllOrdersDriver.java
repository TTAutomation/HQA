package ModuleDrivers;

import static cbf.engine.TestResultLogger.log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import cbf.engine.TestResult.ResultType;
import cbf.reporting.ExcelReporter;
import cbf.utils.DataRow;
import cbfx.BaseWebDriver.BaseWebModuleDriver;


public class CustomerPortal_AllOrdersDriver extends BaseWebModuleDriver{

	/**/String ordref="";
	int AddressIndex=0;
	static String Product;
	static String OrderName="";
	static String LLUCMDID="";	
	static String NGACMDID="";
	static String CRDDate="";
	static String CMMDDID="";
	//Select sel;
	static String Environment="";
	static int Counter=0;
	static String PersonalDetails_MobileNumber="";
	static String MADetails_EmailID = "";
	static String PersonalDetails_FName ="";
	static String PersonalDetails_LName = "";
	static String Order_Reference="";	
	//AgentPortal_AllOrdersDriver Agent = new AgentPortal_AllOrdersDriver();

	public void LaunchApp(DataRow input, DataRow output) throws Exception
	{
		Environment = input.get("environment");
		uiDriver.launchApplication(input.get("url"));
		Thread.sleep(2000);

		uiDriver.toSelectItemFromList("//*[@class='ng-pristine ng-valid']/select", "test");
		uiDriver.clickOperation("//*[@class=\"ng-isolate-scope\"]/section/table[1]/tbody/tr[8]/td/input");

		uiDriver.clickOperation("//*[@id=\"content\"]/test/div/div[1]/div[1]/div/environment-changer/section/form[2]/table/tbody/tr[2]/td[2]/input");
		uiDriver.clearOperation("//*[@id=\"content\"]/test/div/div[1]/div[1]/div/environment-changer/section/form[2]/table/tbody/tr[2]/td[2]/input");
		uiDriver.SendKeysOperation("//*[@id=\"content\"]/test/div/div[1]/div[1]/div/environment-changer/section/form[2]/table/tbody/tr[2]/td[2]/input", input.get("MAEndPoint"));

		uiDriver.clickOperation("//*[@id=\"content\"]/test/div/div[1]/div[1]/div/environment-changer/section/form[2]/table/tbody/tr[3]/td[2]/input");
		uiDriver.clearOperation("//*[@id=\"content\"]/test/div/div[1]/div[1]/div/environment-changer/section/form[2]/table/tbody/tr[3]/td[2]/input");
		uiDriver.SendKeysOperation("//*[@id=\"content\"]/test/div/div[1]/div[1]/div/environment-changer/section/form[2]/table/tbody/tr[3]/td[2]/input", input.get("SalesEndPoint"));
		//takeScreenShots(uiDriver.toGetPageTitle());
		uiDriver.clickOperation("//*[@id=\"content\"]/test/div/div[1]/div[1]/div/environment-changer/section/form[2]/table/tbody/tr[8]/td/input[1]");
		uiDriver.clickOperation("//*[@class=\"header__logo\"]/a/span[2]");		
		Thread.sleep(2000);		
	}

	public void SelectProduct(DataRow input, DataRow output) throws Exception{

		Product = input.get("ProductName");
		String Speed= input.get("Speed");
		String TVType = input.get("TVType");
		String Channel = input.get("Channel");
		System.out.println("+++---  " + Product + " " + Speed + " "+ TVType + " "+ Channel);		
		////takeScreenShots(uiDriver.toGetPageTitle());
		System.out.println("--- " +Product);
		if(Product.trim().equalsIgnoreCase("Fast"))
		{
			System.out.println("Eneterred");
			uiDriver.clickOperation("//*[@href=\"/feature/master/customise/set-package/fast\" and text()='Get Started']");
			Thread.sleep(2000);
			uiDriver.clickOperation("//*[@id=\"packageSelector-fast-id\"]/span/span[2]");			
		}
		else if(Product.equalsIgnoreCase("Faster Fibre")){

			uiDriver.clickOperation("//*[@href=\"/feature/master/customise/set-package/faster\" and text()='Get Started']");			
			Thread.sleep(2000);
			if(Speed.equalsIgnoreCase("38Mb"))
			{
				uiDriver.clickOperation("//*[@id=\"packageSelector-faster-id\"]/span/span[2]/segmented-price/div");
			}
			else if(Speed.equalsIgnoreCase("76Mb"))
			{
				uiDriver.clickOperation("//*[@id=\"packageSelector-fastest-id\"]/span/span[2]/segmented-price/div");
			}
		}		
		//takeScreenShots(uiDriver.toGetPageTitle()+"_1");
		if(TVType.equalsIgnoreCase("TV Box")){
			uiDriver.clickOperation("//*[text()=\"TV Box\" and @class=\"ng-binding ng-scope\"][1]");
		}
		else if(TVType.equalsIgnoreCase("TV Plus Box")){
			uiDriver.clickOperation("//*[text()=\"TV Plus Box\" and @class=\"ng-binding ng-scope\"][1]");
		}
		if(Channel.equalsIgnoreCase("Sky Cinema")){
			uiDriver.clickOperation("//*[@id=\"tvBoosts-sky_cinema_30252-id\"]/span[1]/span/icon-checkbox-price/span");		
		}
		uiDriver.clickOperation("//*[@id=\"next-id\"]");
		Thread.sleep(3000);		
	}

	public void AddressDetails(DataRow input, DataRow output) throws Exception{
		String PostCode = input.get("PostCode");
		String TelephoneNumber = input.get("TelephoneNumber");
		String Address = input.get("Address");
		String Address_Lived = input.get("Address_Lived");
		String Line_type = input.get("Line_type");
		String CurrentServiceProvider = input.get("CurrentServiceProvider");
		String LandLineNumber = input.get("LandLineNumber");



		uiDriver.SendKeysOperation("//*[@name='postcode' and @type='text']", PostCode);
		uiDriver.SendKeysOperation("//*[@name='phoneNumber' and @type='tel']", TelephoneNumber);
		uiDriver.clickOperation("//*[@id=\"confirmOrderButton-id\"]");
		Thread.sleep(15000);		
		//Address Selection		
		List<WebElement> listAddress = uiDriver.webDr.findElements(By.xpath("//*[@id=\"address-list-id\"]/div/label"));
		int size = listAddress.size();
		System.out.println("List Address Size = " + size);
		if(size==0)
			throw new Exception("No Address lines are available for the given PostCode.");
		String Add1 = "//*[@id=\"address-list-id\"]/div[2]/label[";
		String Add2 = "]/span/p";
		String rdbtn1 = "/ancestor::label[";
		String rdbtn2 = "]/div[1]";
		String Adres1 ="//*[@id=\"address-list-id\"]/div[2]/label[";
		String Adres2 ="]/div/following-sibling::span/p";
		for(int i=1;i<=size;i++){
			if(Address.equalsIgnoreCase("")){
				uiDriver.webDr.findElement(By.xpath(Add1+i+1+Add2+rdbtn1+i+rdbtn2)).click();
				String SelectedAddress = uiDriver.webDr.findElement(By.xpath(Adres1+i+1+Adres2)).getText();
				System.out.println("Selected Address is - " + SelectedAddress );
				break;
			}
			else if(uiDriver.webDr.findElement(By.xpath(Add1+i+Add2)).getText().trim().equalsIgnoreCase(Address))
			{
				uiDriver.webDr.findElement(By.xpath("//*[text()='"+Address+"']/ancestor::label/div")).click();
				break;
			}

		}
		Thread.sleep(5000);
		if(Address_Lived.trim().equalsIgnoreCase("more than 30 days"))
		{
			uiDriver.webDr.findElement(By.xpath("//*[@id=\"wlto-resident-living-id\"]/div[1]")).click();
		}
		else if(Address_Lived.trim().equalsIgnoreCase("within 30 days"))
		{
			uiDriver.webDr.findElement(By.xpath("//*[@id=\"wlto-resident-moved-id\"]/div")).click();
		}
		else if(Address_Lived.trim().equalsIgnoreCase("moving to address"))
		{
			uiDriver.webDr.findElement(By.xpath("//*[@id=\"wlto-resident-moving-id\"]/div")).click();
		}		
		uiDriver.webDr.findElement(By.xpath("//*[@id=\"confirmOrderButton-id\"]")).click();		
		Thread.sleep(35000);

		if(Line_type.equalsIgnoreCase("Existing"))
		{
			if(uiDriver.isElementPresent("//*[@name=\"SwitchLine\"][1]/div[1]")){
				uiDriver.webDr.findElement(By.xpath("//*[@name=\"SwitchLine\"][1]/div[1]")).click();

				if(CurrentServiceProvider.equalsIgnoreCase("Sky"))
				{
					uiDriver.webDr.findElement(By.xpath("//*[text()=\"Sky\"]")).click();
				}
				else if(CurrentServiceProvider.equalsIgnoreCase("Virgin"))
				{
					uiDriver.webDr.findElement(By.xpath("//*[text()=\"Virgin\"]")).click();
				}
				else if(CurrentServiceProvider.equalsIgnoreCase("Others"))
				{
					uiDriver.webDr.findElement(By.xpath("//*[text()=\"Others\"]")).click();
				}			
				uiDriver.webDr.findElement(By.xpath("//*[@name=\"phoneNumber\" and @type=\"tel\"]")).sendKeys(LandLineNumber);
			}
		}
		else if(Line_type.equalsIgnoreCase("New Line"))
		{
			if(uiDriver.isElementPresent("//*[@name=\"SwitchLine\"][2]/div[1]")){
				uiDriver.webDr.findElement(By.xpath("//*[@name=\"SwitchLine\"][2]/div[1]")).click();
			}
		}
		if(uiDriver.isElementPresent("//*[@id=\"confirmOrderButton-id\"]")){
			uiDriver.webDr.findElement(By.xpath("//*[@id=\"confirmOrderButton-id\"]")).click();		
			Thread.sleep(40000);		
		}
		uiDriver.webDr.findElement(By.xpath("//*[@id=\"next-id\"]")).click();	
		Thread.sleep(5000);
	}

	public void PersonalDetails(DataRow input, DataRow output) throws Exception{

		String PersonalDetails_Title = input.get("PersonalDetails_Title");
		PersonalDetails_FName = input.get("PersonalDetails_FName");
		PersonalDetails_LName = input.get("PersonalDetails_LName");
		PersonalDetails_MobileNumber = input.get("PersonalDetails_MobileNumber");
		MADetails_EmailID = input.get("MADetails_EmailID");
		String MADetails_ConfirmEmailID = input.get("MADetails_ConfirmEmailID");
		String MADetails_Pwd = input.get("MADetails_Pwd");
		String MADetails_ConfirmPwd = input.get("MADetails_ConfirmPwd");




		uiDriver.toSelectItemFromList("//*[@id=\"title-id\"]",PersonalDetails_Title);
		uiDriver.SendKeysOperation("//*[@id=\"first-name-id\"]", PersonalDetails_FName);
		uiDriver.SendKeysOperation("//*[@id=\"last-name-id\"]", PersonalDetails_LName);
		uiDriver.SendKeysOperation("//*[@name=\"mobilenumber\" and @type= \"tel\"]", PersonalDetails_MobileNumber);
		uiDriver.SendKeysOperation("//*[@id=\"email-id\"]", MADetails_EmailID);
		uiDriver.SendKeysOperation("//*[@id=\"email-confirm-id\"]", MADetails_ConfirmEmailID);
		uiDriver.SendKeysOperation("//*[@id=\"password-id\"]", MADetails_Pwd);
		uiDriver.SendKeysOperation("//*[@id=\"password-confirm-id\"]", MADetails_ConfirmPwd);
		if(uiDriver.isElementPresent("//*[@name=\"checkbox1\"]/following-sibling::span"))
			uiDriver.clickOperation("//*[@name=\"checkbox1\"]/following-sibling::span");

		uiDriver.toSelectItemFromList("//*[@name=\"day\"]","19");
		uiDriver.toSelectItemFromList("//*[@name=\"month\"]","October");
		uiDriver.toSelectItemFromList("//*[@name=\"year\"]","1981");
		if(uiDriver.isElementPresent("//*[@id=\"confirmOrderButton-id\"]"))
			uiDriver.clickOperation("//*[@id=\"confirmOrderButton-id\"]");
		Thread.sleep(2000);			
		if(uiDriver.isElementPresent("//*[@error-placeholder=\"Move-in date: - Month\"]")){
			uiDriver.toSelectItemFromList("//*[@error-placeholder=\"Move-in date: - Month\"]","October");
			uiDriver.toSelectItemFromList("//*[@error-placeholder=\"Move-in date: - Year\"]","2010");
			uiDriver.clickOperation("//*[text()=\"Confirm move-in date\"]");
			Thread.sleep(2000);
		}
		uiDriver.clickOperation("//*[@id=\"confirmOrderButton-id\"]");
		Thread.sleep(15000);
		System.out.println("Personal Details Completed");
	}

	public void AppointmentAndOrderCompletion(DataRow input, DataRow output) throws Exception{

		String TVInstallationOption = input.get("TVInstallationOption");
		String TVandBBLocation = input.get("TVandBBLocation");
		String AccountNumber = input.get("AccountNumber");
		String SortCode = input.get("SortCode");
		System.out.println(TVInstallationOption + " "+ TVandBBLocation + " "+AccountNumber + " "+SortCode);

		if(uiDriver.toGetPageTitle().trim().equalsIgnoreCase("TalkTalk Order - Installation")){
			if(uiDriver.isElementPresent("//*[@id=\"datePickerOpenModalButton-id\"]")){
				//takeScreenShots(uiDriver.toGetPageTitle());
				uiDriver.clickOperation("//*[@id=\"datePickerOpenModalButton-id\"]");
				uiDriver.clickOperation("//*[@class=\"btn btn-default btn-sm btn-info active\"]/span");

				if(uiDriver.webDr.findElement(By.xpath("//*[@id=\"datePickerAppointmentComponent-id\"]/div[2]/label[1]/div")).isEnabled()){
					uiDriver.clickOperation("//*[@id=\"datePickerAppointmentComponent-id\"]/div[2]/label[1]/div");
					//takeScreenShots(uiDriver.toGetPageTitle()+"_1");
					uiDriver.clickOperation("//*[@id=\"datePickerConfirmAppointmentButton-id\"]");				
				}	
			}
		}
		if(TVInstallationOption.equals("Self")){

			if(uiDriver.isElementPresent("//*[@id=\"left-col-sticky-basket-spacer\"]/div[1]/installation/form/div[1]/section[1]/label[1]/span/span[2]")){
				uiDriver.clickOperation("//*[@id=\"left-col-sticky-basket-spacer\"]/div[1]/installation/form/div[1]/section[1]/label[1]/span/span[2]");
				Thread.sleep(2000);}
			if(TVandBBLocation.equals("Less than 3 metres apart") || TVandBBLocation.equals("Over 3 metres apart") || 
					TVandBBLocation.equals("In different rooms"))
				System.out.println("//*[text()='"+TVandBBLocation+"']");
			uiDriver.clickOperation("//*[text()='"+TVandBBLocation+"']");

			Thread.sleep(2000);
			uiDriver.clearOperation("//*[text()=\"Mobile Number:\"]/ancestor::label/input");
			uiDriver.SendKeysOperation("//*[text()=\"Mobile Number:\"]/ancestor::label/input", PersonalDetails_MobileNumber);

			uiDriver.clearOperation("//*[text()=\"Email:\"]/ancestor::label/input");			
			uiDriver.SendKeysOperation("//*[text()=\"Email:\"]/ancestor::label/input", MADetails_EmailID);

			uiDriver.clickOperation("//*[@id=\"confirmOrderButton-id\"]");			
			Thread.sleep(30000);

			if(!uiDriver.toGetPageTitle().equals("TalkTalk Order - Payment")){
				Thread.sleep(25000);
			}
			System.out.println(uiDriver.toGetPageTitle());
		}
		else if(TVInstallationOption.equals("Engineer")){
			uiDriver.clickOperation("//*[@id=\"left-col-sticky-basket-spacer\"]/div[1]/installation/form/div[1]/section[1]/label[2]/span/span[2]");
			Thread.sleep(2000);
			uiDriver.clickOperation("//*[@id=\"confirmOrderButton-id\"]");
			Thread.sleep(50000);
			System.out.println(uiDriver.toGetPageTitle());
			if(!uiDriver.toGetPageTitle().equals("TalkTalk Order - Payment")){
				Thread.sleep(25000);
			}
			System.out.println(uiDriver.toGetPageTitle());
		}	

		if(uiDriver.toGetPageTitle().trim().equals("TalkTalk Order - Payment")){ 			

			uiDriver.SendKeysOperation("//*[@id=\"holder-name-id\"]", PersonalDetails_FName + " " +PersonalDetails_LName);
			uiDriver.SendKeysOperation("//*[@id=\"account-number-id\"]/label/input", AccountNumber);
			uiDriver.SendKeysOperation("//*[@id=\"sort-code-id\"]/label/div/input", SortCode);
			uiDriver.clickOperation("//*[@id=\"confirmOrderButton-id\"]");
			Thread.sleep(7000);
		}

		if(uiDriver.toGetPageTitle().equals("TalkTalk Order - Summary")){
			uiDriver.clickOperation("//*[@id=\"protractor-tcs\"]/span");
			uiDriver.clickOperation("//*[@id=\"confirmOrderButton-id\"]");
			Thread.sleep(7000);
		}
		if(uiDriver.toGetPageTitle().equals("TalkTalk Customise - HomeSafe")){

			uiDriver.clickOperation("//*[@id=\"confirmOrderButton-id\"]");
			Thread.sleep(7000);
		}
		if(uiDriver.toGetPageTitle().equals("TalkTalk Order - Confirm")){
			//takeScreenShots(uiDriver.toGetPageTitle());
			String Orderref = uiDriver.toGetText("//*[@id=\"left-col-sticky-basket-spacer\"]/div/confirm/h2[1]");
			System.out.println(Orderref);
			String[] SplitOrderRef = Orderref.split(":");
			System.out.println(SplitOrderRef[0]);
			System.out.println(SplitOrderRef[1].trim());
			Order_Reference=SplitOrderRef[1].trim();
		}

		if(!Order_Reference.equals("")){
			ExcelReporter.toCreatePlaceOrdersReferenceNumber(OrderName,Order_Reference,Environment);
			if(Environment.equals("VIL")){
				System.out.println("Environment VIL ----------- " +Environment );
				AgentPortal_AllOrdersDriver.toCreateTestResults(ordref,Environment); 
			}			
			else if(Environment.equals("EVE")){
				AgentPortal_AllOrdersDriver.toCreateTestResults(ordref,Environment); 
				System.out.println("Environment EVE ----------- " +Environment );
				System.out.println("OrderName ----------- " +OrderName );
				if(OrderName.trim().equals("Fast Broadband") && Counter==1)
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
					//takeScreenShots(uiDriver.toGetPageTitle());
					Process p =  Runtime.getRuntime().exec(command); 
					Thread.sleep(180000);
				}
			}
		}
		else
		{
			//takeScreenShots(uiDriver.toGetPageTitle());
			log("Order Completed", ResultType.ERROR, "Order should be Completed successfully", "Order not Completed successfully", true);
		}

	}

	public  void takeScreenShots(String name) throws Exception
	{
		boolean makeDir = false;
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy") ;
		String curDate =dateFormat.format(date);
		File Path = new File(System.getProperty("user.dir")+ "//Screenshots//"+curDate);
		makeDir = Path.mkdir();
		System.out.println(makeDir);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy,HH-mm");
		String formattedDate = sdf.format(date);
		formattedDate.toString();
		// edited for the taking screen shot in remote webdriver
		//Augmenter augmenter = new Augmenter(); 
		File scr = ((TakesScreenshot)uiDriver.webDr).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scr, new File(Path+"//"+ name +"_"+ formattedDate +".jpeg"));
	}
}
