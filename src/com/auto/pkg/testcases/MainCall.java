package com.auto.pkg.testcases;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Set;
//import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.auto.pkg.util.TCUtil;
import com.auto.pkg.testcases.DBUtil;



//import com.thoughtworks.selenium.Selenium;
//import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.ie.InternetExplorerDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.Select;
//import org.openqa.selenium.support.ui.WebDriverWait;
import jxl.*;
import jxl.read.biff.BiffException;

public class MainCall extends TCUtil {
	private WebDriver driver;
	private String baseUrl;
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = new StringBuffer();
	private Statement statement = null;
	private static String testSheetName;
	private FileInputStream fstream;
	private DataInputStream in;
	private String strLine = null;
	private String[] values = null;
	BufferedReader br;
	BufferedWriter logBuffer;
	private static int dataArrRow; //index to data sheet rows
	protected String cellValue;
	public long testStartTime;
	public long testEndTime;
	public String execTime;
	public static String exexMsg;
	public String testcaseWorksheet;
	public String testcaseWorkbook;

	String browserStr="InternetExplorerDriver";
	String DbServer = null;
	String DBPort = null;
	String DBTNS = null;
	String userName = null;
	String userPassword = null;

	public static String getTestCaseFile(String[] args) {
		System.out.println("String parameter is  : " + args[1]);
		return args[1];
	}

	private static int getDataArrRow() {
		return dataArrRow;
	}

	private static void setDataArrRow(int dataArrRow) {
		MainCall.dataArrRow = dataArrRow;
	}

	static int testPass = 0; //keeping count of passed testcases
	//private static int getTestPass() {
	//	return testPass;
	//}

	private static void setTestPass() {
		testPass++;
	}

	static int testFail = 0; //keeping count of failed testcases
	//private static int getTestFail() {
		//return testFail;
	//}

	private static void setTestFail() {
		testFail++;
	}
	String[] POP = new String[10]; // to store runtime data retrieved from UI

	@BeforeClass
	public void setUp() throws Exception {
		fstream = new FileInputStream("conf\\TestProps.csv");
		in = new DataInputStream(fstream);
		br = new BufferedReader(new InputStreamReader(in));
		br.readLine(); // to exempt header line (first row) of CSV file
		strLine = br.readLine(); // fetches the second line in CSV file.
		values = strLine.split(",");
		String browserDriver = values[5];
		String logFile = values[7];
		File driverFile;

		if(!(browserDriver.toLowerCase().contains(browserStr.toLowerCase()))) {
			createTestFiles(instanceCount());
		}

		/*********** Log Files details *******************/
		File logfile = new File(logFile);
		FileWriter file;
		if (logfile.exists())
			file = new FileWriter(logFile, true);
		else
			file = new FileWriter(logFile);
		logBuffer = new BufferedWriter(file);

		switch(browserDriver.toUpperCase())
		{
		case "CHROMEDRIVER":
			driverFile = new File("webdrivers\\chromedriver.exe");
			if(driverFile.exists())
			{
				System.setProperty("webdriver.chrome.driver","webdrivers\\chromedriver.exe");
				driver = new ChromeDriver();
				driver.manage().window().maximize();
			}
			else
			{
				log(logBuffer,"Error: Chrome driver missing in res folder.");
			}
			break;


		case "INTERNETEXPLORERDRIVER":

			driverFile = new File("webdrivers\\IEDriverServer.exe");
			if(driverFile.exists())
			{
				System.setProperty("webdriver.ie.driver", "webdrivers\\IEDriverServer.exe");
				driver = new InternetExplorerDriver();
			}
			else
			{
				log(logBuffer,"Error: Internet Explorer driver missing in res folder.");
			}
			break;

		case "FIREFOXDRIVER":
			driver = new FirefoxDriver();
			break;
		default:
			log(logBuffer,"Incorrect webdriver value present in property file.");
			log(logBuffer,"Valid webdriver values are:");
			log(logBuffer,"1. ChromeDriver.");
			log(logBuffer,"2. InternetExplorerDriver.");
			log(logBuffer,"3. FirefoxDriver.");
			log(logBuffer,"Ensure the chosen webdriver executables are present in res folder");
			logBuffer.close();
			System.exit(0);
		}
		/*
		if (browserDriver.equals("ChromeDriver")) {
			System.setProperty("webdriver.chrome.driver",
					"res\\chromedriver.exe");
			driver = new ChromeDriver();
		}

		if (browserDriver.equals("InternetExplorerDriver")) {
			System.setProperty("webdriver.ie.driver", "res\\IEDriverServer.exe");
			driver = new InternetExplorerDriver();

		}

		if (browserDriver.equals("FirefoxDriver")) {
			driver = new FirefoxDriver();
			// no need to setup the driver property explicitly
		}
		 */
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

	}

