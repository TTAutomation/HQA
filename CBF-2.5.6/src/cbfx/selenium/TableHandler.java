/******************************************************************************
$Id : TableHandler.java 3/3/2016 6:01:28 PM
Copyright 2016-2017 IGATE GROUP OF COMPANIES. All rights reserved
(Subject to Limited Distribution and Restricted Disclosure Only.)
THIS SOURCE FILE MAY CONTAIN INFORMATION WHICH IS THE PROPRIETARY
INFORMATION OF IGATE GROUP OF COMPANIES AND IS INTENDED FOR USE
ONLY BY THE ENTITY WHO IS ENTITLED TO AND MAY CONTAIN
INFORMATION THAT IS PRIVILEGED, CONFIDENTIAL, OR EXEMPT FROM
DISCLOSURE UNDER APPLICABLE LAW.
YOUR ACCESS TO THIS SOURCE FILE IS GOVERNED BY THE TERMS AND
CONDITIONS OF AN AGREEMENT BETWEEN YOU AND IGATE GROUP OF COMPANIES.
The USE, DISCLOSURE REPRODUCTION OR TRANSFER OF THIS PROGRAM IS
RESTRICTED AS SET FORTH THEREIN.
******************************************************************************/

package cbfx.selenium;

import static cbf.engine.TestResultLogger.*;

import java.util.List;

import org.apache.poi.util.SystemOutLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import cbf.utils.LogUtils;
import cbf.utils.StringUtils;

public class TableHandler {


	/**
	 * For getting WebElement of an element
	 * 
	 * @param elementName
	 *            Element name for table locator - uiMap
	 * @return WebElement - WebElement for element Name
	 */

	public TableHandler(WebUIDriver driver) {
		this.driver=driver;
	}

	private WebElement getWebElement(String elementName) {
		WebElement tableElement = null;
		try {
			
			tableElement = driver.getControl(elementName);
			
		} catch (Exception e) {
			logger.handleError("Failed to get WebElement ", elementName);
		}
		return tableElement;
	}

	/**
	 * For getting total number of columns in table
	 * 
	 * @param elementName
	 *            Element name for table locator - uiMap
	 * @return int - Number of columns
	 */

	public int getColumnCount(String elementName) {
		int col_count = 0;

		try {
			col_count = getWebElement(elementName).findElements(
					By.tagName("td")).size();
		} catch (Exception e) {
			logger.handleError("Failed to get row count for ", elementName);
		}
		return col_count;
	}

	/**
	 * For getting total number of rows in table
	 * 
	 * @param elementName
	 *            Element name for table locator - uiMap
	 * @return int - Number of rows
	 */
	public int getRowCount(String elementName) {
		int row_count = 0;
		try {
			row_count = getWebElement(elementName).findElements(
					By.tagName("tr")).size();

		} catch (Exception e) {
			logger.handleError("Failed to get row count for ", elementName);
		}
		return row_count;
	}

	/**
	 * For getting total number of columns for particular row in table
	 * 
	 * @param elementName
	 *            Element name of table
	 * @return int - Number of columns
	 */

	public int getColumnCountOfRow(String elementName) {
		WebElement tableElement = getWebElement(elementName);

		List<WebElement> td_collection = tableElement.findElement(
				By.tagName("tr")).findElements(By.tagName("td"));
		try {
			if (td_collection.size() == 0) {
				td_collection = tableElement.findElement(By.tagName("tr"))
						.findElements(By.tagName("th"));
			}
		} catch (Exception e) {
			logger.handleError("Failed to get column count for ", tableElement);
		}
		return td_collection.size();
	}

	/**
	 * For getting an object of the table - name matches with given text
	 * 
	 * @param textTosearch
	 *            Name of the table you want to search
	 * @return WebElement - Object of table
	 */
	public WebElement getTableObjectByText(String textTosearch) {
		List<WebElement> tablecollection = webDr.findElements(By
				.tagName("table"));
		int FinalIndex = 0;
		try {
			for (int i = 0; i < tablecollection.size(); i++) {
				if (tablecollection.get(i).getText().contains(textTosearch)) {
					FinalIndex = i;
					break;
				}
				if (i == tablecollection.size() - 1) {
					failed("FAIL", "The table does not exist",
							"The table with the given text,'" + textTosearch
									+ "' is not present");
				}
			}
		} catch (Exception e) {
			logger.handleError("Failed to get text ", textTosearch, "for ",
					textTosearch, " ", e);
		}
		return tablecollection.get(FinalIndex);
	}

	/**
	 * For getting data of particular cell
	 * 
	 * @param tableElement
	 *            Element of the table
	 * @param row
	 *            Row number
	 * @param col
	 *            Column number
	 * @return String - Data of that particular cell
	 */

