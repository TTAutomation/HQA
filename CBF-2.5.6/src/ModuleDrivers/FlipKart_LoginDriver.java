package ModuleDrivers;

import static cbf.engine.TestResultLogger.*;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import cbf.engine.TestResult.ResultType;
import cbf.utils.DataRow;
import cbfx.BaseWebDriver.BaseWebModuleDriver;


public class FlipKart_LoginDriver extends BaseWebModuleDriver{
	
	public void LaunchApp(DataRow input, DataRow output) throws IOException
	{
		uiDriver.launchApplication(input.get("url"));
		if(uiDriver.checkPage(input.get("pageName")))
		{			
			passed("Launching FlipKart App", "FlipKart App should be launched successfully", "FlipKart App launched successfully");
		}
		else
		{
			log("Launching FlipKart App", ResultType.FAILED, "FlipKart App should be launched successfully", "FlipKart App not launched successfully", true);
		}
	}
	
	
	
	public void Login(DataRow input, DataRow output) throws IOException, InterruptedException
	{
		
		
	uiDriver.setValue("flipsearchtext", input.get("Search"));
		
	uiDriver.click("flipbutton");
		
		
		
	}
	
	

}