	@Test
	@Parameters("testProp")
	public void left_navigation(String testProp) throws Exception {
		//reading TestProps.csv
		DbServer = values[0];
		DBPort = values[1];
		DBTNS = values[2];
		userName = values[3];
		userPassword = values[4];
		String browserDriver = values[5];
		baseUrl = values[6];
		int testCount=0;
		//HtmlReport_initialize(baseUrl, browserDriver);
		FileReader tcFile=null;

		//String browserDriver = values[5];

		/*********** database setup *******************/
		//DBUtil db = new DBUtil(DbServer, DBPort, DBTNS, userName, userPassword);
		//Connection connect = db.getConnection();
		//statement = connect.createStatement();

		/*
		/*********** Log Files details *******************
		File logfile = new File(logFile);
		FileWriter file;
		if (logfile.exists())
			file = new FileWriter(logFile, true);
		else
			file = new FileWriter(logFile);
		BufferedWriter logBuffer = new BufferedWriter(file);
		 */

		/*********** Test Execution Starts *******************/
		log(logBuffer, "Test Execution started at " + new Date()+"\n");
		// long freeMemory=Runtime.getRuntime().freeMemory();
		// long totalMemory=Runtime.getRuntime().totalMemory();
		// System.out.println(totalMemory+"    "+freeMemory);

		/********** Login **********/

		if(baseUrl.length()>0) {
			try {
				driver.get(baseUrl);
			} catch(Exception e) {
				exexMsg="URL \"" + baseUrl + "\" not found";
				log(logBuffer, "URL \"" + baseUrl + "\" not found.\n");
			}

		}
		/*driver.findElement(By.name("USER_NAME")).clear();
		driver.findElement(By.name("USER_NAME")).sendKeys(user_name);
		driver.findElement(By.name("PASSWORD")).clear();
		driver.findElement(By.name("PASSWORD")).sendKeys(user_password);
		driver.findElement(By.cssSelector("span.buttonInner")).click();
		 //driver.findElement(By.cssSelector("span.loginInner")).click();
		Thread.sleep(1000);

		//Clicking and expanding left navigation
		/*
		 * if(isElementPresent(By.linkText("Data Management"))==false) {
		 * driver.findElement
		 * (By.cssSelector("div.navButton.solutionNav")).click();
		 * Thread.sleep(1000);
		 * driver.findElement(By.cssSelector("span.navPin")).click(); }
		 *
		 * /*if(isElementPresent(By.linkText("Manufacturing Network"))==false) {
		 * driver.findElement(By.linkText("Data Management")).click(); }
		 */


		/*** Test Case Call ****/
		if(browserDriver.toLowerCase().contains(browserStr.toLowerCase())) {
			tcFile = new FileReader("res\\TestCase.txt");
		}
		else {
			tcFile = new FileReader("res\\"+testProp);
		}

		BufferedReader tcBuffer = new BufferedReader(tcFile);


		while ((testSheetName = tcBuffer.readLine()) != null)
		{
			//skip commented (#) test cases in TestCases.txt
			if (testSheetName.trim().startsWith("#", 0)|| testSheetName.isEmpty())

				continue;

			//Splitting workbook and worksheet

			String[] testcaseParam = testSheetName.trim().split(",");
			testcaseWorkbook = testcaseParam[0];
			testcaseWorksheet = testcaseParam[1];

			if (testcaseWorksheet.trim().startsWith("DD_", 0))
			{
				continue;
			}

			/*********************************************************************/
			int sheetCount = 1;
			Sheet[] sheetArray;
			boolean tcFlag = false;
			//Reading test case workbook to get all sheet names
			FileInputStream istreamWB = new FileInputStream(testcaseWorkbook);
			Workbook workbook = Workbook.getWorkbook(istreamWB);
			/*
			if(workbook.getSheet(testcaseWorksheet).getName() == null)
			{
				log(logBuffer, "Sheet " + testcaseWorksheet + " not found in workbook " + testcaseWorkbook);
				System.exit(0);
			}
			 */
			if(testcaseWorksheet.toLowerCase().equals("all"))
			{
				sheetArray = workbook.getSheets(); // to read all sheets in workbook
				sheetCount = sheetArray.length;
				tcFlag=true;
			}
			else
			{
				sheetCount=1;
				tcFlag=false;
			}


			for(int iCount=0; iCount < sheetCount;iCount++)
			{
				if(tcFlag)
				{
					testcaseWorksheet = workbook.getSheet(iCount).getName();
				}

				if(!(testcaseWorksheet.equalsIgnoreCase("main")||testcaseWorksheet.equalsIgnoreCase("resource")||testcaseWorksheet.equalsIgnoreCase("contigencyPlan")||testcaseWorksheet.contains("#") || testcaseWorksheet.trim().startsWith("DD_", 0)))
				{
					testCount++;
					try {

						log(logBuffer, "*****Executing testcase " + testcaseWorkbook + "," + testcaseWorksheet + "*****\n");

						testStartTime=System.currentTimeMillis();
						boolean testResult = test_engine(logBuffer, testcaseWorkbook,testcaseWorksheet,null);
						testEndTime=System.currentTimeMillis();

						long diffTime=testEndTime - testStartTime;
						execTime=msToString(diffTime);

						if (testResult == false) {
							//reportBuffer.write("###### Testcase " + testcaseWorkbook + "," + testcaseWorksheet + " failed. ######\n");
							//reportBuffer.flush();
							log(logBuffer, "###### Testcase " + testcaseWorkbook + ","+ testcaseWorksheet + " Status:Failed. Error Message : " + exexMsg + " Execution Time: " + execTime + " ######\n");
							setTestFail();
							log(logBuffer, "Calling contigency plan sheet");
							test_engine(logBuffer, testcaseWorkbook,"contigencyPlan",null);
							System.gc();
							//HtmlReport_lines(testcaseWorkbook,testcaseWorksheet,"Failed",exexMsg, execTime);
						} else {
							//reportBuffer.write("###### Testcase " + testcaseWorkbook + ","+ testcaseWorksheet + " passed. ######\n");
							log(logBuffer, "###### Testcase " + testcaseWorkbook + "," + testcaseWorksheet + " Status:Passed. Execution Time: " + execTime + " ######\n");
							//reportBuffer.flush();
							setTestPass();
							System.gc();
							//HtmlReport_lines(testcaseWorkbook,testcaseWorksheet,"Passed",exexMsg,execTime);
						}
					} catch (Exception e) {
						log(logBuffer,"-------------------Error Stack Trace--------------------------");
						log(logBuffer, e.toString());
						log(logBuffer,"--------------------------------------------------------------");
						if(e.toString().contains("NullPointerException")) {
							exexMsg=testcaseWorkbook + ","+ testcaseWorksheet + " worksheet not found";
						} else {
						exexMsg=e.toString();
						}
						//reportBuffer.write("Testcase " + testcaseWorkbook + "," + testcaseWorksheet + " failed. ######\n");
						//reportBuffer.flush();
						log(logBuffer, "Testcase " + testcaseWorkbook + ","+ testcaseWorksheet + " Status:Failed. Error Message : " + exexMsg + " Execution Time: " + execTime + " ######\n");
						setTestFail();
						log(logBuffer, "Calling contigency plan sheet");
						test_engine(logBuffer, testcaseWorkbook,"contigencyPlan",null);
						System.gc();
						//HtmlReport_lines(testcaseWorkbook,testcaseWorksheet,"Warning/Black Page",exexMsg,execTime);
					}
				}//end of if
			}// end of iCount for loop
			istreamWB.close();
		} //end of while


		if((countFailed() + countPassed())==totalTestCount()) {
			log(logBuffer, "\n*****************Test Summary*********************");
			log(logBuffer, "Total test cases executed = " + (countFailed() + countPassed()) + ".");
			log(logBuffer, "Total test cases passed = " + countPassed() + ".");
			log(logBuffer, "Total test cases failed = " + countFailed() + ".");
			log(logBuffer, "\n**************************************************");
		}

		/**
		if((countFailed() + countPassed())==validTestCount()) {
			HtmlReport_initialize(baseUrl, browserDriver);
			HtmlReport_result(validTestCount(), countPassed(), countFailed());
			HtmlReport_close();
			HtmlReport_generator();
		}
		**/
		tcBuffer.close();
		//logBuffer.close();
		if(!(browserDriver.toLowerCase().contains(browserStr.toLowerCase()))) {
			try{
				File file = new File("res\\"+testProp);
				if(file.delete()){
					System.out.println(file.getName() + " is deleted!");
				}else{
					System.out.println(file.getName() +  "Delete operation failed.");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public boolean test_engine(BufferedWriter logBuffer, String workbookPath, String sheetName, String[][] dataArr) throws InterruptedException, SQLException
	{
		try {

			FileInputStream fstreamWB = new FileInputStream(workbookPath);
			Workbook workbook = Workbook.getWorkbook(fstreamWB);
			Sheet sheet = null;
			boolean sheetFlag = false;
			String altSheet = null;
			String wb=workbookPath.substring(5);
			String excelFileName=wb.substring(0,(wb.length()-4));
			String propfile="conf//" + excelFileName+".properties";
			String[] allSheets = workbook.getSheetNames();

			for (int i =0; i<allSheets.length; i++)
			{
				if(allSheets[i].equals(sheetName))
				{
					sheetFlag=true;
					break;
				}
				else
				{
					sheetFlag=false;
					if(allSheets[i].equalsIgnoreCase(sheetName))
						altSheet=allSheets[i];
				}

			}

			if(sheetFlag)
			{
				sheet = workbook.getSheet(sheetName);
			}
			else
			{
				exexMsg="Sheet \"" + sheetName + "\" not found in workbook " + workbookPath;
				log(logBuffer, "Sheet : \"" + sheetName + "\" not found in workbook " + workbookPath);
				if(altSheet!=null)
					log(logBuffer, "Instead we found \"" + altSheet + "\". Hence, executing it.");
				sheet = workbook.getSheet(altSheet);
			}

			for (int row = 1; row < sheet.getRows(); row++)
			{
				if(sheet.getRow(row).length == 0)
					return true;

				Cell[] cells = sheet.getRow(row);
				String zeroCellVal = cells[0].getContents();
				String firstCellVal= cells[1].getContents();
				String secondCellVal= cells[2].getContents();
				String thirdCellVal= cells[3].getContents();

				cellValue=zeroCellVal + " : " + firstCellVal + ":" + secondCellVal + ":" + thirdCellVal;
				if(firstCellVal.startsWith("prop:")) {
					String[] firstPropString = firstCellVal.split(":");
					File f = new File(propfile);
					if(f.exists()){
						firstCellVal=getPropertyValue(propfile, firstPropString[1]);
						if(firstCellVal.equals("propNotFound")) {
							exexMsg="Property " + firstPropString[1] + " not found in  file " + propfile;
							log(logBuffer, "Property " + firstPropString[1] + " not found in  file " + propfile);
							return false;
						}
					}
					else {
						exexMsg="Could not find Property file " + propfile;
						log(logBuffer, "Could not find Property file " + propfile);
						return false;
					}

				}

				if(secondCellVal.startsWith("prop:")) {
					String [] secondPropString = secondCellVal.split(":");
					File f = new File(propfile);
					if(f.exists()){
						secondCellVal=getPropertyValue(propfile, secondPropString[1]);
						if(secondCellVal.equals("propNotFound")) {
							exexMsg="Property " + secondPropString[1] + " not found in  file " + propfile;
							log(logBuffer, "Property " + secondPropString[1] + " not found in  file " + propfile);
							return false;
						}
					}
					else {
						exexMsg="Could not find Property file " + propfile;
						log(logBuffer, "Could not find Property file " + propfile);
						return false;
					}
				}
				if (secondCellVal.toUpperCase().contains("#PARAM,"))
				{

					if(!zeroCellVal.equalsIgnoreCase("skipstep"))
					{
						String [] paramString = secondCellVal.split(",");
						int paramIndex = Integer.parseInt(paramString[1]);
						secondCellVal = dataArr[getDataArrRow()][paramIndex];
					}
				}

				if (secondCellVal.toUpperCase().contains("POP,"))
				{
					if(!zeroCellVal.equalsIgnoreCase("skipstep"))
					{
						secondCellVal = stackPop(logBuffer, secondCellVal);
					}
				}
				if (zeroCellVal.equals(""))
					return true;
				log(logBuffer, "#" + zeroCellVal.toUpperCase());

				//all cases should be in upper case here
				// Existence of element should be verified before performing any action on it.
				switch (zeroCellVal.toUpperCase())
				{
				case "LINK":
					String linkName = null;
					if(secondCellVal.trim().length() > 0)
					{
						linkName = secondCellVal;
					}
					else
					{
						if(firstCellVal.trim().length() > 0)
						{
							linkName = firstCellVal;
						}
						else
						{
							exexMsg="Parameter missing: Value";
							log(logBuffer, "Parameter missing: Value");
							return false;
						}
					}
					log(logBuffer, testcaseWorksheet + ": Clicking on link \"" + linkName + "\".\n");
					if (driver.findElements(By.linkText(linkName)).size() != 0) {
						driver.findElement(By.linkText(linkName)).click();
						Thread.sleep(2000);
					} else {
						exexMsg="Link \"" + linkName + "\" not found";
						log(logBuffer, testcaseWorksheet + ": Link \"" + linkName + "\" not found.\n");
						return false;
					}
					break;

				case "DOUBLE_CLICK":
					String objName = null;
					if(secondCellVal.trim().length() > 0)
					{
						objName = secondCellVal;
					}
					else
					{
						if(firstCellVal.trim().length() > 0)
						{
							objName = firstCellVal;
						}
						else
						{
							exexMsg="Parameter missing: Value";
							log(logBuffer, "Parameter missing: Value");
							return false;
						}
					}
					log(logBuffer, testcaseWorksheet + ": Clicking on Object \"" + objName + "\".\n");
					if (driver.findElements(By.linkText(objName)).size() != 0) {
						WebElement listElement=driver.findElement(By.linkText(objName));
						((Actions) listElement).doubleClick();
						Thread.sleep(2000);
					} else {
						exexMsg="Object \"" + objName + "\" not found";
						log(logBuffer, testcaseWorksheet + ": Object \"" + objName + "\" not found.\n");
						return false;
					}
					break;

				case "BUTTON":
					String buttonName = null;
					boolean IsbuttonNameId=false;
					if(firstCellVal.trim().length() > 0)
					{
						buttonName = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Clicking on button \"" + buttonName + "\".\n");
					try {
						IsbuttonNameId = IsFindElementById(buttonName);
					} catch(Exception e) {
						exexMsg="Button " + buttonName + "not found by Id";
						log(logBuffer, testcaseWorksheet + ": Button " + buttonName + "not found by Id\n");
					}

					if(IsbuttonNameId)
					{
						if (driver.findElements(By.id(buttonName)).size() != 0) {
							driver.findElement(By.id(buttonName)).click();
							Thread.sleep(2000);
						} else {
							exexMsg="Button \"" + buttonName	+ "\" not found";
							log(logBuffer, testcaseWorksheet + ": Button \"" + buttonName	+ "\" not found.\n");
							return false;
						}
					}
					else
					{
						if (driver.findElements(By.xpath(buttonName)).size() != 0) {
							driver.findElement(By.xpath(buttonName)).click();
							Thread.sleep(2000);
						} else {
							exexMsg="Button \"" + buttonName	+ "\" not found by xpath";
							log(logBuffer, testcaseWorksheet + ": Button \"" + buttonName	+ "\" not found  by xpath.\n");
							return false;
						}
					}
					break;
				case "TEXTBOX":
					String textboxName = null;
					String textValue = null;
					if(firstCellVal.trim().length() > 0)
					{
						textboxName = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						textValue = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Entering \"" + textValue + "\" into textbox \"" + textboxName + "\".\n");
					boolean IstextboxNameId = IsFindElementById(textboxName);
					if(IstextboxNameId)
					{
						if (driver.findElements(By.id(textboxName)).size() != 0) {
							driver.findElement(By.id(textboxName)).sendKeys(textValue);
							Thread.sleep(2000);
						} else {
							exexMsg="Textbox \"" + textboxName + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Textbox \"" + textboxName + "\" not found.\n");
							return false;
						}
					}
					else
					{
						if (driver.findElements(By.xpath(textboxName)).size() != 0) {
							driver.findElement(By.xpath(textboxName)).sendKeys(textValue);
							Thread.sleep(2000);
						} else {
							exexMsg="Textbox \"" + textboxName + "\" not found";
							log(logBuffer, testcaseWorksheet + ": Textbox \"" + textboxName + "\" not found.\n");
							return false;
						}
					}
					break;

				case "TEXTBOXCLEAR":
					String txtboxName = null;
					if(firstCellVal.trim().length() > 0) {
						txtboxName = firstCellVal;
					}
					else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Clearing textbox \"" + txtboxName + "\".\n");
					boolean IstxtboxNameId = IsFindElementById(txtboxName);
					if(IstxtboxNameId)	{
						if (driver.findElements(By.id(txtboxName)).size() != 0) {
							driver.findElement(By.id(txtboxName)).clear();
							Thread.sleep(2000);
						} else {
							exexMsg="Textbox \"" + txtboxName + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Textbox \"" + txtboxName + "\" not found.\n");
							return false;
						}
					} else	{
						if (driver.findElements(By.xpath(txtboxName)).size() != 0) {
							driver.findElement(By.xpath(txtboxName)).clear();
							Thread.sleep(2000);
						} else {
							exexMsg="Textbox \"" + txtboxName + "\" not found";
							log(logBuffer, testcaseWorksheet + ": Textbox \"" + txtboxName + "\" not found.\n");
							return false;
						}
					}
					break;
				case "URL":
					String urlStr=firstCellVal.trim();
					if(firstCellVal.trim().length() > 0)
					{
						driver.get(urlStr);
						//System.out.println(driver.getPageSource());
					}
					else
					{
						exexMsg="URL not provided";
						log(logBuffer, testcaseWorksheet + ": URL not provided");
						return false;
					}
					break;

				case "FILE_EXISTS":
					String filDirName=firstCellVal.trim();
					if(firstCellVal.trim().length() > 0 && fileDirExists(filDirName))
					{
						log(logBuffer, testcaseWorksheet + ": File/Dir " + filDirName + " exits.\n");
					}
					else
					{
						exexMsg="File/Dir " + filDirName + " does not exists";
						log(logBuffer, testcaseWorksheet + ": File/Dir " + filDirName + " does not exists");
						return false;
					}
					break;

				case "FIND_STRING":
					String seachFile=firstCellVal.trim();
					String seachStr=secondCellVal.trim();
					if(firstCellVal.trim().length() > 0 && secondCellVal.trim().length() > 0)
					{
						if(findString(seachFile,seachStr)) {
							log(logBuffer, seachStr + " found in " + seachFile + ".\n");

						} else	{
							exexMsg=seachStr + " Not found in " + seachFile;
							log(logBuffer, seachStr + " Not found in " + seachFile);
							return false;
						}
					}
					else
					{
						exexMsg="Search file or String is empty";
						log(logBuffer, testcaseWorksheet + ": Search file or String is empty");
						return false;
					}
					break;

				case "WIN_CMD":
					String winCmd=firstCellVal.trim();
					if(firstCellVal.trim().length() > 0)
					{
						Boolean exec=execWinBatch(winCmd);
						if(exec){
							log(logBuffer, winCmd + "is executed.\n");
						} else {
							exexMsg=winCmd + " execution failed.";
							log(logBuffer, winCmd + " execution failed.");
							return false;
						}


					}
					else
					{
						exexMsg="Windows batch Script not provided";
						log(logBuffer, testcaseWorksheet + ": Windows batch Script not provided");
						return false;
					}
					break;

				case "UNIX_SHELL":
					String UnixShell=firstCellVal.trim();
					if(firstCellVal.trim().length() > 0)
					{
						Boolean exec=execWinBatch(UnixShell);
						if(exec){
							log(logBuffer, UnixShell + "is executed.\n");
						} else {
							exexMsg=UnixShell + " execution failed.";
							log(logBuffer, UnixShell + " execution failed.");
							return false;
						}


					}
					else
					{
						exexMsg="Unix Shell Script not provided";
						log(logBuffer, testcaseWorksheet + ": Unix Shell Script not provided");
						return false;
					}
					break;

				case "DROPDOWN":
					String dropdownName = null;
					String dropdownChoice = null;
					if(firstCellVal.trim().length() > 0)
					{
						dropdownName = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						dropdownChoice = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Selecting Value \"" + dropdownChoice + "\" in the dropdown " + dropdownName + "\n");
					// new
					// Select(driver.findElement(By.xpath(dropdownName))).selectByVisibleText(dropdownChoice);
					boolean IsdropdownNameId = IsFindElementById(dropdownName);
					log(logBuffer, testcaseWorksheet + ": Dropdown object : " + dropdownName + " is " + IsdropdownNameId + "\n");

					if(IsdropdownNameId)
					{
						if (driver.findElements(By.id(dropdownName)).size() != 0) {
							WebElement dropDownListBox = driver.findElement(By.id(dropdownName));
							Select clickThis = new Select(dropDownListBox);
							clickThis.selectByVisibleText(dropdownChoice);
							Thread.sleep(2000);
						} else {
							exexMsg="Dropdown \"" + dropdownName + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Dropdown \"" + dropdownName + "\" not found.\n");
							return false;
						}
					}
					else
					{
					if (driver.findElements(By.xpath(dropdownName)).size() != 0) {
						WebElement dropDownListBox = driver.findElement(By
								.xpath(dropdownName));
						Select clickThis = new Select(dropDownListBox);
						clickThis.selectByVisibleText(dropdownChoice);
						Thread.sleep(2000);
					} else {
						exexMsg="Dropdown \"" + dropdownName	+ "\" not found";
						log(logBuffer, testcaseWorksheet + ": Dropdown \"" + dropdownName	+ "\" not found.\n");
						return false;
					}
					}
					break;

				case "FRAME":
					String frameName = null;
					if(firstCellVal.trim().length() > 0)
					{
						frameName = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Clicking on frame " + frameName + "\n");
					if (driver.findElements(By.name(frameName)).size() != 0) {
						driver.switchTo().frame(frameName);
						Thread.sleep(2000);
					} else {
						exexMsg="Frame \"" + frameName + "\" not found";
						log(logBuffer, testcaseWorksheet + ": Frame \"" + frameName + "\" not found.\n");
						return false;
					}
					break;

				case "FRAMEXPATH":
					String frameNameXPATH = null;
					if(firstCellVal.trim().length() > 0)
					{
						frameNameXPATH = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Clicking on frame " + frameNameXPATH + "\n");
					boolean IsframeNameXPATHId = IsFindElementById(frameNameXPATH);
					if(IsframeNameXPATHId)
					{
						if (driver.findElements(By.id(frameNameXPATH)).size() != 0) {
							driver.switchTo().frame(
									driver.findElement(By.id(frameNameXPATH)));
							Thread.sleep(2000);
						} else {
							exexMsg="Frame \"" + frameNameXPATH + "\" not found";
							log(logBuffer, testcaseWorksheet + ": Frame \"" + frameNameXPATH + "\" not found.\n");
							return false;
						}
					}
					else
					{
					if (driver.findElements(By.xpath(frameNameXPATH)).size() != 0) {
						driver.switchTo().frame(
								driver.findElement(By.xpath(frameNameXPATH)));
						Thread.sleep(2000);
					} else {
						exexMsg="Frame \"" + frameNameXPATH + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Frame \"" + frameNameXPATH + "\" not found.\n");
						return false;
					}
					}
					break;

				case "FRAMEDEFAULT":
					log(logBuffer, testcaseWorksheet + ": Selecting default frame.\n");
					driver.switchTo().defaultContent();
					Thread.sleep(2000);
					break;

				case "TRANSFERBOX_VALUE":
					String transferValue = null;
					if(firstCellVal.trim().length() > 0)
					{
						transferValue = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Selecting transfer value \"" + transferValue + "\".\n");
					boolean IstransferValueId = IsFindElementById(transferValue);
					if(IstransferValueId)
					{
						if (driver.findElements(By.id(transferValue)).size() != 0) {
							driver.findElement(By.id(transferValue)).click();
							Thread.sleep(2000);
						} else {
							exexMsg="Transfer Value \"" + transferValue + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Transfer Value \"" + transferValue + "\" not found.\n");
							return false;
						}
					}
					else
					{
					if (driver.findElements(By.xpath(transferValue)).size() != 0) {
						driver.findElement(By.xpath(transferValue)).click();
						Thread.sleep(2000);
					} else {
						exexMsg="Transfer Value \"" + transferValue + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Transfer Value \"" + transferValue + "\" not found.\n");
						return false;
					}
					}
					break;

				case "TRANSFERBOX_ARROW":
					String transferImage = null;
					if(firstCellVal.trim().length() > 0)
					{
						transferImage = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Clicking the transfer button \n" + transferImage + "\".\n");
					boolean IstransferImageId = IsFindElementById(transferImage);
					if(IstransferImageId)
					{
						driver.findElement(By.id(transferImage)).click();
						if (driver.findElements(By.id(transferImage)).size() != 0) {
							driver.findElement(By.id(transferImage)).click();
							Thread.sleep(2000);
						} else {
							exexMsg="Transfer arrow \"" + transferImage + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Transfer arrow \"" + transferImage + "\" not found.\n");
							return false;
						}
					}
					else
					{
					driver.findElement(By.xpath(transferImage)).click();
					if (driver.findElements(By.xpath(transferImage)).size() != 0) {
						driver.findElement(By.xpath(transferImage)).click();
						Thread.sleep(2000);
					} else {
						exexMsg="Transfer arrow \"" + transferImage + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Transfer arrow \"" + transferImage + "\" not found.\n");
						return false;
					}
					}
					break;

				case "SCREENSHOT":
					String imageName = null;
					if(secondCellVal.trim().length() > 0)
					{
						imageName = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					screenshot(driver, logBuffer, sheet.getName(), imageName);
					break;

				case "LOG":
					String logText = null;
					if(secondCellVal.trim().length() > 0)
					{
						logText = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, logText);
					break;

				case "WAIT":
					String waitTime = null;
					if(secondCellVal.trim().length() > 0)
					{
						waitTime = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Waiting for " + waitTime + " milli seconds.\n");
					Thread.sleep(Long.parseLong(waitTime));
					break;

				case "VALIDATE_TEXT":
					String textXpath = null;
					String textString = null;
					if(firstCellVal.trim().length() > 0)
					{
						textXpath = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						textString = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Validating text " + textString + " for element " + textXpath);
					boolean IstextXpathId = IsFindElementById(textXpath);
					if(IstextXpathId)
					{
						if (driver.findElements(By.id(textXpath)).size() != 0) {
							if (driver.findElement(By.id(textXpath)).getText()
									.equals(textString))
								log(logBuffer, testcaseWorksheet + ": Text " + textString
										+ " verified on UI.\n");
							else {
								exexMsg="Text " + textString + " couldn't be verified on UI.";
								log(logBuffer, testcaseWorksheet + ": Text " + textString + " couldn't be verified on UI.\n");
								return false;
							}
							Thread.sleep(2000);
						} else {
							exexMsg="Text \"" + textXpath + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Text \"" + textXpath + "\" not found.\n");
							return false;
						}
					}
					else
					{
					if (driver.findElements(By.xpath(textXpath)).size() != 0) {
						if (driver.findElement(By.xpath(textXpath)).getText()
								.equals(textString))
							log(logBuffer, testcaseWorksheet + ": Text " + textString	+ " verified on UI.\n");
						else {
							exexMsg = testcaseWorksheet + ": Text " + textString	+ " couldn't be verified on UI.";
							log(logBuffer, testcaseWorksheet + ": Text " + textString	+ " couldn't be verified on UI.\n");
							return false;
						}
						Thread.sleep(2000);
					} else {
						exexMsg="Text \"" + textXpath + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Text \"" + textXpath + "\" not found.\n");
						return false;
					}
					}
					break;

				case "VALIDATE_TEXT_PARTIAL":
					String textXpathPart = null;
					String textStringPart = null;
					if(firstCellVal.trim().length() > 0)
					{
						textXpathPart = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						textStringPart = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Validating text " + textStringPart + " for element " + textXpathPart + "\n");
					boolean IstextXpathPartId = IsFindElementById(textXpathPart);
					if(IstextXpathPartId)
					{
						if (driver.findElements(By.id(textXpathPart)).size() != 0) {
							if (driver.findElement(By.id(textXpathPart))
									.getText().contains(textStringPart))
								log(logBuffer, testcaseWorksheet + ": Text " + textStringPart
										+ " verified on UI.\n");
							else
							{
								exexMsg="Text " + textStringPart + " couldn't be verified on UI.";
								log(logBuffer, testcaseWorksheet + ": Text " + textStringPart + " couldn't be verified on UI.\n");
								return false;
							}

							Thread.sleep(2000);
						} else {
							exexMsg="Text \"" + textXpathPart + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Text \"" + textXpathPart + "\" not found.\n");
							return false;
						}
					}
					else
					{
					if (driver.findElements(By.xpath(textXpathPart)).size() != 0) {
						if (driver.findElement(By.xpath(textXpathPart))
								.getText().contains(textStringPart))
							log(logBuffer, testcaseWorksheet + ": Text " + textStringPart	+ " verified on UI.\n");
						else
						{
							exexMsg="Text " + textStringPart + " couldn't be verified on UI.";
							log(logBuffer, testcaseWorksheet + ": Text " + textStringPart + " couldn't be verified on UI.\n");
							return false;
						}

						Thread.sleep(2000);
					} else {
						exexMsg="Text \"" + textXpathPart + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Text \"" + textXpathPart + "\" not found.\n");
						return false;
					}
					}
					break;

				case "GET_TEXT":
					String getTextXpath = null;
					String pushIndexText = null;
					if(firstCellVal.trim().length() > 0)
					{
						getTextXpath = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						pushIndexText = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Getting text of element " + getTextXpath);
					boolean IsgetTextXpathId = IsFindElementById(getTextXpath);
					if(IsgetTextXpathId)
					{
						if (driver.findElements(By.id(getTextXpath)).size() != 0) {
							log(logBuffer,
									"Text found is - "
											+ driver.findElement(
													By.id(getTextXpath))
													.getText() + "\n");
							if (pushIndexText.toUpperCase().contains("PUSH,")) {
								stackPush(logBuffer, pushIndexText, driver
										.findElement(By.id(getTextXpath))
										.getText());
							}

							Thread.sleep(2000);
						} else {
							exexMsg="Text path \"" + getTextXpath	+ "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Text path \"" + getTextXpath	+ "\" not found.\n");
							return false;
						}
					}
					else
					{
					if (driver.findElements(By.xpath(getTextXpath)).size() != 0) {
						log(logBuffer,
								"Text found is - "
										+ driver.findElement(
												By.xpath(getTextXpath))
												.getText() + "\n");
						if (pushIndexText.toUpperCase().contains("PUSH,")) {
							stackPush(logBuffer, pushIndexText, driver
									.findElement(By.xpath(getTextXpath))
									.getText());
						}

						Thread.sleep(2000);
					} else {
						exexMsg="Text path \"" + getTextXpath + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Text path \"" + getTextXpath + "\" not found.\n");
						return false;
					}
					}
					break;