	public String getCellData(String elementName, int row, int col) {
		WebElement tableElement = getWebElement(elementName);
		List<WebElement> tr_Collection = tableElement.findElements(By
				.tagName("tr"));
		int row_num = 0;
		String Data = null;
		try {
			for (WebElement trElement : tr_Collection) {

				if (row_num == row) {
					List<WebElement> td_collection = trElement.findElements(By
							.tagName("td"));

					if (td_collection.size() == 0) {
						td_collection = trElement
								.findElements(By.tagName("th"));
					}
					Data = td_collection.get(col).getText();

					break;
				}

				row_num++;
			}
		} catch (Exception e) {
			logger.handleError("Failed to get cell data for ", tableElement,
					" ", e);
		}
		return Data;
	}
	
	public int GetRow(String tableLocator, String elementName, String colName) {
		WebElement tableElement = getWebElement(tableLocator);
		List<WebElement> tr_Collection = tableElement.findElements(By.xpath("./tbody/tr"));
		int row_num = 0;
		int col_num = 0;
		String Data = null;
		try {
			
			int colcount=getColumnCountOfRow(tableLocator);
			
			for (int i=1;i<=colcount;i++)
			{
				WebElement colElement=tableElement.findElement(By.xpath("./thead/tr/th["+i+"]"));
				if(colElement.getText().trim().equalsIgnoreCase(colName))
				{
					col_num=i;
					break;
				}
			}
			
			
			
			for (WebElement trElement : tr_Collection) {
				//if (row_num == 0) {
					/*List<WebElement> td_col = trElement.findElements(By
							.tagName("td")); */
				
					//WebElement rowElement=trElement.findElement(By.xpath("./td["+col_num+"]/label"));
					WebElement rowElement=trElement.findElement(By.xpath("./td["+col_num+"]"));
					if(rowElement.getText().trim().equalsIgnoreCase(elementName))
					{
						System.out.println("Row found : "+elementName);
						row_num=++row_num;
						break;
					}
					row_num++;
				//}

				
			}
		} catch (Exception e) {
			logger.handleError("Failed to get cell data for ", tableElement,
					" ", e);
		}
		return row_num;

	}

	
	public void clickrelativecell(String tableLocator, String rownumber, String colName) {
		WebElement tableElement = getWebElement(tableLocator);
		
		int row_num = 0;
		int col_num = 0;
		String Data = null;
		try {
			
			int colcount=getColumnCount(tableLocator);
			
			for (int i=1;i<=colcount;i++)
			{
				WebElement colElement=tableElement.findElement(By.xpath("./thead/tr/th["+i+"]"));
				if(colElement.getText().trim().equalsIgnoreCase(colName))
				{
					col_num=i;
					break;
				}
			}
			
			
			WebElement tr_element = tableElement.findElement(By.xpath("./tbody/tr["+rownumber+"]"));
			WebElement rowElement=tr_element.findElement(By.xpath("./td["+col_num+"]/input"));
			
			rowElement.click();
			
			
		} catch (Exception e) {
			logger.handleError("Failed to get cell data for ", tableElement,
					" ", e);
		}
		

	}
	
	
	

	/**
	 * For getting data of particular cell
	 * 
	 * @param tableElement
	 *            Element of the table
	 * @param row
	 *            Row number
	 * @param colName
	 *            Column name
	 * @return String - Data of that particular cell
	 */

	public String getCellData(String elementName, int row, String colName) {
		WebElement tableElement = getWebElement(elementName);
		List<WebElement> tr_Collection = tableElement.findElements(By
				.tagName("tr"));
		int row_num = 0;
		int col_num = 0;
		String Data = null;
		try {
			for (WebElement trElement : tr_Collection) {
				if (row_num == 0) {
					List<WebElement> td_col = trElement.findElements(By
							.tagName("td"));
					if (td_col.size() == 0) {
						td_col = trElement.findElements(By.tagName("th"));
					}
					for (int i = 0; i < td_col.size(); i++) {
						if (td_col.get(i).getText().contains(colName)) {
							col_num = i;
							break;
						}
					}
				}

				if (row_num == row) {
					List<WebElement> td_collection = trElement.findElements(By
							.tagName("td"));
					Data = td_collection.get(col_num).getText();
					break;
				}

				row_num++;
			}
		} catch (Exception e) {
			logger.handleError("Failed to get cell data for ", tableElement,
					" ", e);
		}
		return Data;

	}

