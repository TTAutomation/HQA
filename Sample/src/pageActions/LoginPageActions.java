package pageActions;

import common.Setup;
import common.WebPage;
import features.TestUtil;

public class LoginPageActions {

	String MPF_App_url;
	String MPF_PostCode;
	String MPF_AgentID;
	String MPF_BTN_CheckAvailability;
	
	public String ToGetMPFXLSCellData(String Methodname, String ColumnName,int RowNum)
	{
		String Data = Setup.MPFOrderxls.getCellData("TestData", ColumnName, RowNum);
		System.out.println(Data);
		return  Data;
		
	}
	
	public void ToNavigate_MPF_AppUrl() throws Exception
	{
		MPF_App_url = Setup.LoginPage.getProperty("MPF_App_url");
		WebPage.openURL(MPF_App_url);
		Setup.APP_LOGS.info("Application Navigated to the URL - " + MPF_App_url );	
		Setup.APP_LOGS.info("Navigated Page title - " + WebPage.getTitle());
	}	
	
	public void ToSendPostCode(String TestCaseID,int RowNum)
	{
		String PostCode = ToGetMPFXLSCellData(TestCaseID,"PostCode",RowNum);
		MPF_PostCode = Setup.LoginPage.getProperty("MPF_PostCode");
		WebPage.sendKeys(MPF_PostCode, "id", PostCode);
		WebPage.wait(1000);
		Setup.APP_LOGS.info("Entered PostCode - " + PostCode);
	}
	
	public void ToSendAgentID(String TestCaseID,int RowNum)
	{
		String AgentID = ToGetMPFXLSCellData(TestCaseID,"AgentID",RowNum);
		MPF_AgentID = Setup.LoginPage.getProperty("MPF_AgentID");
		WebPage.sendKeys(MPF_AgentID, "id", AgentID);
		WebPage.wait(1000);
		Setup.APP_LOGS.info("Entered AgentID - " + AgentID);
	}	
	
	public void ToClickButtonCheckavailability() throws Exception
	{
		MPF_BTN_CheckAvailability = Setup.LoginPage.getProperty("MPF_BTN_CheckAvailability");
		WebPage.click(MPF_BTN_CheckAvailability, "X");
		Setup.APP_LOGS.info("Clicked the button - CheckAvailability ");
	}
	
	public void toLoginIntoTheMPFApplication(String TestCaseID,int RowNum) throws Exception{
		ToSendPostCode(TestCaseID,RowNum);
		ToSendAgentID(TestCaseID,RowNum);
		ToClickButtonCheckavailability();
	}
	
	
	
}
