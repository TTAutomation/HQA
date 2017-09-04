package common;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import features.TestUtil;

import xlsx.Xls_Reader;



public class CommonMethods {

	ArrayList<String> array1;
	ArrayList<String> array2;
	List<String> List1;
	List<String> List2;

	String SysDate;


	/**
	 * Used for Reporting Pass/Fail of the Executed Test Case 
	 * in Suite A Excel Sheet.
	 * 
	 * 
	 * @param InvokedBrowser
	 * @param methodname
	 * @param isTestPass
	 */
	public void reportTestResult(Xls_Reader ExcelObjectName, String InvokedBrowser,String methodname ,boolean isTestPass)
	{
		if(InvokedBrowser.contentEquals("Firefox"))
		{
			if(isTestPass==true)
			{
				TestUtil.reportDataSetResultFirefox(ExcelObjectName, "Test Cases", TestUtil.getRowNum(ExcelObjectName, methodname), "PASS");
				Setup.APP_LOGS.info(methodname+" Passed");
			}

			else
			{
				TestUtil.reportDataSetResultFirefox(ExcelObjectName, "Test Cases", TestUtil.getRowNum(ExcelObjectName, methodname), "FAIL");
				Setup.APP_LOGS.info(methodname+" Failed");
			}

		}

		else if (InvokedBrowser.contentEquals("Chrome"))
		{
			if(isTestPass==true){
				TestUtil.reportDataSetResultChrome(ExcelObjectName, "Test Cases", TestUtil.getRowNum(ExcelObjectName, methodname), "PASS");
				Setup.APP_LOGS.info(methodname+" Passed");
			}

			else
			{
				TestUtil.reportDataSetResultChrome(ExcelObjectName, "Test Cases", TestUtil.getRowNum(ExcelObjectName, methodname), "FAIL");
				Setup.APP_LOGS.info(methodname+" Failed");
			}
		}

		else if (InvokedBrowser.contentEquals("IE"))
		{
			if(isTestPass==true){
				TestUtil.reportDataSetResultIE(ExcelObjectName, "Test Cases", TestUtil.getRowNum(ExcelObjectName, methodname), "PASS");
				Setup.APP_LOGS.info(methodname+" Passed");
			}

			else
			{
				TestUtil.reportDataSetResultIE(ExcelObjectName, "Test Cases", TestUtil.getRowNum(ExcelObjectName, methodname), "FAIL");
				Setup.APP_LOGS.info(methodname+" Failed");
			}
		}

		else if (InvokedBrowser.contentEquals("Safari"))
		{
			if(isTestPass==true){
				TestUtil.reportDataSetResultSafari(ExcelObjectName, "Test Cases", TestUtil.getRowNum(ExcelObjectName, methodname), "PASS");
				Setup.APP_LOGS.info(methodname+" Passed");
			}

			else
			{
				TestUtil.reportDataSetResultSafari(ExcelObjectName, "Test Cases", TestUtil.getRowNum(ExcelObjectName, methodname), "FAIL");
				Setup.APP_LOGS.info(methodname+" Failed");
			}
		}

	}


	/**
	 * Used to Created the Data wise folder in ScreenShot Package and
	 * take screen shorts with user selected name
	 * along with date-time time stamp as image name.  
	 * 
	 * @param name -- Name of the Screen shot to be saved excluding the date- time format.
	 * @throws Exception
	 */

