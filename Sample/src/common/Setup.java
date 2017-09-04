package common;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import features.CreatePDF;
import xlsx.Xls_Reader;
//import atu.testrecorder.ATUTestRecorder;



public class Setup 
{

	public static CreatePDF pdf;
	public static Logger APP_LOGS=null;
	public static Properties LoginPage=null;
	public static Properties AddressPage=null;	
	public static Xls_Reader MPFOrderxls=null;
	public static Xls_Reader Suitexls=null;
	
	//public static Xls_Reader endtoendsuitexls=null;
	//public static ATUTestRecorder recorder;
	public static  boolean isInitialized=false;
	


	/**
	 * it is the default/ first to executed.
	 * It initialize all the pre-configured files like
	 * 
	 * 1. Creates PDF Report Object for the CreatePDF Class in the poc.features 
	 * 	  Object initialize --- Where PDF should be created with name for each suite executed.
	 * 	  and used all over the class to generated pdf report
	 * 
	 * 2. Creates Object for APP_Logs -- Used to report the logs
	 * 	  in user defined way.
	 * 
	 * 3. Creates Object to the Recorder -- Used to record the executed script suite wise manner. 
	 * 
	 * 4. Creates Object to all the Excel Class Used in the project.
	 * 
	 * @throws Exception
	 */

	public static void initialize() throws Exception
	{
		// OBJECT DECLARATION OF PDF CREATION
		@SuppressWarnings("unused")
		boolean makeDir = false;
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy") ;
		String curDate =dateFormat.format(date);
		File Path = new File(System.getProperty("user.dir")+ "//reports//"+curDate);
		makeDir = Path.mkdir();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy,HH-mm");
		String formattedDate = sdf.format(date);
		formattedDate.toString();
		pdf = new CreatePDF(Path+"//"+ "Report" +"-" + formattedDate + ".pdf" );


		//To Record the running script

		//recorder = new ATUTestRecorder("Automationbatch - "+formattedDate,false);
		//recorder.start();	



		if(!isInitialized)
		{

			try
			{

				// DECLARATION OF LOG4J FOR LOGGING.
				APP_LOGS=Logger.getLogger(Setup.class.getName());
				DOMConfigurator.configure("log4j.xml");

				
				// TO LOAD  LOGIN PROPERTIES FILE. 
				FileInputStream gc1 = new FileInputStream(System.getProperty("user.dir")+"//src//configurations//Login.properties");
				LoginPage= new Properties();
				LoginPage.load(gc1);
				APP_LOGS.info("--Config file is picked from the given path: Login.properties");
				
				// To load AddressPage Properties
				
				FileInputStream gc2 = new FileInputStream(System.getProperty("user.dir")+"//src//configurations//Address.properties");
				AddressPage= new Properties();
				AddressPage.load(gc2);
				APP_LOGS.info("--Config file is picked from the given path: Address.properties");
			

				
				
								



				// TO INITIALIZE EXCEL FILES. 

				APP_LOGS.info("Initializing Excel Files");
				MPFOrderxls = new Xls_Reader(System.getProperty("user.dir")+"//src//Xlsx//XlsMPFOrder.xlsx");
				APP_LOGS.info("XlsMPFOrder.xlsx file is Initialized");

				Suitexls = new Xls_Reader(System.getProperty("user.dir")+"//src//xlsx//Suite.xlsx");
				APP_LOGS.info("Suite.xlsx file is Initialized");
				
				APP_LOGS.info(" All Excel Files Initialized successfully ");				
				isInitialized=true;
			}

			catch(Exception ex)
			{
				System.out.println("File not found in  ");
				ex.printStackTrace();
				APP_LOGS.warn("--No file found in the given path");
				pdf.table("Setup", "setup", "Fail");
			}


		}




	}


}