				case "VALIDATE_IF_FIELD_EMPTY":
					String getEmptyTextXpath = null;
					String Compare_Text=null;
					if(firstCellVal.trim().length() > 0)
					{
						getEmptyTextXpath = firstCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}


					log(logBuffer, testcaseWorksheet + ": Getting text of element " + getEmptyTextXpath);
					boolean IsgetEmptyTextXpathId = IsFindElementById(getEmptyTextXpath);
					if(IsgetEmptyTextXpathId)
					{
						Compare_Text=driver.findElement(By.id(getEmptyTextXpath)).getAttribute("value").trim();
						if (Compare_Text.isEmpty())

						{
							log(logBuffer, "Text path \"" + getEmptyTextXpath + "\" is Empty.\n");
							Thread.sleep(2000);
						}

						else {
							exexMsg="Text path \"" + getEmptyTextXpath	+ "\" is not Empty.";
							log(logBuffer, testcaseWorksheet + ": Text path \"" + getEmptyTextXpath	+ "\" is not Empty.\n");
							return false;
						}
					}
					else
					{
					Compare_Text=driver.findElement(By.xpath(getEmptyTextXpath)).getAttribute("value").trim();
					if (Compare_Text.isEmpty())

					{
						log(logBuffer,"Text path \"" + getEmptyTextXpath + "\" is Empty.\n");
						Thread.sleep(2000);
					}

					else {
						exexMsg="Text path \"" + getEmptyTextXpath + "\" is not Empty.";
						log(logBuffer, testcaseWorksheet + ": Text path \"" + getEmptyTextXpath + "\" is not Empty.\n");
						return false;
					}
					}
					break;

