package features;

import java.util.ArrayList;
import java.util.List;

import xlsx.Xls_Reader;



public class TestUtil {


	// finds if the test suite is runnable 
	public static boolean isSuiteRunnable(Xls_Reader xls , String suiteName){
		boolean isExecutable=false;
		for(int i=2; i <= xls.getRowCount("Test Suite") ;i++ ){
			//String suite = xls.getCellData("Test Suite", "TSID", i);
			//String runmode = xls.getCellData("Test Suite", "Runmode", i);

			if(xls.getCellData("Test Suite", "TSID", i).equalsIgnoreCase(suiteName)){
				if(xls.getCellData("Test Suite", "Runmode", i).equalsIgnoreCase("Y")){
					isExecutable=true;
				}else{
					isExecutable=false;
				}
			}

		}
		xls=null; // release memory
		return isExecutable;

	}


	// returns true if runmode of the test is equal to Y
	public static boolean isTestCaseRunnable(Xls_Reader xls, String testCaseName){
		boolean isExecutable=false;
		for(int i=2; i<= xls.getRowCount("Test Cases") ; i++){
			//String tcid=xls.getCellData("Test Cases", "TCID", i);
			//String runmode=xls.getCellData("Test Cases", "Runmode", i);
			//System.out.println(tcid +" -- "+ runmode);


			if(xls.getCellData("Test Cases", "TCID", i).equalsIgnoreCase(testCaseName))
			{
				if(xls.getCellData("Test Cases", "Runmode", i).equalsIgnoreCase("Y"))
				{
					isExecutable= true;
				}

				else
				{
					isExecutable= false;
				}
			}
		}

		return isExecutable;

	}


	// return the test data from a test in a 2 dim array
	public static Object[][] getData(Xls_Reader xls , String testCaseName){
		// if the sheet is not present
		if(! xls.isSheetExist(testCaseName)){
			xls=null;
			return new Object[1][0];
		}


		int rows=xls.getRowCount(testCaseName);
		int cols=xls.getColumnCount(testCaseName);
		//System.out.println("Rows are -- "+ rows);
		//System.out.println("Cols are -- "+ cols);

		Object[][] data =new Object[rows-1][cols-3];
		for(int rowNum=2;rowNum<=rows;rowNum++){
			for(int colNum=0;colNum<cols-3;colNum++){
				//System.out.print(xls.getCellData(testCaseName, colNum, rowNum) + " -- ");
				data[rowNum-2][colNum] = xls.getCellData(testCaseName, colNum, rowNum);
			}
			//System.out.println();
		}
		return data;

	}



	// checks Runmode for dataSet
	public static String[] getDataSetRunmodes(Xls_Reader xlsFile,String sheetName){
		String[] runmodes=null;
		if(!xlsFile.isSheetExist(sheetName)){
			xlsFile=null;
			sheetName=null;
			runmodes = new String[1];
			runmodes[0]="Y";
			xlsFile=null;
			sheetName=null;
			return runmodes;
		}
		runmodes = new String[xlsFile.getRowCount(sheetName)-1];
		for(int i=2;i<=runmodes.length+1;i++){
			runmodes[i-2]=xlsFile.getCellData(sheetName, "Runmode", i);
		}
		xlsFile=null;
		sheetName=null;
		return runmodes;

	}

	// update results for a particular data set	
	public static void reportDataSetResultFirefox(Xls_Reader xls, String testCaseName, int rowNum,String result){	
		xls.setCellData(testCaseName, "FireFox-Results", rowNum, result);
	}


	public static void reportDataSetResultChrome(Xls_Reader xls, String testCaseName, int rowNum,String result){	
		xls.setCellData(testCaseName, "Chrome-Results", rowNum, result);
	}

	public static void reportDataSetResultIE(Xls_Reader xls, String testCaseName, int rowNum,String result){	
		xls.setCellData(testCaseName, "IE-Results", rowNum, result);
	}


