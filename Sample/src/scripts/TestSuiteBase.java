package scripts;

import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import common.CommonMethods;
import common.Setup;
import common.WebPage;
import features.TestUtil;




@SuppressWarnings("unused")
public class TestSuiteBase {
  
	

	@BeforeSuite
	public void checkSuiteSkip() throws Exception{
		
		Setup.initialize();
		System.out.println("Setup is Initialized");
		Setup.APP_LOGS.info("Checking Runmode of Suite A");
		if(!TestUtil.isSuiteRunnable(Setup.Suitexls, "scripts")){
		
			System.out.println("the runmode of suite A is " + TestUtil.isSuiteRunnable(Setup.Suitexls, "Suite A"));
			TestUtil.reportDataSetResult(Setup.Suitexls, "Test Suite", 2, "Skipped");
			Setup.APP_LOGS.info("Skipped Suite A as the runmode was set to NO");
			Setup.pdf.table("Suite A", "All Test Cases","Skip");
			Setup.pdf.pdfList();
			//Setup.pdf.addpdf();
			//Setup.recorder.stop();
			throw new SkipException("Runmode of Suite A set to no. So Skipping all tests in Suite A");
			
		}
		else
		{
			Setup.APP_LOGS.info("Executing Suite A as the runmode was set to Yes");
		}
		
	}
	
	/*@AfterSuite
	@Parameters({ "BrowserName" })
	public void closeSuite(String browserName) throws Exception
		{
		CommonMethods com= new CommonMethods();
		Setup.pdf.pdfList();
		Setup.pdf.addpdf(browserName);
		String Currentdate= com.currentDate();
		String DateTime = com.currentDateTime();
		Setup.pdf.rename(System.getProperty("user.dir")+ "//reports//"+Currentdate+"//"+"Report_"+browserName+"_"+DateTime+".pdf");
		Setup.recorder.stop();
	
	}*/

	
}