	/**
	 * For getting data of particular cell
	 * 
	 * @param tableElement
	 *            Element of the table
	 * @param rowName
	 *            Row name
	 * @param colName
	 *            Column name
	 * @return String - Data of that particular cell
	 */
	public String getCellData(String elementName, String rowName, String colName) {

		WebElement tableElement = getWebElement(elementName);
		List<WebElement> tr_Collection = tableElement.findElements(By
				.tagName("tr"));
		int row_num = 0;
		int col_num = 0;

		String Data = null;
		try {
			for (WebElement trElement : tr_Collection) {
				if (row_num == 0) {
					List<WebElement> td_col = trElement.findElements(By
							.tagName("td"));

					for (int i = 0; i < td_col.size(); i++) {
						if (td_col.get(i).getText().contains(colName)) {
							col_num = i;
							break;
						}
					}
				}
				List<WebElement> td_col = trElement.findElements(By
						.tagName("td"));
				if (td_col.get(0).getText().contains(rowName)) {
					Data = td_col.get(col_num).getText();
					break;
				}
				row_num++;
			}
		} catch (Exception e) {
			logger.handleError("Failed to get cell data for ", tableElement,
					" ", e);
		}

		return Data;

	}

	public String getRelativeCellData(String elementName, String searchName,
			int columnNumber) {
		int row_count = 0;
		String cellData = null;

		WebElement tableElement = getWebElement(elementName);
		List<WebElement> allRows = tableElement.findElements(By.tagName("tr"));
		try {
			for (WebElement row : allRows) {
				int col_count = 0;
				List<WebElement> cells = row.findElements(By.tagName("td"));
				for (WebElement cell : cells) {
					if (cell.getText().equals(searchName)) {
						cellData = getCellData(elementName, row_count,
								columnNumber);
						break;
					}

					col_count++;
				}
				row_count++;
			}

		} catch (Exception e) {
			logger.handleError("getRelativeCellData ", elementName);

		}
		return cellData;
	}

	/*
	 * For Searching relative cell based on some value
	 * 
	 * @param elementName Element name for table locator - uiMap
	 * 
	 * @return int - Number of rows
	 */
	public String findRelativeCellAndClick(String elementName,
			String searchName, int columnNumber, String text) {
		int row_count = 0;
		String cellData = null;

		WebElement tableElement = getWebElement(elementName);
		List<WebElement> allRows = tableElement.findElements(By.tagName("tr"));
		try {
			for (WebElement row : allRows) {
				int col_count = 0;
				List<WebElement> cells = row.findElements(By.tagName("td"));
				for (WebElement cell : cells) {
					if (cell.getText().equals(searchName)) {
						cellData = getCellData(elementName, row_count,
								columnNumber);
						if (cellData.equals(text)) {
							clickCell(cell);
						}
						break;
					}

					col_count++;
				}

				row_count++;
				if (cellData != null) {
					break;
				}

			}

		} catch (Exception e) {
			logger.handleError("getRelativeCellData ", elementName);

		}
		return cellData;
	}
	
	public void CellClick(String elementName,
			int row, int columnNumber) {
		

		WebElement tableElement = getWebElement(elementName);
		
		String Locator="./tbody/tr["+row+"]/td["+columnNumber+"]/label";
		
		try {
			
			WebElement element = tableElement.findElement(By.xpath(Locator));
			clickCell(element);
			

		} catch (Exception ex) {
			try{
				
			}
			catch (Exception e){
			logger.handleError("CellClick ", elementName);
			}

		}
		//return cellData;
	}

	public void CellClick1(String elementName,
			int row, int columnNumber) {
		

		WebElement tableElement = getWebElement(elementName);
		
		String Locator="./tbody/tr["+row+"]/td["+columnNumber+"]/label";
		
		try {
			
				WebElement element = tableElement.findElement(By.xpath(Locator));
				clickCell(element);
				
				
			} 
		catch (Exception ex) 
			{
				try{
					Locator="./tbody/tr["+row+"]/td["+columnNumber+"]/input";
					WebElement element = tableElement.findElement(By.xpath(Locator));
					clickCell(element);
					
					}
				catch (Exception exx){
					try{
						Locator="./tbody/tr["+row+"]/td["+columnNumber+"]/label";
						WebElement element = tableElement.findElement(By.xpath(Locator));
						clickCell(element);
					}
					catch (Exception e){
					logger.handleError("CellClick ", elementName);
			}
		
			}//

			}//
		//return cellData;
	}



	public void clickCell(WebElement element) {

		element.click();

	}

	/**
	 * Overriding toString() method to return TableHandler format string
	 */
	public String toString() {
		return StringUtils.mapString(this);
	}

	private LogUtils logger = new LogUtils(this);
	private WebDriver webDr;
	private ObjectMap objMap;
	private WebUIDriver driver;
}