	public static void reportDataSetResultSafari(Xls_Reader xls, String testCaseName, int rowNum,String result){	
		xls.setCellData(testCaseName, "Safari-Results", rowNum, result);
	}

	public static void reportDataSetResult(Xls_Reader xls, String testCaseName, int rowNum,String result){	
		xls.setCellData(testCaseName, "Results", rowNum, result);
	}



	// return the row num for a test
	public static int getRowNum(Xls_Reader xls, String id){
		for(int i=2; i<= xls.getRowCount("Test Cases") ; i++){
			String tcid=xls.getCellData("Test Cases", "TCID", i);

			if(tcid.equals(id)){
				xls=null;
				return i;
			}


		}

		return -1;
	}
	
	public static int getRowNumber(Xls_Reader xls,String SheetName, String Colname){
		for(int i=2; i<= xls.getRowCount(SheetName) ; i++){
			String tcid=xls.getCellData(SheetName, Colname, i);
			//System.out.println(tcid+ "Run Mode Value");
			if(tcid.equals("Y")){
				xls=null;
				return i;
			}
		}

		return -1;
	}
	public static  List<Integer> getRowNumbers(Xls_Reader xls,String SheetName, String Colname){
		List<Integer> A = new ArrayList<>();
		for(int i=2; i<= xls.getRowCount(SheetName) ; i++){
			String tcid=xls.getCellData(SheetName, Colname, i);
			//System.out.println(tcid+ "Run Mode Value");
			if(tcid.equals("Y")){
				A.add(i);
			}
		}
		return A;
	}

	public static List<Integer> getRowNumbers(Xls_Reader xls,String SheetName,String methodName, String Colname){
		List<Integer> A = new ArrayList<>();

		for(int i=2; i<= xls.getRowCount(SheetName) ; i++){
			String tcid=xls.getCellData(SheetName, Colname, i);
			//System.out.println(tcid+ "Run Mode Value");
			if(tcid.equals(methodName)){
				A.add(i);
			}
		}
		return A;
	}
	public static int getFirstDataRowNum(Xls_Reader xls, String SheetName,String methodName,String Colname ){
		System.out.println(SheetName+"SheetName");
		System.out.println(methodName+"methodName");
		System.out.println(Colname+"Colname");
		for(int i=2; i<= xls.getRowCount(SheetName) ; i++){
			String tcid=xls.getCellData(SheetName, Colname, i);

			if(tcid.equals(methodName)){
				xls=null;
				return i;
			}


		}

		return -1;
	}


	public static int getFirstDataRowNum(Xls_Reader xls, String SheetName,String id){
		for(int i=2; i<= xls.getRowCount(SheetName) ; i++){
			String tcid=xls.getCellData(SheetName, "TCID", i);

			if(tcid.equals(id)){
				xls=null;
				return i;
			}


		}

		return -1;
	}


	public static int getLastDataRowNum(Xls_Reader xls,String SheetName, String id, int row){

		for(int i=row; i<= xls.getRowCount(SheetName) ; i++){
			String tcid=xls.getCellData(SheetName, "TCID", i);
			
		if(i==xls.getRowCount(SheetName)){
				if(tcid.contentEquals(id))
				{
					return i;
				}
				
			}
		if(!tcid.contentEquals(id))
			{
				return i-1;
			}


		}

		return -1;
	}
	public static void writeDataAtExcelColumn(Xls_Reader xls, String sheetName, String ColumnName, int rowNum,String result){
		//System.out.println(sheetName+ColumnName+rowNum+result);
		xls.setCellData(sheetName,ColumnName,rowNum,result);
	}



	public static int getFirstDataRowNumber(Xls_Reader xls, String SheetName,String methodName){
	/*	System.out.println(SheetName+"SheetName");
		System.out.println(methodName+"methodName");
	*/		for(int i=2; i<= xls.getRowCount(SheetName) ; i++){
			String tcid=xls.getCellData(SheetName,"TCID", i);

			if(tcid.equals(methodName)){
				xls=null;
				return i;
			}


		}

		return -1;
	}







}