	@SuppressWarnings("unused")
	public void takeScreenShots(String name) throws Exception
	{
		boolean makeDir = false;
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy") ;
		String curDate =dateFormat.format(date);
		File Path = new File(System.getProperty("user.dir")+ "//screenshots//"+curDate);
		makeDir = Path.mkdir();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy,HH-mm");
		String formattedDate = sdf.format(date);
		formattedDate.toString();
		// edited for the taking screen shot in remote webdriver
		//Augmenter augmenter = new Augmenter(); 
		File scr = ((TakesScreenshot)WebPage.getWD()).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scr, new File(Path+"//"+ name +"_"+ formattedDate +".jpeg"));
	}


	
	public void takeAlertScreenShot(String name) throws Exception
	{
		boolean makeDir = false;
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy") ;
		String curDate =dateFormat.format(date);
		File Path = new File(System.getProperty("user.dir")+ "//screenshots//"+curDate);
		makeDir = Path.mkdir();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy,HH-mm");
		String formattedDate = sdf.format(date);
		formattedDate.toString();
		// edited for the taking screen shot in remote webdriver
		//Augmenter augmenter = new Augmenter(); 
		BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIO.write(image, "jpeg", new File(Path+"//"+ name +"_"+ formattedDate +".jpeg"));	
	}


	/**
	 * to get the reference data from the Properties File based
	 * on "," separator.
	 * @param RefernceString
	 * @return
	 */
	public ArrayList<String> getReferenceRecords(String RefernceString)
	{
		ArrayList<String> values = new ArrayList<String>();
		int charCount = 0;
		for(int i =0 ; i<RefernceString.length(); i++)
		{
			if(RefernceString.charAt(i) == ','){
				charCount++;
			}          
		}
		String tbheaders[] = RefernceString.split(","); 
		for(int j=0;j<=charCount;j++)
		{
			values.add(tbheaders[j]);
		}

		Setup.APP_LOGS.info("The Ref Values are " + values);
		return values;

	}


	/**
	 * to get the present date in "MM-dd-yyyy" format.
	 * @return
	 */

	public String currentDate()
	{
		String curDate=null;
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy") ;
		curDate =dateFormat.format(date);
		return curDate;
	}
	
	public String currentDateTime()
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy,HH-mm");
		String formattedDate = sdf.format(date);
		formattedDate.toString();
		return formattedDate;
	}

	
	
	

		

	

	

	
	


	




	
	


	

	/**
	 * to get the unmatched array list
	 * @param array1
	 * @param array2
	 * @return
	 */

	public ArrayList<String> getUmatchedInArrayComparision(ArrayList<String> array1, ArrayList<String> array2) throws Exception
	{
		ArrayList<String> UnmatchedArray = new ArrayList<String>();
		if(array1!=null && array2!=null &&array1.size()>0&&array2.size()>0 && array1.size()==array2.size()){
			for(int i=0;i<array1.size();i++)
			{
				if(!((array1.get(i)).contentEquals(array2.get(i))))
					UnmatchedArray.add(array1.get(i));	
			}
		}
		else{
			throw new Exception("Arrya Sizes are not matched");
		}
		return UnmatchedArray;			
	}

	public boolean isArrayHavingSameData(ArrayList<String> array1)
	{
		ArrayList<String> UnmatchedArray = new ArrayList<String>();
		for(int i=0;i<array1.size();i++)
		{
			if(!((array1.get(0)).contentEquals(array1.get(i))))
				UnmatchedArray.add(array1.get(i));	
		}
		if(UnmatchedArray.size()==0)
		{
			return true;
		}
		else
			return false;
	}

	
	
	public List<String> getUmatchedInBothArrayComparision(List<String> array1, List<String> array2) throws Exception
	{

		array1.removeAll(Arrays.asList("", null));
		array2.removeAll(Arrays.asList("", null));
		System.out.println("After Removing the nulls and spaces"+array1);
		System.out.println("After Removing the nulls and spaces"+array2);
		List1 = new ArrayList<String>();
		List2 = new ArrayList<String>();
		List<String> UnmatchedArray = new ArrayList<String>();
		List1.addAll(array1);
		List2.addAll(array2);
		for(Iterator<String> itr = List1.iterator(); itr.hasNext();)
		{
			String elm = itr.next();		    
			if(List2.remove(elm))
			{
				itr.remove();	
			}	
		}

		UnmatchedArray.addAll(List1);
		UnmatchedArray.addAll(List2);
		WebPage.SetMesage("Unmatched Data in Both Array Comparision are - " + UnmatchedArray);
		return  UnmatchedArray;
	}


	

	
	





	






	
	public ArrayList<String> getOptionsListFromDDL(String Property, String PropertyType){
		ArrayList<String> OptionList = new ArrayList<>();
		List<WebElement> we=WebPage.getWebElementList(Property,PropertyType);
		for(WebElement option:we){
			OptionList.add(option.getText().trim());
		}
		OptionList.removeAll(Arrays.asList("", null));/**To remove null values from the drop down*/
		return OptionList;
	}

	public boolean acceptAlert(String a) throws Exception{

		if(WebPage.isAlertPresent()){
			Setup.APP_LOGS.info(a+" "+"Alert is displayed");
			WebPage.chooseOkOnNextConfirmation();
			return true;
		}
		return false;
	}
	public boolean dismissAlert(String a) throws Exception{

		if(WebPage.isAlertPresent()){
			Setup.APP_LOGS.info(a+" "+"Alert is displayed");
			WebPage.chooseNoOnNextConfirmation();
			WebPage.delay(2);
			return true;
		}
		return false;
	}
	public String getCurrentDate(){

		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, -2);
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy',' hh:mma" );
		String Formatdate=date.format(now.getTime());
		return Formatdate;
	}
	
	public void WriteDatainExcel(Xls_Reader ExcelObjectName, String SheetName,String methodname,String TDColumnName, String TestData ) throws Exception
	{			int rownum=TestUtil.getFirstDataRowNumber(ExcelObjectName,SheetName,methodname);
	if(rownum!=-1){
		TestUtil.writeDataAtExcelColumn(ExcelObjectName,SheetName,TDColumnName,rownum,TestData);
		Setup.APP_LOGS.info(methodname+TestData);
	}
	else{
		throw new Exception("Row number of excel is not getting");
	}
	}

	public String toWriteDatainExcelAndReturnData(Xls_Reader ExcelObjectName, String SheetName,String methodname,String TDColumnName, String TestData ) throws Exception
	{			int rownum=TestUtil.getFirstDataRowNumber(ExcelObjectName,SheetName,methodname);
	if(rownum!=-1){
		TestUtil.writeDataAtExcelColumn(ExcelObjectName,SheetName,TDColumnName,rownum,TestData);
		Setup.APP_LOGS.info(methodname+TestData);
	}
	else{
		throw new Exception("Row number of excel is not getting");
	}
	return TestData;
	}

	public ArrayList<String> createArrayObject(){

		return  new ArrayList<String>();

	}

	
	public ArrayList<String> SplitString(String splitval,String strinval) throws Exception{
		ArrayList<String >ActualReferences =createArrayObject();
		String reference[]= strinval.split(",");
		for(int i=0; i<reference.length;i++){
			//WebPage.SetMesage("Spppppppppppppppp........."+strarray[i]);
			ActualReferences.add(reference[i].trim());
		}
		System.out.println(ActualReferences+".............");
		return ActualReferences;
	}


	public ArrayList<String> isFullArrayMatchWithData(ArrayList<String> Array,String Data)
	{
		ArrayList<String> data = new ArrayList<String>();
		for(int i=0;i<Array.size();i++)
		{
			if(!Array.get(i).contentEquals(Data))
			{
				Setup.APP_LOGS.info("The Missmatch occured at record no " + i + " and displayed record is " + Array.get(i));
				data.add(Array.get(i));
			}
		}
		return data;
	}
	



	

}