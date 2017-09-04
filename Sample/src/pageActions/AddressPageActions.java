package pageActions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import common.Setup;
import common.WebPage;
import features.TestUtil;

public class AddressPageActions {
	
	String Address;
	String Chk_Morethan30days;
	String Chk_last30days;
	String btn_Next;
	String btn_Back;
	String msg_error;
	String msg_errorMsg;
	
	
	public String ToGetMPFXLSCellData(String Methodname, String ColumnName,int RowNum)
	{
		//int rownum=TestUtil.getFirstDataRowNumber(Setup.MPFOrderxls,"TestData",Methodname);
		String Data = Setup.MPFOrderxls.getCellData("TestData", ColumnName, RowNum);
		return  Data;
	}
		
	public void ToSelectAddress(String TestCaseID,int RowNum)
	{
		String Addressxls = ToGetMPFXLSCellData(TestCaseID,"Address",RowNum);
		Address = Setup.AddressPage.getProperty("list_Address");
		WebElement mySelectElement = WebPage.d.findElement(By.id("bt-address"));
		Select dd= new Select(mySelectElement);
		dd.selectByVisibleText(Addressxls);	
		Setup.APP_LOGS.info("Selected Address - " + Addressxls);
		//"5 North Road, Edgware, HA8 0AG"
	}
	
	public void toCheckMorethan30daysChkBox()
	{
		Chk_Morethan30days = Setup.AddressPage.getProperty("Chk_Morethan30days");
		WebPage.check(Chk_Morethan30days, "id");
	}
	
	
	public void toChecklast30daysChkBox()
	{
		Chk_last30days = Setup.AddressPage.getProperty("Chk_last30days");
		WebPage.check(Chk_last30days, "id");
	}
	
	public void toClickButtonBack() throws Exception
	{
		btn_Back = Setup.AddressPage.getProperty("btn_Back");
		WebPage.clickAndWait(btn_Back, "X");
	}
	
	public void toClickButtonNext() throws Exception
	{
		btn_Next = Setup.AddressPage.getProperty("btn_Next");
		WebPage.clickAndWait(btn_Next, "X");
	}
	
	public boolean toVerifyErrorMsgIsGettingDisplayedOrNot() throws Exception
	{
		boolean isDisplay = false;
		msg_error = Setup.AddressPage.getProperty("msg_error");
		msg_errorMsg = Setup.AddressPage.getProperty("msg_errorMsg");
		//System.out.println(msg_error + "---------------" + msg_errorMsg);
		isDisplay = WebPage.isVisible(msg_error, "X");
				//WebPage.getWebElement(msg_error, "X").isDisplayed(); 
				//WebPage.isVisible(msg_error, "X");
		//System.out.println("isDisplay -- " + isDisplay);
		Setup.APP_LOGS.info("Error Message is displayed -- " + isDisplay);
		if(isDisplay==true)
		{
			Setup.APP_LOGS.info("Error Message is getting displayed upon clicking on next button by selecting the address - " + WebPage.getText(msg_errorMsg, "X"));
			throw new Exception("Error Message is getting displayed upon clicking on next button by selecting the address - " + WebPage.getText(msg_errorMsg, "X"));
		}
		return isDisplay;
	}
	
	
	public String toGetTheErrorMsg() throws Exception
	{
		String Display = null ;
		if(toVerifyErrorMsgIsGettingDisplayedOrNot()==true)
		{
		Display = WebPage.getText(msg_errorMsg, "X");
		}
		return Display;
	}

}