				case "EXIT_ON_TEXTMATCH_TRUE":
					String textXpathTrue = null;
					String textStringTrue = null;
					if(firstCellVal != null)
					{
						textXpathTrue = firstCellVal;
					} else	{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal != null)
					{
						textStringTrue = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Validating text " + textStringTrue + " for " + textXpathTrue);
					boolean IstextXpathTrueId = IsFindElementById(textXpathTrue);
					if(IstextXpathTrueId)
					{
						if (driver.findElements(By.id(textXpathTrue)).size() != 0)
						{
							if (driver.findElement(By.id(textXpathTrue))
									.getText().equals(textStringTrue))
							{
								exexMsg="Unexpected Text " + textXpathTrue + " is present on UI.";
								log(logBuffer, testcaseWorksheet + ": Unexpected Text \"" + textStringTrue + "\"  is present on UI. Exiting.\n");
								return false;
							}
							else
							{
								log(logBuffer, testcaseWorksheet + ": Unexpected Text \"" + textStringTrue	+ "\" is not present on UI.\n");
							}

							Thread.sleep(2000);
						} else
						{
							exexMsg="Unexpected Text " + textXpathTrue + " is present on UI.";
							log(logBuffer, testcaseWorksheet + ": Text path \"" + textXpathTrue	+ "\" not found.\n");
							return false;
						}
					}
					else
					{
					if (driver.findElements(By.xpath(textXpathTrue)).size() != 0)
					{
						if (driver.findElement(By.xpath(textXpathTrue))
								.getText().equals(textStringTrue))
						{
							exexMsg="Unexpected Text " + textStringTrue + "  is present on UI. Exiting.";
							log(logBuffer, testcaseWorksheet + ": Unexpected Text " + textStringTrue + "  is present on UI. Exiting.\n");
							return false;
						}
						else
						{
							log(logBuffer, testcaseWorksheet + ": Unexpected Text " + textStringTrue + " is not present on UI.\n");
						}

						Thread.sleep(2000);
					} else
					{
						exexMsg="Text path \"" + textXpathTrue + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Text path \"" + textXpathTrue + "\" not found.\n");
						return false;
					}
					}
					break;

