package scripts;



import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import pageActions.AddressPageActions;
import pageActions.LoginPageActions;
import common.CommonMethods;
import common.Setup;
import common.WebPage;
import features.TestUtil;

public class MPF_Order extends TestSuiteBase{


	String InvokedBrowser="";
	String Methodname="";

	public static CommonMethods com;
	public static LoginPageActions LPA;
	public static AddressPageActions APA;

	@BeforeClass
	@Parameters("BrowserName")
	public void openBrowser(String browserName) throws Exception
	{
		Setup.APP_LOGS=Logger.getLogger(Setup.class.getName());
		System.out.println("Opened Browser is "+browserName);
		InvokedBrowser=browserName;
		WebPage.createWebDriverInstance(browserName);
		com = new CommonMethods();
		LPA = new LoginPageActions();
		APA = new AddressPageActions();
		WebPage.maximize();
	}

	@Test
	public void TC_MPFOrder() throws Exception
	{
		Methodname = new Exception().getStackTrace()[0].getMethodName();
		try
		{
			int firstrow = TestUtil.getFirstDataRowNum(Setup.MPFOrderxls, "TestData", Methodname);
			int Lastrow = TestUtil.getLastDataRowNum(Setup.MPFOrderxls, "TestData", Methodname, firstrow);
			System.out.println(firstrow + " ---- " + Lastrow );
			for(int i =firstrow;i<= Lastrow;i++){
				LPA.ToNavigate_MPF_AppUrl();
				LPA.toLoginIntoTheMPFApplication(Methodname, i);				
				APA.ToSelectAddress(Methodname,i);
				APA.toCheckMorethan30daysChkBox();
				APA.toClickButtonNext();
				Thread.sleep(2000);

				if(APA.toVerifyErrorMsgIsGettingDisplayedOrNot()==false)
				{
					WebPage.tryBlockData(Methodname, InvokedBrowser, Setup.MPFOrderxls, this.getClass().getSimpleName());
				}
			}




			
						
		}
		catch(Exception e)
		{
			WebPage.catchBlockData(Methodname, InvokedBrowser, e, Setup.MPFOrderxls, this.getClass().getSimpleName());
		}

	}



	/*
	 */
}