				case "EXIT_ON_TEXTMATCH_FALSE":
					String textXpathFalse = null;
					String textStringFalse = null;
					if(firstCellVal != null)
					{
						textXpathFalse = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal != null)
					{
						textStringFalse = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Validating text " + textStringFalse + " for "
							+ textXpathFalse);
					boolean IstextXpathFalseId = IsFindElementById(textXpathFalse);
					if(IstextXpathFalseId)
					{
						if (driver.findElements(By.id(textXpathFalse)).size() != 0)
						{
							if (!driver.findElement(By.id(textXpathFalse))
									.getText().equals(textStringFalse))
							{
								exexMsg="Expected Text " + textStringFalse + " is NOT present on UI.";
								log(logBuffer, testcaseWorksheet + ": Expected Text \"" + textStringFalse										+ "\" is not present on UI. Exiting.\n");
								return false;
							}	else {
								log(logBuffer, testcaseWorksheet + ": Expected Text \"" + textStringFalse + "\" is present on UI.\n");
							}
							Thread.sleep(2000);

						}	else {
							exexMsg="Text path \"" + textXpathFalse + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Text path \"" + textXpathFalse + "\" not found.\n");
							return false;
						}
					}
					else
					{
					if (driver.findElements(By.xpath(textXpathFalse)).size() != 0)
					{
						if (!driver.findElement(By.xpath(textXpathFalse))
								.getText().equals(textStringFalse))
						{
							exexMsg="Expected Text " + textStringFalse + " is NOT present on UI.";
							log(logBuffer, testcaseWorksheet + ": Expected Text " + textStringFalse	+ " is not present on UI. Exiting.\n");
							return false;
						}	else {
							log(logBuffer, testcaseWorksheet + ": Expected Text " + textStringFalse + " is present on UI.\n");
						}
						Thread.sleep(2000);

					}	else {
						exexMsg="Text path \"" + textXpathFalse + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Text path \"" + textXpathFalse + "\" not found.\n");
						return false;
					}
					}
					break;



				case "VALIDATE_ATTR_VALUE":
					String textAttrXpath = null;
					String textAttrString = null;
					String textAttrName = null;
					if(firstCellVal.trim().length() > 0)
					{
						textAttrXpath = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						textAttrString = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}

					if(thirdCellVal.trim().length() > 0)
					{
						textAttrName = thirdCellVal;
					}	else {
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Validating text " + textAttrString + " for element " + textAttrXpath);
					boolean IstextAttrXpathId = IsFindElementById(textAttrXpath);
					if(IstextAttrXpathId)
					{
						if (driver.findElements(By.id(textAttrXpath)).size() != 0) {
							if (driver.findElement(By.id(textAttrXpath)).getAttribute(textAttrName).trim().equals(textAttrString)) {
								log(logBuffer, testcaseWorksheet + ": Text " + textAttrString	+ " verified on UI.\n");
							}	else {
								exexMsg="Text " + textAttrString	+ " couldn't be verified on UI.";
								log(logBuffer, testcaseWorksheet + ": Text " + textAttrString	+ " couldn't be verified on UI.\n");
								return false;
							}

							Thread.sleep(2000);

						} else {
							exexMsg="Text \"" + textAttrXpath + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Text \"" + textAttrXpath + "\" not found.\n");
							return false;
						}
					}
					else
					{
					if (driver.findElements(By.xpath(textAttrXpath)).size() != 0) {
						if (driver.findElement(By.xpath(textAttrXpath)).getAttribute(textAttrName).trim().equals(textAttrString)) {
							log(logBuffer, testcaseWorksheet + ": Text " + textAttrString	+ " verified on UI.\n");
						}	else {
							exexMsg="Text " + textAttrString	+ " couldn't be verified on UI.";
							log(logBuffer, testcaseWorksheet + ": Text " + textAttrString	+ " couldn't be verified on UI.\n");
							return false;
						}

						Thread.sleep(2000);

					} else {
						exexMsg="Text \"" + textAttrXpath + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Text \"" + textAttrXpath + "\" not found.\n");
						return false;
					}
					}
					break;

				case "ISELEMENTNOTEDITABLE":
					//This is to verify Read Only input fields
					String eleNotEditableXpath = null;
					if(firstCellVal.trim().length() > 0)
					{
						eleNotEditableXpath = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					boolean IseleNotEditableXpathId = IsFindElementById(eleNotEditableXpath);
					if(IseleNotEditableXpathId)
					{
						if (driver.findElements(By.id(eleNotEditableXpath)).size() != 0) {
							String className = driver.findElement(
									By.id(eleNotEditableXpath))
									.getAttribute("class");
							log(logBuffer, className);
							if (className.toUpperCase().contains("NONEDITABLE")) {
								log(logBuffer, testcaseWorksheet + ": Pass: Element " + eleNotEditableXpath
										+ " is non editable on UI.\n");
							} else {
								exexMsg="Element " + eleNotEditableXpath	+ " is editable on UI.";
								log(logBuffer, testcaseWorksheet + ": Fail: Element " + eleNotEditableXpath	+ " is editable on UI.\n");
								return false;
							}

						} else {
							log(logBuffer, testcaseWorksheet + ": Element " + eleNotEditableXpath
									+ " not found on UI.\n");

						}
					}	else {
					if (driver.findElements(By.xpath(eleNotEditableXpath)).size() != 0) {
						String className = driver.findElement(
								By.xpath(eleNotEditableXpath))
								.getAttribute("class");
						log(logBuffer, className);
						if (className.toUpperCase().contains("NONEDITABLE")) {
							log(logBuffer, testcaseWorksheet + ": Pass: Element " + eleNotEditableXpath + " is non editable on UI.\n");
						} else {
							exexMsg="Element " + eleNotEditableXpath	+ " is editable on UI";
							log(logBuffer, testcaseWorksheet + ": Fail: Element " + eleNotEditableXpath + " is editable on UI.\n");
							return false;
						}

					} else {
						exexMsg="Element " + eleNotEditableXpath	+ " not found on UI";
						log(logBuffer, testcaseWorksheet + ": Element " + eleNotEditableXpath	+ " not found on UI.\n");
							return false;
					}
					}
					break;

				case "GET_ATTRIBUTE":
					String getTextattr1Xpath = null;
					String attributename= null;
					if(firstCellVal.trim().length() > 0)
					{
						getTextattr1Xpath = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(secondCellVal.trim().length() > 0)
					{
						attributename = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					boolean IsgetTextattr1XpathId = IsFindElementById(getTextattr1Xpath);
					if(IsgetTextattr1XpathId)
					{
						if (driver.findElements(By.xpath(getTextattr1Xpath)).size() != 0) {
							log(logBuffer,
									"Text found is - "
											+ driver.findElement(
													By.xpath(getTextattr1Xpath)).getAttribute(attributename) + "\n");
						}	else {
							exexMsg="Text path \"" + getTextattr1Xpath	+ "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Text path \"" + getTextattr1Xpath	+ "\" not found.\n");
							return false;
						}
					}	else {
					if (driver.findElements(By.xpath(getTextattr1Xpath)).size() != 0) {
						log(logBuffer,"Text found is - " + driver.findElement(By.xpath(getTextattr1Xpath)).getAttribute(attributename) + "\n");
					}
					else {
						exexMsg="Text path \"" + getTextattr1Xpath+ "\" not found";
						log(logBuffer, testcaseWorksheet + ": Text path \"" + getTextattr1Xpath+ "\" not found.\n");
						return false;
					}
					}
					break;

				case "CHECKENTITY":
					String sql = null;
					String output = null;
					ResultSet rs = null;
					if(firstCellVal.trim().length() > 0)
					{
						if(firstCellVal.contains("/")) {
							String[] dbProp = firstCellVal.split("/");
							userName = dbProp[0].trim();
							userPassword = dbProp[1].trim();
							DBTNS = dbProp[2].trim();
							DbServer = dbProp[3].trim();
							DBPort = dbProp[4].trim();
							sql = dbProp[5].trim();
						} else {
							sql = firstCellVal;
						}

					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					log(logBuffer, testcaseWorksheet + ": Verifying Database with SQL " + sql	+ "\n");

					try {
						DBUtil db = new DBUtil(DbServer, DBPort, DBTNS, userName, userPassword);
						Connection connect = db.getConnection();
						statement = connect.createStatement();

						rs = statement.executeQuery(sql);
						while (rs.next())
							output = rs.getString(0);
						if (output != null) {
							log(logBuffer, output);
						} else {
							exexMsg="No result returned by SQL";
							log(logBuffer, testcaseWorksheet + ": No result returned by SQL\n");
						}
					} catch(Exception e) {
						exexMsg="DB Connection error";
						log(logBuffer, testcaseWorksheet + ": DB Connection Error\n");
					} finally {
					    if (statement != null) { statement.close(); }
					}

					break;

				case "EXIT_ON_DBCHECKTRUE":
					String sqlChkTrue = null;
					String outputChkTrue = null;
					ResultSet rsChkTrue = null;
					if(firstCellVal.trim().length() > 0)
					{
						if(firstCellVal.contains("/")) {
							String[] dbProp = firstCellVal.split("/");
							userName = dbProp[0].trim();
							userPassword = dbProp[1].trim();
							DBTNS = dbProp[2].trim();
							DbServer = dbProp[3].trim();
							DBPort = dbProp[4].trim();
							sqlChkTrue = dbProp[5].trim();
						} else {
							sqlChkTrue = firstCellVal;
						}
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Verifying Database with SQL " + sqlChkTrue + "\n");

					try {
						DBUtil db = new DBUtil(DbServer, DBPort, DBTNS, userName, userPassword);
						Connection connect = db.getConnection();
						statement = connect.createStatement();

						rsChkTrue = statement.executeQuery(sqlChkTrue);

						while (rsChkTrue.next())
							outputChkTrue = rsChkTrue.getString(1);
						if (outputChkTrue == null)	{
							exexMsg="No records found, continuing.";
							log(logBuffer, testcaseWorksheet + ": No records found, continuing.\n");
						}	else {
							log(logBuffer, testcaseWorksheet + ": Record found, exiting.\n");
							return true;
						}
					} catch(Exception e) {
						exexMsg="DB Connection error";
						log(logBuffer, testcaseWorksheet + ": DB Connection Error\n");
					} finally {
					    if (statement != null) { statement.close(); }
					}
					break;

				case "EXIT_ON_DBCHECKFALSE":
					String sqlChkFalse = null;
					ResultSet rsChkFalse = null;
					String outputChkFalse = null;
					if(firstCellVal.trim().length() > 0)
					{
						if(firstCellVal.contains("/")) {
							String[] dbProp = firstCellVal.split("/");
							userName = dbProp[0].trim();
							userPassword = dbProp[1].trim();
							DBTNS = dbProp[2].trim();
							DbServer = dbProp[3].trim();
							DBPort = dbProp[4].trim();
							sqlChkFalse = dbProp[5].trim();
						} else {
							sqlChkFalse = firstCellVal;
						}
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Verifying Database with SQL " + sqlChkFalse	+ "\n");
					try {
						DBUtil db = new DBUtil(DbServer, DBPort, DBTNS, userName, userPassword);
						Connection connect = db.getConnection();
						statement = connect.createStatement();
						rsChkFalse = statement.executeQuery(sqlChkFalse);

						while (rsChkFalse.next())
							outputChkFalse = rsChkFalse.getString(1);
						if (outputChkFalse != null)
							log(logBuffer, testcaseWorksheet + ": Record found, continuing.\n");
						else {
							exexMsg="No records found, exiting.";
							log(logBuffer, testcaseWorksheet + ": No records found, exiting.\n");
							return false;
						}
					} catch(Exception e) {
						exexMsg="DB Connection error";
						log(logBuffer, testcaseWorksheet + ": DB Connection Error\n");
					} finally {
					    if (statement != null) { statement.close(); }
					}
					break;

				case "GET_DB_DATA":
					// will get first value from DB based on the query.
					String sqlString = null;
					String pushReqDB = null;
					ResultSet dbResult = null;
					String dbOutput = null;
					if(firstCellVal.trim().length() > 0)
					{
						if(firstCellVal.contains("/")) {
							String[] dbProp = firstCellVal.split("/");
							userName = dbProp[0];
							userPassword = dbProp[1].trim();
							DBTNS = dbProp[2].trim();
							DbServer = dbProp[3].trim();
							DBPort = dbProp[4].trim();
							sqlString = dbProp[5].trim();
						} else {
							sqlString = firstCellVal;
						}
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						pushReqDB = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Fetching data from Database using SQL "	+ sqlString + "\n");

					try {
						DBUtil db = new DBUtil(DbServer, DBPort, DBTNS, userName, userPassword);
						Connection connect = db.getConnection();
						statement = connect.createStatement();
						dbResult = statement.executeQuery(sqlString);

						while (dbResult.next())
							dbOutput = dbResult.getString(1);
						if (dbOutput != null)
							log(logBuffer, testcaseWorksheet + ": Record found. Value = " + dbOutput + "\n");
						if (pushReqDB.toUpperCase().contains("PUSH,")) {
							stackPush(logBuffer, pushReqDB, dbOutput);
						} else {
							exexMsg="No matching record found for given query exiting.";
							log(logBuffer, testcaseWorksheet + ": No matching record found for given query exiting.\n");
							return false;
						}
					} catch(Exception e) {
						e.printStackTrace();
						exexMsg="DB Connection error";
						log(logBuffer, testcaseWorksheet + ": DB Connection Error\n");
					} finally {
					    if (statement != null) { statement.close(); }
					}
					break;

				case "SHEET":
					String sheetValue = null;
					if(firstCellVal.trim().length() > 0)
					{
						sheetValue = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Executing testcase - " + sheetValue + "\n");
					test_engine(logBuffer, workbookPath, sheetValue,null);

					if (test_engine(logBuffer, workbookPath,sheetValue,null) == false) {
						//reportBuffer.write("Testcase " + workbookPath + "." + sheetValue + " failed.\n");
						exexMsg="Testcase " + workbookPath + ","+ sheetValue + " failed.";
						log(logBuffer, testcaseWorksheet + ": Testcase " + workbookPath + ","+ sheetValue + " failed.\n");
						setTestFail();
						System.gc();
					} else {
						//reportBuffer.write("Testcase " + workbookPath + "."+ sheetValue + " passed.\n");
						log(logBuffer, testcaseWorksheet + ": Testcase " + workbookPath + "," + sheetValue + " passed.\n");
						setTestPass();
						System.gc();
					}
					break;

				case "SKIPSTEP": // can be used to assign variables in sheet
					System.out.println();
					break;

				case "WAITONVISIBLE":
					String elementID = null;
					if(firstCellVal.trim().length() > 0)
					{
						elementID = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					int timeOut = Integer.parseInt(secondCellVal);
					log(logBuffer, testcaseWorksheet + ": Waiting for " + timeOut + " on " + elementID + "\n");
					try {
						boolean IselementIDId = IsFindElementById(elementID);
						if(IselementIDId)
						{
						waitOnElementVisible(By.id(elementID), timeOut);
						}	else {
						waitOnElementVisible(By.xpath(elementID), timeOut);
						}
					} catch(Exception e){
						exexMsg="Timed out after " + timeOut + " seconds waiting for visibility of element " + elementID;
						log(logBuffer, testcaseWorksheet + ": Timed out after " + timeOut + " seconds waiting for visibility of element " + elementID);
						return false;
					}
					break;

				case "WAITONCLICKABLE":
					String elementID1 = null;
					if(firstCellVal.trim().length() > 0)
					{
						elementID1 = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Waiting for element " + elementID1	+ " to be clickable \n");
					int timeOut1 = Integer.parseInt(secondCellVal);
					try {
						boolean IselementID1Id = IsFindElementById(elementID1);
						if(IselementID1Id)
						{
							waitOnElementVisible(By.id(elementID1), timeOut1);
						}	else {
							waitOnElementVisible(By.xpath(elementID1), timeOut1);
						}
					} catch(Exception e){
						exexMsg="Timed out after " + timeOut1 + " seconds waiting for visibility of element " + elementID1;
						log(logBuffer, testcaseWorksheet + ": Timed out after " + timeOut1 + " seconds waiting for visibility of element " + elementID1);
						return false;
					}

					break;

				case "PARENTFOLDER":
					String xpathParent = null;
					if(firstCellVal.trim().length() > 0)
					{
						xpathParent = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Expanding parent folder " + xpathParent + "\n");
					boolean IsxpathParentId = IsFindElementById(xpathParent);
					if(IsxpathParentId)
					{
						if (driver.findElements(By.id(xpathParent)).size() != 0) {
							String className = driver.findElement(
									By.id(xpathParent)).getAttribute("class");
							log(logBuffer, className + "\n");
							if (className.contains("collapsed"))
								driver.findElement(By.id(xpathParent)).click();
							Thread.sleep(5000);
						} else {
							exexMsg="Parent folder \"" + xpathParent + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Parent folder \"" + xpathParent + "\" not found.\n");
							return false;
						}
					}	else {
					if (driver.findElements(By.xpath(xpathParent)).size() != 0) {
						String className = driver.findElement(
								By.xpath(xpathParent)).getAttribute("class");
						log(logBuffer, className + "\n");
						if (className.contains("collapsed"))
							driver.findElement(By.xpath(xpathParent)).click();
						Thread.sleep(5000);
					} else {
						exexMsg="Parent folder \"" + xpathParent	+ "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Parent folder \"" + xpathParent	+ "\" not found.\n");
						return false;
					}
					}
					break;

				case "MOUSEHOVER":
					String mouseElement = null;
					if(firstCellVal.trim().length() > 0)
					{
						mouseElement = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					Actions builder = new Actions(driver);
					log(logBuffer, testcaseWorksheet + ": Hovering over " + mouseElement + "\n");
					boolean IsmouseElementId = IsFindElementById(mouseElement);
					if(IsmouseElementId)
					{
						if (driver.findElements(By.id(mouseElement)).size() != 0) {
							builder.moveToElement(
									driver.findElement(By.id(mouseElement)))
									.build().perform();
							Thread.sleep(2000);
						} else {
							exexMsg="Text path \"" + mouseElement	+ "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Text path \"" + mouseElement	+ "\" not found.\n");
							return false;
						}
					}	else {
					if (driver.findElements(By.xpath(mouseElement)).size() != 0) {
						builder.moveToElement(
								driver.findElement(By.xpath(mouseElement)))
								.build().perform();
						Thread.sleep(2000);
					} else {
						exexMsg="Text path \"" + mouseElement + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Text path \"" + mouseElement + "\" not found.\n");
						return false;
					}
					}
					break;

				case "IFTEXTMATCHTHENELSE":
					/*
					 * Compares text on UI with input. needs keyword, if, then
					 * and else if needs identity and value then/else accepts
					 * identity as sheet then sheet name as value then/else
					 * accepts identity as log then log text as value input for
					 * then/else can be left blank
					 */
					String ifTextMatchThenElseXpath = null;
					String ifTextMatchThenElseText = null;
					if(firstCellVal.trim().length() > 0)
					{
						ifTextMatchThenElseXpath = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						ifTextMatchThenElseText = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Validating text " + ifTextMatchThenElseText
							+ " for element " + ifTextMatchThenElseXpath);
					boolean IsifTextMatchThenElseXpathId = IsFindElementById(ifTextMatchThenElseXpath);
					if(IsifTextMatchThenElseXpathId)
					{
					if (driver.findElements(By.id(ifTextMatchThenElseXpath))
							.size() != 0) {
						if (driver
								.findElement(By.id(ifTextMatchThenElseXpath))
								.getText().equals(ifTextMatchThenElseText)) {
							log(logBuffer, testcaseWorksheet + ": Text \"" + ifTextMatchThenElseText
									+ "\" verified on UI.\n");
							cells = sheet.getRow(++row);// reading 'then' clause
							if (zeroCellVal.toUpperCase()
									.equals("THEN")) {
								String sheetValueIfThenCase = cells[1]
										.getContents();
								String sheetValueIfThenParam = cells[2]
										.getContents();
								if (!sheetValueIfThenParam.equals(null)) {
									if (sheetValueIfThenCase.toUpperCase()
											.equals("SHEET")) {
										log(logBuffer, testcaseWorksheet + ": Executing sheet - "
												+ sheetValueIfThenParam + "\n");
										test_engine(logBuffer, workbookPath,sheetValueIfThenParam,null);
									}

									if (sheetValueIfThenCase.toUpperCase().equals("LOG")) {
										log(logBuffer, sheetValueIfThenParam + "\n");
									}

									if (sheetValueIfThenCase.toUpperCase().equals("SCREENSHOT")) {
										screenshot(driver, logBuffer,testSheetName,sheetValueIfThenParam);
									}
									if (sheetValueIfThenCase.toUpperCase().equals("RETURN")) {
										return sheetValueIfThenParam != null;
									}
									// ++row;
									return true;
								}
							} else {
								exexMsg="If condition should have 'then' clause. Please fix the script. Exit.";
								log(logBuffer,"If condition should have 'then' clause. Please fix the script. Exit.");
								return false;
							}
						} else {
							exexMsg="Text \"" + ifTextMatchThenElseText + "\" couldn't be verified on UI.";
							log(logBuffer, testcaseWorksheet + ": Text \"" + ifTextMatchThenElseText + "\" couldn't be verified on UI.\n");
						}
						cells = sheet.getRow(++row);// reading the 'then' row
						cells = sheet.getRow(++row);// reading the 'else' row
						if (zeroCellVal.toUpperCase().equals("ELSE")) {
							String sheetValueIfThenElseCase = cells[1]
									.getContents();
							String sheetValueIfThenElseParam = cells[2]
									.getContents();
							if (!sheetValueIfThenElseParam.equals(null)) {
								if (sheetValueIfThenElseCase.toUpperCase()
										.equals("SHEET")) {
									log(logBuffer, testcaseWorksheet + ": Executing sheet - "
											+ sheetValueIfThenElseParam + "\n");
									test_engine(logBuffer, workbookPath,sheetValueIfThenElseParam,null);
								}

								if (sheetValueIfThenElseCase.toUpperCase()
										.equals("LOG")) {
									log(logBuffer, sheetValueIfThenElseParam
											+ "\n");
								}
								if (sheetValueIfThenElseCase.toUpperCase()
										.equals("SCREENSHOT")) {
									screenshot(driver, logBuffer, testSheetName,
											sheetValueIfThenElseParam);
								}
								if (sheetValueIfThenElseCase.toUpperCase()
										.equals("RETURN")) {
									return sheetValueIfThenElseParam != null;
								}
							}
						} else
							--row; // reverting row count when 'else' clause not
						// there.
					} else {
						exexMsg="Text \"" + ifTextMatchThenElseXpath	+ "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Text \"" + ifTextMatchThenElseXpath	+ "\" not found.\n");
						return false;
					}
					}	else {
						if (driver.findElements(By.xpath(ifTextMatchThenElseXpath))
								.size() != 0) {
							if (driver
									.findElement(By.xpath(ifTextMatchThenElseXpath))
									.getText().equals(ifTextMatchThenElseText)) {
								log(logBuffer, testcaseWorksheet + ": Text \"" + ifTextMatchThenElseText
										+ "\" verified on UI.\n");
								cells = sheet.getRow(++row);// reading 'then' clause
								if (zeroCellVal.toUpperCase()
										.equals("THEN")) {
									String sheetValueIfThenCase = cells[1]
											.getContents();
									String sheetValueIfThenParam = cells[2]
											.getContents();
									if (!sheetValueIfThenParam.equals(null)) {
										if (sheetValueIfThenCase.toUpperCase()
												.equals("SHEET")) {
											log(logBuffer, testcaseWorksheet + ": Executing sheet - "
													+ sheetValueIfThenParam + "\n");
											test_engine(logBuffer, workbookPath,sheetValueIfThenParam,null);
										}

										if (sheetValueIfThenCase.toUpperCase().equals("LOG")) {
											log(logBuffer, sheetValueIfThenParam + "\n");
										}

										if (sheetValueIfThenCase.toUpperCase().equals("SCREENSHOT")) {
											screenshot(driver, logBuffer,testSheetName,sheetValueIfThenParam);
										}
										if (sheetValueIfThenCase.toUpperCase().equals("RETURN")) {
											return sheetValueIfThenParam != null;
										}
										// ++row;
										return true;
									}
								} else {
									exexMsg="If condition should have 'then' clause. Please fix the script. Exit.";
									log(logBuffer,"If condition should have 'then' clause. Please fix the script. Exit.");
									return false;
								}
							} else {
								exexMsg="Text \"" + ifTextMatchThenElseText + "\" couldn't be verified on UI.";
								log(logBuffer, testcaseWorksheet + ": Text \"" + ifTextMatchThenElseText + "\" couldn't be verified on UI.\n");
							}
							cells = sheet.getRow(++row);// reading the 'then' row
							cells = sheet.getRow(++row);// reading the 'else' row
							if (zeroCellVal.toUpperCase().equals("ELSE")) {
								String sheetValueIfThenElseCase = cells[1]
										.getContents();
								String sheetValueIfThenElseParam = cells[2]
										.getContents();
								if (!sheetValueIfThenElseParam.equals(null)) {
									if (sheetValueIfThenElseCase.toUpperCase()
											.equals("SHEET")) {
										log(logBuffer, testcaseWorksheet + ": Executing sheet - "
												+ sheetValueIfThenElseParam + "\n");
										test_engine(logBuffer, workbookPath,sheetValueIfThenElseParam,null);
									}

									if (sheetValueIfThenElseCase.toUpperCase()
											.equals("LOG")) {
										log(logBuffer, sheetValueIfThenElseParam
												+ "\n");
									}
									if (sheetValueIfThenElseCase.toUpperCase()
											.equals("SCREENSHOT")) {
										screenshot(driver, logBuffer, testSheetName,
												sheetValueIfThenElseParam);
									}
									if (sheetValueIfThenElseCase.toUpperCase()
											.equals("RETURN")) {
										return sheetValueIfThenElseParam != null;
									}
								}
							} else
								--row; // reverting row count when 'else' clause not
							// there.
						} else {
							exexMsg="Text \"" + ifTextMatchThenElseXpath	+ "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Text \"" + ifTextMatchThenElseXpath	+ "\" not found.\n");
							return false;
						}
					}
					break;

				case "ISELEMENTPRESENT":
					String eleXpath = null;
					if(firstCellVal.trim().length() > 0)
					{
						eleXpath = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					boolean IseleXpathId = IsFindElementById(eleXpath);
					if(IseleXpathId)
					{
					if (isElementPresent(eleXpath)) {
						log(logBuffer, testcaseWorksheet + ": Element " + eleXpath + " present on UI.\n");
					} else {
						exexMsg="Element " + eleXpath + " not present on UI.";
						log(logBuffer, testcaseWorksheet + ": Fail: Element " + eleXpath + " not present on UI.\n");
						return false;
					}
					}	else {
						if (isElementPresent(eleXpath)) {
							log(logBuffer, testcaseWorksheet + ": Element " + eleXpath + " present on UI.\n");
						} else {
							exexMsg="Element " + eleXpath	+ " not present on UI.";
							log(logBuffer, testcaseWorksheet + ": Fail: Element " + eleXpath	+ " not present on UI.\n");
							return false;
						}
					}
					break;

				case "ISELEMENTNOTPRESENT":
					String elemXpath = null;
					if(firstCellVal.trim().length() > 0)
					{
						elemXpath = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					boolean IselemXpathId = IsFindElementById(elemXpath);
					if(IselemXpathId)
					{
						if (isElementPresent(elemXpath)) {
							log(logBuffer, testcaseWorksheet + ": Element " + elemXpath + " present on UI.\n");
						} else {
							exexMsg="Element " + elemXpath + " not present on UI.";
							log(logBuffer, testcaseWorksheet + ": Fail: Element " + elemXpath + " not present on UI.\n");
							return false;
						}
					}	else {
					if (!isElementPresent(elemXpath)) {
						log(logBuffer, testcaseWorksheet + ": Pass: Element " + elemXpath	+ " not present on UI.\n");
					} else {
						exexMsg="Element " + elemXpath	+ " present on UI";
						log(logBuffer, testcaseWorksheet + ": Fail: Element " + elemXpath	+ " present on UI.\n");
						return false;

					}
					}
					break;
				case "ISELEMENTENABLED":
					String eleEnabXpath = null;
					if(firstCellVal.trim().length() > 0)
					{
						eleEnabXpath = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					boolean IseleEnabXpathId = IsFindElementById(eleEnabXpath);
					if(IseleEnabXpathId)
					{
						if (driver.findElements(By.id(eleEnabXpath)).size() != 0) {
							String className = driver.findElement(
									By.id(eleEnabXpath)).getAttribute("class");
							log(logBuffer, className);
							if (className.toUpperCase().contains("DISABLED")) {
								exexMsg="Element " + eleEnabXpath + " is disabled on UI.";
								log(logBuffer, testcaseWorksheet + ": Fail: Element " + eleEnabXpath + " is disabled on UI.\n");
								return false;
							} else
								log(logBuffer, testcaseWorksheet + ": Pass: Element " + eleEnabXpath + " is enabled on UI.\n");
						} else {
							log(logBuffer, testcaseWorksheet + ": Element " + eleEnabXpath + " not found on UI.\n");
						}
					}
					else
					{
					if (driver.findElements(By.xpath(eleEnabXpath)).size() != 0) {
						String className = driver.findElement(
								By.xpath(eleEnabXpath)).getAttribute("class");
						log(logBuffer, className);
						if (className.toUpperCase().contains("DISABLED")) {
							exexMsg="Element " + eleEnabXpath	+ " is disabled on UI.";
							log(logBuffer, testcaseWorksheet + ": Fail: Element " + eleEnabXpath	+ " is disabled on UI.\n");
							return false;
						} else
							log(logBuffer, testcaseWorksheet + ": Pass: Element " + eleEnabXpath	+ " is enabled on UI.\n");
					} else {
						exexMsg="Element " + eleEnabXpath + " not found on UI.";
						log(logBuffer, testcaseWorksheet + ": Element " + eleEnabXpath + " not found on UI.\n");
						return false;
					}
					}
					break;

				case "ISELEMENTNOTENABLED":
					String eleNotEnabXpath = null;
					if(firstCellVal.trim().length() > 0)
					{
						eleNotEnabXpath = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					boolean IseleNotEnabXpathId = IsFindElementById(eleNotEnabXpath);
					if(IseleNotEnabXpathId)
					{
						if (driver.findElements(By.id(eleNotEnabXpath)).size() != 0) {
							String className = driver.findElement(
									By.id(eleNotEnabXpath))
									.getAttribute("class");
							log(logBuffer, className);
							if (className.toUpperCase().contains("DISABLED")) {
								log(logBuffer, testcaseWorksheet + ": Pass: Element " + eleNotEnabXpath
										+ " is disabled on UI.\n");
							} else {
								exexMsg="Element " + eleNotEnabXpath + " is enabled on UI.";
								log(logBuffer, testcaseWorksheet + ": Fail: Element " + eleNotEnabXpath + " is enabled on UI.\n");
								return false;
							}

						} else {
							log(logBuffer, testcaseWorksheet + ": Element " + eleNotEnabXpath
									+ " not found on UI.\n");

						}
					}
					else
					{
					if (driver.findElements(By.xpath(eleNotEnabXpath)).size() != 0) {
						String className = driver.findElement(
								By.xpath(eleNotEnabXpath))
								.getAttribute("class");
						log(logBuffer, className);
						if (className.toUpperCase().contains("DISABLED")) {
							log(logBuffer, testcaseWorksheet + ": Pass: Element " + eleNotEnabXpath + " is disabled on UI.\n");
						} else {
							exexMsg="Element " + eleNotEnabXpath + " is enabled on UI.";
							log(logBuffer, testcaseWorksheet + ": Fail: Element " + eleNotEnabXpath + " is enabled on UI.\n");
							return false;
						}

					} else {
						exexMsg="Element " + eleNotEnabXpath	+ " not found on UI.";
						log(logBuffer, testcaseWorksheet + ": Element " + eleNotEnabXpath	+ " not found on UI.\n");
						return false;
					}
					}
					break;

				case "SET_DATE_PICKER_VALUE":
					buttonName = null;
					String dateVal = null;
					if(firstCellVal.trim().length() > 0)
					{
						buttonName = firstCellVal;
					}
					if(secondCellVal.trim().length() > 0)
					{
						dateVal = secondCellVal;
					}
					else
					{
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					int dd = Integer.parseInt(dateVal);
					//int dd = Integer.parseInt(dateVal.substring(0, 2));
					//int mm = Integer.parseInt(dateVal.substring(3, 5));
					//int yy = Integer.parseInt(dateVal.substring(6, dateVal.length()));

					int rownum = 2;
					int colnum = 7;

					if(dd > 1 && dd < 9)
					{
						rownum = 3;
						colnum = dd-1;
					}
					else if(dd > 8 && dd < 16)
					{
						rownum = 4;
						colnum = dd-8;
					}
					else if(dd > 15 && dd < 23)
					{
						rownum = 5;
						colnum = dd-15;
					}
					else if(dd > 22 && dd < 30)
					{
						rownum = 6;
						colnum = dd-22;
					}
					else if(dd > 29 && dd < 32)
					{
						rownum = 7;
						colnum = dd-29;
					}


					buttonName = buttonName + "/div[2]/div[3]/table/tbody/tr["+rownum+"]/td["+colnum+"]";
					log(logBuffer, testcaseWorksheet + ": Clicking on Date Picker \"" + buttonName + "\".\n");
					boolean IsbuttonNameId1 = IsFindElementById(buttonName);
					if(IsbuttonNameId1)
					{
						if (driver.findElements(By.id(buttonName)).size() != 0) {
							driver.findElement(By.id(buttonName)).click();
							Thread.sleep(2000);
						} else {
							exexMsg="Date Picker \"" + buttonName + "\" not found.";
							log(logBuffer, testcaseWorksheet + ": Date Picker \"" + buttonName + "\" not found.\n");
							return false;
						}
					}	else {
					if (driver.findElements(By.xpath(buttonName)).size() != 0) {
						driver.findElement(By.xpath(buttonName)).click();
						Thread.sleep(2000);
					} else {
						exexMsg="Date Picker \"" + buttonName + "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Date Picker \"" + buttonName + "\" not found.\n");
						return false;
					}
					}
					break;


				case "ISELEMENTSELECTED":
					String eleSelected = null;
					if(firstCellVal.trim().length() > 0)
					{
						eleSelected = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					boolean IseleSelectedId = IsFindElementById(eleSelected);
					if(IseleSelectedId)
					{
						if(driver.findElement(By.id(eleSelected)).isSelected())
						{
							log(logBuffer,"Element " + eleSelected + " is Selected on UI.\n");
						}	else {
							exexMsg="Element " + eleSelected + " is not Selected on UI.";
							log(logBuffer,"Fail: Element " + eleSelected + " is not Selected on UI.\n");
							return false;
						}
					}	else {

					if(driver.findElement(By.xpath(eleSelected)).isSelected())
					{
						log(logBuffer,"Element " + eleSelected + " is Selected on UI.\n");
					}	else {
						exexMsg="Element " + eleSelected + " is not Selected on UI.";
						log(logBuffer,"Fail: Element " + eleSelected + " is not Selected on UI.\n");
						return false;
					}
					}
					break;
				case "ISELEMENTNOTSELECTED":
					String eleNotSelected = null;
					if(firstCellVal.trim().length() > 0)
					{
						eleNotSelected = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					boolean IseleNotSelectedId = IsFindElementById(eleNotSelected);
					if(IseleNotSelectedId)
					{
						if(driver.findElement(By.id(eleNotSelected)).isSelected())
						{
							exexMsg="Element " + eleNotSelected + " is  Selected on UI.";
							log(logBuffer,"Fail: Element " + eleNotSelected + " is  Selected on UI.\n");
							return false;
						}	else {
							log(logBuffer,"Element " + eleNotSelected + " is not Selected on UI.\n");

						}
					}	else {

					if(driver.findElement(By.xpath(eleNotSelected)).isSelected())
					{
						exexMsg="Element " + eleNotSelected + " is  Selected on UI.";
						log(logBuffer,"Fail: Element " + eleNotSelected + " is  Selected on UI.\n");
						return false;
					}	else {
						log(logBuffer,"Element " + eleNotSelected + " is not Selected on UI.\n");

					}
					}
					break;

				case "FOR":
					String forSheet = null;
					int forCount = 1;
					if(firstCellVal.trim().length() > 0)
					{
						forSheet = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						forCount = Integer.parseInt(secondCellVal);
					}	else {
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					for (int i = 0; i < forCount; i++)
						test_engine(logBuffer, workbookPath, forSheet,null);
				case "LOGIN":
					String user_name = null;
					String user_password = null;
					if(firstCellVal.trim().length() > 0)
					{
						user_name = firstCellVal;
					}	else {
						exexMsg="User name Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						user_password = secondCellVal;
					}	else {
						exexMsg="Login Password Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					try {
					driver.findElement(By.name("USER_NAME")).clear();
						driver.findElement(By.name("USER_NAME")).sendKeys(user_name);
						driver.findElement(By.name("PASSWORD")).clear();
						driver.findElement(By.name("PASSWORD")).sendKeys(user_password);
					} catch (Exception e)
					{
						exexMsg="Application URL not Valid";
						log(logBuffer, testcaseWorksheet + ": Application URL not Valid");
						return false;
					}

					if (driver.findElements(By.cssSelector("span.loginInner")).size() != 0) {
						driver.findElement(By.cssSelector("span.loginInner")).click();
					}	else {
						driver.findElement(By.cssSelector("span.buttonInner")).click();
					}

					if (driver.findElements(By.name("USER_NAME")).size() != 0) {
						exexMsg="Application login failed";
						log(logBuffer, testcaseWorksheet + ": Application login failed");
						return false;
					}
					log(logBuffer, testcaseWorksheet + ": Logged in as " + user_name + "\n");

					break;
				case "LOGOUT":
					driver.switchTo().defaultContent();
					if (driver.findElements(
							By.xpath("//*[@id='shellUsername']")).size() != 0) {
						driver.findElement(By.xpath("//*[@id='shellUsername']")).click();
						driver.findElement(
								By.xpath("//*[@id='shellLogoutAction']"))
								.click();
						Thread.sleep(2000);
					} else {
						exexMsg="Button \"" + "//*[@id='shellUsername']"	+ "\" not found.";
						log(logBuffer, testcaseWorksheet + ": Button \"" + "//*[@id='shellUsername']"	+ "\" not found.\n");
						return false;
					}
					break;

				case "DATADRIVEN":  //getting test sheet name and workbook with path from which data has to be picked up.
					//data should be present in sheet with same name as of test sheet
					// data driven sheet has to be called from another sheet.
					String tcSheetName = null;
					String dataWBPath = null;
					Sheet dataSheet = null;
					FileInputStream ddWBStream = null;
					if(firstCellVal.trim().length() > 0)
					{
						tcSheetName = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					if(secondCellVal.trim().length() > 0)
					{
						dataWBPath = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}

					File fileWB = new File (dataWBPath);
					if(fileWB.exists())
					{
						ddWBStream = new FileInputStream(dataWBPath);

					}	else {
						exexMsg="Workbook " + dataWBPath + "not found";
						log(logBuffer, testcaseWorksheet + ": Workbook " + dataWBPath + "not found");
						return false;
					}
					Workbook dataWB = Workbook.getWorkbook(ddWBStream);
					if(dataWB.getSheet(tcSheetName) != null)
					{
						dataSheet = dataWB.getSheet(tcSheetName);
					}	else {
						exexMsg="Data sheet \"" + tcSheetName + "\" not found in workbook " + dataWBPath + ".";
						log(logBuffer, testcaseWorksheet + ": Data sheet \"" + tcSheetName + "\" not found in workbook " + dataWBPath + ".\n");
						return false;
					}
					Cell[] dataCells = dataSheet.getRow(0);
					int arrRow = dataSheet.getRows();
					int arrCol = dataCells.length;
					String [][] testdataArray = new String[arrRow][arrCol];
					for (int xlRow = 1; xlRow < arrRow; xlRow++)
					{
						for (int xlCol = 0; xlCol < arrCol; xlCol++)
						{
							if(dataSheet.getRow(xlRow).length == 0)
								break; //breaking inner for once empty row found in data sheet

							dataCells = dataSheet.getRow(xlRow);
							testdataArray[xlRow][xlCol] = dataCells[xlCol].getContents();
						}
						if (dataSheet.getRow(xlRow).length == 0)
						{//breaking inner for once empty row found in data sheet
							arrRow=xlRow;//resetting arrRow based on continuous
							//non empty rows detected in data sheet
							break;
						}
					}
					for (int runCount = 1 ; runCount < arrRow; runCount++)
					{
						setDataArrRow(runCount);
						test_engine(logBuffer, workbookPath,tcSheetName,testdataArray);
					}
					ddWBStream.close();
					dataWB.close();
					dataSheet =null;
					break;

				case "ALERTOK" :
					driver.switchTo().alert().accept();
					break;
				case "JS_WINDOW":((JavascriptExecutor)driver).executeScript("window.showModalDialog = function( sURL,vArguments, sFeatures) { window.open(sURL, 'modal', sFeatures); }");
				break;

				case "CHILDWINDOW":
					Thread.sleep(2000);
					//driver.switchTo().window("Message");
					String winHandleBefore = driver.getWindowHandle();
					for(String winHandle : driver.getWindowHandles()){
						//System.out.println(winHandle);
						//System.out.println(driver.switchTo().window(winHandle).getTitle());
						if(driver.switchTo().window(winHandle).getTitle().equalsIgnoreCase("Message"))
						{
							//System.out.println("yo yo "+driver.switchTo().window(winHandle).getTitle());
							driver.switchTo().window(winHandle);
							break;
						}
					}
					System.out.println(driver.getTitle());
					//driver.switchTo().window("Message");

					driver.findElement(By.xpath("//*[@id='yes']/span")).click();
					Thread.sleep(2000);
					//driver.close();
					driver.switchTo().window(winHandleBefore);
					System.out.println(driver.getTitle());
					System.out.println(driver.getPageSource().toString());
					//System.out.println(driver.getPageSource().toString());
					//driver.close();
					//Switch back to original browser (first window)
					//driver.switchTo().window(winHandleBefore);

					//Thread.sleep(2000);
					break;

					/*
					//driver.switchTo().alert();
					// String parentWindowHandle = driver.getWindowHandle(); //
					// save the current window handle.
					// driver.switchTo().window(driver.window_handles.last);
					//WebDriver popup = null;
					// Iterator<String> windowIterator =
					// (Iterator<java.lang.String>) driver.getWindowHandles();
					// while(windowIterator.hasNext()) {
					// String windowHandle = windowIterator.next();

					System.out.println(driver.getPageSource().toString());
					popup = (WebDriver) driver.switchTo().activeElement();
					if (popup.getTitle().equals("Message")) {
						System.out.println("found");
						break;
					}*/
				case "FILEEXIST":
					String filePath = values[10] + firstCellVal;
					//String inText = secondCellVal; //to search given text in above file.
					log(logBuffer, testcaseWorksheet + ": Verifying existence of file " + filePath);
					File testFile = new File(filePath);
					if (testFile.exists()) {
						log(logBuffer, testcaseWorksheet + ": File " + filePath+ " existence verified.\n");
					} else {
						exexMsg="File " + filePath+ " existence not verified.";
						log(logBuffer, testcaseWorksheet + ": File " + filePath+ " existence not verified.\n");
						return false;
					}
					break;
				case "RIGHT_CLICK":
					String rightClickLoc = null;
					if(firstCellVal.trim().length() > 0)
					{
						rightClickLoc = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					Actions actions = new Actions(driver);
					try {
						boolean IsrightClickLocId = IsFindElementById(rightClickLoc);
						if(IsrightClickLocId)
						{
						actions.contextClick(driver.findElement(By.id(rightClickLoc))).build().perform();
						}	else {
						actions.contextClick(driver.findElement(By.xpath(rightClickLoc))).build().perform();
						}
					} catch(Exception e) {
						exexMsg="\"" + rightClickLoc+ "\" not found.";
						log(logBuffer, testcaseWorksheet + ": \"" + rightClickLoc+ "\" not found.\n");
						return false;
					}
					break;

				case "PAGE_REFRESH":
					driver.navigate().refresh();
					break;

				case "SENDKEYS":
					String keyValue=null;
					if(secondCellVal.trim().length() > 0)
					{
						keyValue = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Value";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Value");
						return false;
					}
					Actions keysBuilder = new Actions(driver);

					switch(keyValue.toUpperCase())
					{
					case "ENTER": keysBuilder.sendKeys(Keys.ENTER).build().perform(); break;
					case "TAB":   keysBuilder.sendKeys(Keys.TAB).build().perform(); break;
					case "ESCAPE":keysBuilder.sendKeys(Keys.ESCAPE).build().perform(); break;
					default: log(logBuffer,"Key " + keysBuilder + " is not a valid key.");
					}
					log(logBuffer,"Sending key " + keysBuilder + " to UI");
					Thread.sleep(2000);
					break;

				case "ISELEMENTVISIBLE":
					String eleVisible = null;
					if(firstCellVal.trim().length() > 0)
					{
						eleVisible = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					boolean IseleVisibleId = IsFindElementById(eleVisible);
					if(IseleVisibleId)
					{
						if(driver.findElement(By.id(eleVisible)).isDisplayed())
						{
							log(logBuffer,"Element " + eleVisible + " is Visible on UI.\n");
						}	else {
							exexMsg="Element " + eleVisible + " is not Visible on UI.";
							log(logBuffer,"Fail: Element " + eleVisible + " is not Visible on UI.\n");
							return false;
						}
					}	else {
					if(driver.findElement(By.xpath(eleVisible)).isDisplayed())
					{
						log(logBuffer,"Element " + eleVisible + " is Visible on UI.\n");
					}	else {
						exexMsg="Element " + eleVisible + " is not Visible on UI.";
						log(logBuffer,"Fail: Element " + eleVisible + " is not Visible on UI.\n");
						return false;
					}
					}
					break;
				case "ISELEMENTNOTVISIBLE":
					String eleNotVisible = null;
					if(firstCellVal.trim().length() > 0)
					{
						eleNotVisible = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}

					boolean IseleNotVisibleId = IsFindElementById(eleNotVisible);
					if(IseleNotVisibleId)
					{
						if(!driver.findElement(By.id(eleNotVisible)).isDisplayed())
						{
							log(logBuffer,"Pass: Element " + eleNotVisible + " is not Visible on UI.\n");
						}	else {
							exexMsg="Element " + eleNotVisible + " is  Visible on UI.";
							log(logBuffer,"Element " + eleNotVisible + " is  Visible on UI.\n");
							return false;


						}
					}	else {
						if(!driver.findElement(By.xpath(eleNotVisible)).isDisplayed())
						{
							log(logBuffer,"Pass: Element " + eleNotVisible + " is not Visible on UI.\n");
						}
						else
						{
							exexMsg="Element " + eleNotVisible + " is Visible on UI.";
							log(logBuffer,"Element " + eleNotVisible + " is  Visible on UI.\n");
							return false;
						}
					}
					break;

				case "EQUALS":
					String firstVar=null;
					String secondVar=null;
					if(firstCellVal.trim().length() > 0)
					{
						if (firstCellVal.toUpperCase().contains("POP,")) {
							firstCellVal = stackPop(logBuffer, firstCellVal);
						}
						firstVar = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(secondCellVal.trim().length() > 0)
					{
						if (secondCellVal.toUpperCase().contains("POP,")) {
							secondCellVal = stackPop(logBuffer, secondCellVal);
						}
						secondVar = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(isEqual(firstVar,secondVar)) {
						log(logBuffer,firstVar + " is equal to " + secondVar + ".\n");
					} else {
						exexMsg=firstVar + " is not equal to " + secondVar;
						log(logBuffer,firstVar + " is not equal to " + secondVar + ".\n");
						return false;
					}
					break;

				case "NOT_EQUALS":
					String firstVar1=null;
					String secondVar1=null;
					if(firstCellVal.trim().length() > 0)
					{
						if (firstCellVal.toUpperCase().contains("POP,")) {
							firstCellVal = stackPop(logBuffer, firstCellVal);
						}
						firstVar1 = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(secondCellVal.trim().length() > 0)
					{
						if (secondCellVal.toUpperCase().contains("POP,")) {
							secondCellVal = stackPop(logBuffer, secondCellVal);
						}
						secondVar1 = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(!(isEqual(firstVar1,secondVar1))) {
						log(logBuffer,firstVar1 + " is not equal to " + secondVar1 + ".\n");
					} else {
						exexMsg=firstVar1 + " is equal to " + secondVar1;
						log(logBuffer,firstVar1 + " is equal to " + secondVar1 + ".\n");
						return false;
					}
					break;

				case "GREATER_THAN":
					String str1=null;
					String str2=null;
					if(firstCellVal.trim().length() > 0)
					{
						if (firstCellVal.toUpperCase().contains("POP,")) {
							firstCellVal = stackPop(logBuffer, firstCellVal);
						}
						str1 = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(secondCellVal.trim().length() > 0)
					{
						if (secondCellVal.toUpperCase().contains("POP,")) {
							secondCellVal = stackPop(logBuffer, secondCellVal);
						}
						str2 = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(isNumber(str1) && isNumber(str2)) {
						double num1=Double.parseDouble(str1);
						double num2=Double.parseDouble(str2);
						if(num1 > num2) {
							log(logBuffer,str1 + " is greater than " + str2 + ".\n");
						} else {
							exexMsg=str1 + " is not greater than " + str2;
							log(logBuffer,str1 + " is not greater than " + str2 + ".\n");
							return false;
						}
					}
					else {
							exexMsg=str1 + " or " + str2 + " not a number";
							log(logBuffer,str1 + " or " + str2 + " not a number.\n");
							return false;
					}
					break;

				case "GREATER_THAN_EQUAL_TO":
					if(firstCellVal.trim().length() > 0)
					{
						if (firstCellVal.toUpperCase().contains("POP,")) {
							firstCellVal = stackPop(logBuffer, firstCellVal);
						}
						str1 = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(secondCellVal.trim().length() > 0)
					{
						if (secondCellVal.toUpperCase().contains("POP,")) {
							secondCellVal = stackPop(logBuffer, secondCellVal);
						}
						str2 = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(isNumber(str1) && isNumber(str2)) {
						double num1=Double.parseDouble(str1);
						double num2=Double.parseDouble(str2);
						if(num1 >= num2) {
							log(logBuffer,str1 + " is greater than or equal to " + str2 + ".\n");
						} else {
							exexMsg=str1 + " is not greater than or equal to " + str2;
							log(logBuffer,str1 + " is not greater than or equal to " + str2 + ".\n");
							return false;
						}
					}
					else {
							exexMsg=str1 + " or " + str2 + " : not a number";
							log(logBuffer,str1 + " or " + str2 + " : not a number.\n");
							return false;
					}
					break;

				case "LESS_THAN":
					if(firstCellVal.trim().length() > 0)
					{
						if (firstCellVal.toUpperCase().contains("POP,")) {
							firstCellVal = stackPop(logBuffer, firstCellVal);
						}
						str1 = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(secondCellVal.trim().length() > 0)
					{
						if (secondCellVal.toUpperCase().contains("POP,")) {
							secondCellVal = stackPop(logBuffer, secondCellVal);
						}
						str2 = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(isNumber(str1) && isNumber(str2)) {
						double num1=Double.parseDouble(str1);
						double num2=Double.parseDouble(str2);
						if(num1 < num2) {
							log(logBuffer,str1 + " is less than " + str2 + ".\n");
						} else {
							exexMsg=str1 + " is not less than " + str2;
							log(logBuffer,str1 + " is not less than " + str2 + ".\n");
							return false;
						}
					}
					else {
							exexMsg=str1 + " or " + str2 + " not a number";
							log(logBuffer,str1 + " or " + str2 + " not a number.\n");
							return false;
					}
					break;

				case "LESS_THAN_EQUAL_TO":
					if(firstCellVal.trim().length() > 0)
					{
						if (firstCellVal.toUpperCase().contains("POP,")) {
							firstCellVal = stackPop(logBuffer, firstCellVal);
						}
						str1 = firstCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(secondCellVal.trim().length() > 0)
					{
						if (secondCellVal.toUpperCase().contains("POP,")) {
							secondCellVal = stackPop(logBuffer, secondCellVal);
						}
						str2 = secondCellVal;
					}	else {
						exexMsg="Parameter missing: Identity";
						log(logBuffer, testcaseWorksheet + ": Parameter missing: Identity");
						return false;
					}
					if(isNumber(str1) && isNumber(str2)) {
						double num1=Double.parseDouble(str1);
						double num2=Double.parseDouble(str2);
						if(num1 <= num2) {
							log(logBuffer,str1 + " is less than or equal to " + str2 + ".\n");
						} else {
							exexMsg=str1 + " is not less than or equal to " + str2;
							log(logBuffer,str1 + " is not less than or equal to " + str2 + ".\n");
							return false;
						}
					}
					else {
							exexMsg=str1 + " or " + str2 + " not a number";
							log(logBuffer,str1 + " or " + str2 + " not a number.\n");
							return false;
					}
					break;

				default:
					log(logBuffer, testcaseWorksheet + ": Invalid option " + zeroCellVal.toUpperCase() + "\n");
					break;
				}// end of switch
			}// end of for containing switch
			//}//end of if, for sheet names
			//}//end of for iCount
		} catch (BiffException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log(logBuffer, testcaseWorksheet + ": Calling contigency plan sheet");
			test_engine(logBuffer, workbookPath, "contigencyPlan",null);
			return false;
		}
		return true;
	}

	public boolean IsFindElementById(String cmd)
	{
		boolean bool = false;
		if(cmd.startsWith("//"))
		{
			bool= false;
		}	else {
			bool = true;
		}

		return bool;

	}

	public void stackPush(BufferedWriter buffer, String pushIndexText,
			String pushValue) {
		log(buffer, "#PUSH");
		log(buffer, pushIndexText + ": " + pushValue + "\n");
		String[] pushIndex = pushIndexText.split(",");
		int Index = Integer.parseInt(pushIndex[1]);
		POP[Index] = pushValue;
	}

	public String stackPop(BufferedWriter buffer, String popString) {
		String[] popIndex = popString.split(",");
		int Index = Integer.parseInt(popIndex[1]);
		log(buffer, "#POP");
		log(buffer, popString + ": " + POP[Index] + "\n");
		return POP[Index];
	}

	//private Object String(Set<String> windowTitles) {
		// TODO Auto-generated method stub
		//return null;
	//}

	public WebElement waitOnElementVisible(By by, int timeOut) {
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		WebElement element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(by));
		return element;
	}

	public WebElement waitOnElementClickable(By by, int timeOut) {
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		WebElement element = wait.until(ExpectedConditions
				.elementToBeClickable(by));
		return element;
	}

	private WebElement Select(WebElement findElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@AfterClass
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	// intentionally made this function public
	public boolean isElementPresent(String xpath) {
		if (driver.findElements(By.xpath(xpath)).size() != 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isElementEnabled(String xpath) {

		// WebElement button= driver.findElement(By.xpath(xpath));
		if (driver.findElement(By.xpath(xpath)).isEnabled()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	private String closeAlertAndGetItsText() {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			if (acceptNextAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			return alertText;
		} finally {
			acceptNextAlert = true;
		}
	}

	public void doubleClick(WebElement element) {
		try {
			Actions action = new Actions(driver).doubleClick(element);
			action.build().perform();

			System.out.println("Double clicked the element");
		} catch (StaleElementReferenceException e) {
			System.out.println("Element is not attached to the page document "
					+ e.getStackTrace());
		} catch (NoSuchElementException e) {
			System.out.println("Element " + element + " was not found"
					+ e.getStackTrace());
		} catch (Exception e) {
			System.out.println("Element " + element + " was not clickable "
					+ e.getStackTrace());
		}
	}
}
