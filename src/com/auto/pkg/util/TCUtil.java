package com.auto.pkg.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import jxl.Sheet;
import jxl.Workbook;


public class TCUtil {
	Date date = new Date();
	DateFormat dt= new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	DateFormat execDate= new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	public String d=dt.format(date).toString();
	String dateWithSpace=execDate.format(date);
	String dateWithoutSpace=dateWithSpace.replace(' ','_');
	String validFileString=dateWithoutSpace.replace(':','-');
	static StringBuffer htmlReportInitialize=new StringBuffer();
	static StringBuffer htmlReportResult=new StringBuffer();
	static StringBuffer htmlReportLines=new StringBuffer();
	static StringBuffer htmlReportClose=new StringBuffer();
	public String fileName;
	static int validTestCnt=0;
	static boolean notInuse=true;

	public boolean leftNav(WebDriver driver, String linkName, String tabTitle) throws InterruptedException
	  {
		  if(isElementPresent(driver, By.linkText(linkName)))
		  driver.findElement(By.linkText(linkName)).click();
		  Thread.sleep(2000);
		  driver.switchTo().frame(driver.findElement(By.id("appFrame")));
		  if (driver.findElement(By.id("breadcrumbsContent")).getText().equals(tabTitle))
		  {
			  System.out.println("Pass: " + linkName + " link presence verified in left navigation.");
			  driver.switchTo().defaultContent();
			  Thread.sleep(2000);
			  return true;
		  }
		  else
		  {
			  System.out.println("Fail: " + linkName + " link presence can't be verified in left navigation");
			  driver.switchTo().defaultContent();
			  Thread.sleep(500);
			  return false;
		  }
	  }
	  public boolean checkEntity(Statement statement, String tableName, String colName, String entityName) throws SQLException
	  {
		  String sql = "Select * from " + tableName + " where " + colName +"='" + entityName + "'";
		  String output=null;
		  ResultSet rs = statement.executeQuery(sql);
		  while(rs.next())
				output = rs.getString(colName);
		  if (output != null)
			  return true;
		  else
			  return false;
	  }
	  public boolean isElementPresent(WebDriver driver, By by) {
		    try {
		      driver.findElement(by);
		      return true;
		    } catch (NoSuchElementException e) {
		      return false;
		    }
		  }

	public void log(BufferedWriter buffer, String str)
	{
		d=dt.format(new Date()).toString();
		try {
			buffer.write(d +": " + str + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			try {
				buffer.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			buffer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		  public void screenshot(WebDriver driver,BufferedWriter buffer, String parentDir, String saveAs)
		  {
			  String path = System.getProperty("user.dir") + "/TestResults/Screenshots";
				//System.out.println(path);
				String imgDir = path + "/" +parentDir;
				File file = new File (imgDir);

				if(!file.exists())
				file.mkdir();

				d=dt.format(new Date()).toString();
				String imgPath = imgDir + "/" + saveAs + d + ".jpg";
				//System.out.println(imgPath);
			  File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				// Now you can do whatever you need to do with it, for example copy somewhere
				  try {
					FileUtils.copyFile(scrFile, new File(imgPath));
					log(buffer, "ScreenShot taken, saved as " + imgPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
		  }

	public boolean createOutputFile( String outputFilePath )
		  {
		    boolean isFileCreated = false;

		    File outputFile = new File( outputFilePath );
		    try
		    {
		      isFileCreated = outputFile.createNewFile( );
		    }
		    catch( IOException e )
		    {
		      e.printStackTrace( );
		    }

		    return isFileCreated;
		  }

	 public void HtmlReport_initialize(String baseUrl, String browserDriver)
	  {
		 	fileName="TestResults\\Reports" + "_" + validFileString + ".html";
			htmlReportInitialize.append("<html>");
			htmlReportInitialize.append("<head>");
			htmlReportInitialize.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
			htmlReportInitialize.append("<STYLE MEDIA=\"screen\" TYPE=\"text/css\">");
			htmlReportInitialize.append("BODY{font-family:verdana,arial,helvetica,sans-serif;color:#505050;background-color:#ffffff;SCROLLBAR-3DLIGHT-COLOR:#999999;SCROLLBAR-ARROW-COLOR:#656363;SCROLLBAR-BASE-COLOR:#d1d6f0;SCROLLBAR-HIGHLIGHT-COLOR:#d1d6f0;SCROLLBAR-SHADOW-COLOR:#d1d6f0;SCROLLBAR-DARKSHADOW-COLOR:#999999;SCROLLBAR-TRACK-COLOR:#f2f4fe;}");
			htmlReportInitialize.append(".bodyText{font-size:11px;font-weight:Normal;padding-left:4px;}");
			htmlReportInitialize.append(".bodyBackground{background-color:#e6e6e6;}");
			htmlReportInitialize.append(".messageBoxBackground{background-color:#f7f8fd;}");
			htmlReportInitialize.append("TR, TD{font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;}");
			htmlReportInitialize.append(".tableBorder{background-color:#999999;}");
			htmlReportInitialize.append(".tableHeader{background-color:#bec5e7;font-size:11px;font-weight:Bold;color:#505050;line-height:18px;border-top:1px solid #999999;border-left:1px solid #999999;border-right:1px solid #999999;}");
			htmlReportInitialize.append(".tableColumnHeadings{background:#d1d6f0;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;line-height:18px;}");
			htmlReportInitialize.append(".tableColumnHeadings TD{padding-left:6px;padding-right:6px;}");
			htmlReportInitialize.append(".tableColumnHeadings TD.popupCell SPAN{padding-right:6px;float:left;}");
			htmlReportInitialize.append(".tableColumnHeadings TD.popupCell IMG{margin-top:1px;}");
			htmlReportInitialize.append(".tableColumnHeadingsNS4{background:#d1d6f0;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;}");
			htmlReportInitialize.append(".tableColumnHeadingsNS4 TD{padding-left:6px;padding-right:6px;}");
			htmlReportInitialize.append(".tableRow0{background:#f7f8fd;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;line-height:18px;}");
			htmlReportInitialize.append(".tableRow0 TD{padding-left:6px;padding-right:6px;}");
			htmlReportInitialize.append(".tableRow0 TD.popupCell SPAN{padding-right:6px;float:left;}");
			htmlReportInitialize.append(".tableRow0 TD.popupCell IMG{margin-top:1px;}");
			htmlReportInitialize.append(".tableRow0NS4{background:#f7f8fd;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;}");
			htmlReportInitialize.append(".tableRow0NS4 TD{padding-left:6px;padding-right:6px;}");
			htmlReportInitialize.append(".tableRow1{background:#eceef8;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;line-height:18px;}");
			htmlReportInitialize.append(".tableRow1 TD{padding-left:6px;padding-right:6px;}");
			htmlReportInitialize.append(".tableRow1 TD.popupCell SPAN{padding-right:6px;float:left;}");
			htmlReportInitialize.append(".tableRow1 TD.popupCell IMG{margin-top:1px;}");
			htmlReportInitialize.append(".tableRow1NS4{background:#eceef8;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;}");
			htmlReportInitialize.append(".tableRow1NS4 TD{padding-left:6px;padding-right:6px;}");
			htmlReportInitialize.append(".tableMassEntryRow{background:#fffde6;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;line-height:18px;}");
			htmlReportInitialize.append(".tableMassEntryRow TD{padding-left:6px;padding-right:6px;}");
			htmlReportInitialize.append(".tableRowSelector{background:#d1d6f0;}");
			htmlReportInitialize.append(".rowHighlight{background:#fff6a6;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;line-height:18px;}");
			htmlReportInitialize.append(".rowHighlight TD{padding-left:6px;padding-right:6px;}");
			htmlReportInitialize.append(".rowHighlight TD.popupCell SPAN{padding-right:6px;float:left;}");
			htmlReportInitialize.append(".rowHighlight TD.popupCell IMG{margin-top:1px;}");
			htmlReportInitialize.append(".rowHighlightNS4{background:#fff6a6;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;}");
			htmlReportInitialize.append(".rowHighlightNS4 TD{padding-left:6px;padding-right:6px;}");
			htmlReportInitialize.append(".columnHighlight{background:#fff6a6;font-family:verdana,arial,helvetica,sans-serif;font-size:11px;color:#505050;}");
			htmlReportInitialize.append("</STYLE>");
			htmlReportInitialize.append("</head>");
			htmlReportInitialize.append("<body scrollable=\"no\">");
			htmlReportInitialize.append("<table width=\"100%\" border=\"1\">");
			htmlReportInitialize.append("<tr>");
			htmlReportInitialize.append("<td width=\"86%\">");
			htmlReportInitialize.append("<table bgcolor=\"#BEC6E7\" width=\"100%\" border=\"2\">");
			htmlReportInitialize.append("<tr>");
			htmlReportInitialize.append("<td align=\"center\" colspan=\"4\" width=\"103%\">");
			htmlReportInitialize.append("<h3>");
			htmlReportInitialize.append("<b>UI AUTOMATION TEST RESULTS</b>");
			htmlReportInitialize.append("</h3>");
			htmlReportInitialize.append("</td>");
			htmlReportInitialize.append("</tr>");
			htmlReportInitialize.append("<tr>");
			htmlReportInitialize.append("<td align=\"left\"><b>APPLICATION URL : <font color=\"#0000FF\">" + baseUrl + "</font></b></td><td align=\"left\"><b>WebDriver : " +browserDriver+ "</b></td><td align=\"left\"><b>Test Execution Date : "+execDate.format(date)+"</b></td><td align=\"left\"><b></b></td></td>");
			htmlReportInitialize.append("</tr>");
	}

	public void HtmlReport_result(int testCount, int testPassed, int testFailed)
		{
			htmlReportResult.append("<tr>");
			htmlReportResult.append("<td align=\"left\"><b>TOTAL TESTS</b> : " + testCount + "</td><td align=\"left\"><b>Test Passed : </b>" +testPassed+ "</td><td align=\"left\"><b>Test Failed : </b><Font color=\"#ff0000\">" +testFailed+ "</font></td><td align=\"left\"></td></td>");
			htmlReportResult.append("</tr>");
			htmlReportResult.append("</table>");
			htmlReportResult.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">");
			htmlReportResult.append("<tr>");
			htmlReportResult.append("<th align=\"left\"></th>");
			htmlReportResult.append("</tr>");
			htmlReportResult.append("</table>");
			htmlReportResult.append("<table width=\"100%\" bgcolor=\"#ECEEF9\" border=\"2\">");
			htmlReportResult.append("<tr>");
			htmlReportResult.append("<th align=\"left\">TEST FILE</th><th align=\"left\">TEST NAME</th><th align=\"left\">STATUS</th><th align=\"left\">ERROR MESSAGE</th><th align=\"left\">Execution Time</th>");
			htmlReportResult.append("</tr>");
	}

	  public void HtmlReport_lines(String testcaseWorkbook,String testcaseWorksheet,String testResult,String firstCellVal, String diffTime)
	  {
	       if(testResult.equals("Warning") || testResult.equals("Blank Page") )
	          {
				htmlReportLines.append("<tr>");
				htmlReportLines.append("<td align=\"left\">" + testcaseWorkbook + "</td><td align=\"left\">" + testcaseWorksheet + "</td><td width=\"20%\" align=\"left\">" + "Warning/Black Page" + "</td><td align=\"left\"></td>" + "</td><td align=\"left\">" + diffTime + "</td>");
				htmlReportLines.append("</tr>");

	          }
	       if(testResult.equals("Passed"))
	          {
				htmlReportLines.append("<tr>");
				htmlReportLines.append("<td align=\"left\">" + testcaseWorkbook + "</td><td align=\"left\">" + testcaseWorksheet + "</td><td width=\"20%\" align=\"left\">" + "Passed" + "</td><td align=\"left\"></td>" + "</td><td align=\"left\">"+ diffTime + " </td>");
				htmlReportLines.append("</tr>");
	           }
	       if(testResult.equals("Failed"))
	          {
				htmlReportLines.append("<tr>");
				htmlReportLines.append("<td align=\"left\">" + testcaseWorkbook + "</td><td align=\"left\">" + testcaseWorksheet + "</td><td width=\"20%\" align=\"left\"><font color=\"#ff0000\">" + "Failed" + "</font></td><td align=\"left\">" + firstCellVal + "</td>" + "</td><td align=\"left\">" + diffTime + "</td>");
				htmlReportLines.append("</tr>");
	          }

	  }

	public void HtmlReport_close()
	{
				htmlReportClose.append("</table>");
				htmlReportClose.append("</td>");
				htmlReportClose.append("</tr>");
				htmlReportClose.append("</table>");
				htmlReportClose.append("</body>");
				htmlReportClose.append("</html> ");
	}

	public void HtmlReport_generator()
	{
			BufferedWriter writer = null;
			File checkforfile = new File(fileName);
	     		if(!checkforfile.exists())
	   		  	{
	   	  			try {
	   	  				createOutputFile(fileName);
	   	  				writer = new BufferedWriter(new FileWriter(fileName,true));
	   	  				writer.newLine();
	   	  				writer.write(htmlReportInitialize.toString());
	   	  				writer.write(htmlReportResult.toString());
	   	  				writer.write(htmlReportLines.toString());
	   	  				writer.write(htmlReportClose.toString());
	   	  				writer.close();
	   	  			}
	            	catch (Exception e) {
				        e.printStackTrace();
	            	}
				}
	}

	public int countPassed() {
		int passed=0;
	    Pattern pass = Pattern.compile("Passed");
		Matcher m = pass.matcher(htmlReportLines.toString());
		while (m.find()) {
			passed++;
		}
		return passed;
	}

	public int countFailed() {
		int failed=0;
		   Pattern pass = Pattern.compile("Failed");
			Matcher m = pass.matcher(htmlReportLines.toString());
			while (m.find()) {
				failed++;
			}
		return failed;
	}

	public String msToString(long ms) {
	        long totalSecs = ms/1000;
	        long hours = (totalSecs / 3600);
	        long mins = (totalSecs / 60) % 60;
	        long secs = totalSecs % 60;
	        String minsString = (mins == 0)
	            ? "00"
	            : ((mins < 10)
	               ? "0" + mins
	               : "" + mins);
	        String secsString = (secs == 0)
	            ? "00"
	            : ((secs < 10)
	               ? "0" + secs
	               : "" + secs);
	        if (hours > 0)
	            return hours + ":" + minsString + ":" + secsString;
	        else if (mins > 0)
	            return "0:"  + mins + ":" + secsString;
	        else return "0:0:" + secsString;
	    }

	public static int validTestCount() {
		int lines = 0;
			//String line=null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("res//TestCase.txt"));
			String text=null;
			while((text=reader.readLine())!=null) {
				if(text.trim().length()> 0 && !(text.startsWith("#"))) {
					lines++;
				}
			}
			reader.close();
		}
		catch (Exception e) {
			        e.printStackTrace();
	  	}
	  	return lines;
	}

	public int totalTestCount() {
		int testCount=0;
		String testcaseWorkbook;
		String testcaseWorksheet;
		//File checkforfile = new File("res//validTestCases.txt");

		try {
			//createOutputFile(fileName);
			BufferedReader reader = new BufferedReader(new FileReader("res//TestCase.txt"));
			String testCaseName=null;
			while((testCaseName=reader.readLine())!=null) {
				//if(text.trim().length()> 0 && !(text.startsWith("#"))) {
				//skip commented (#) test cases in TestCases.txt
				if (!(testCaseName.trim().length()> 0) || (testCaseName.trim().startsWith("#"))) {
					continue;
				}
				//Splitting workbook and worksheet
				String[] testcaseParam = testCaseName.split(",");
				testcaseWorkbook = testcaseParam[0];
				testcaseWorksheet = testcaseParam[1];

				if (testcaseWorksheet.trim().startsWith("DD_", 0)) {
					continue;
				}
				int sheetCount = 1;
				Sheet[] sheetArray;
				boolean tcFlag = false;
				//Reading test case workbook to get all sheet names
				FileInputStream istreamWB = new FileInputStream(testcaseWorkbook);
				Workbook workbook = Workbook.getWorkbook(istreamWB);

				if(testcaseWorksheet.toLowerCase().equals("all")) {
					sheetArray = workbook.getSheets(); // to read all sheets in workbook
					sheetCount = sheetArray.length;
					tcFlag=true;
				} else	{
					sheetCount=1;
					tcFlag=false;
				}

				for(int iCount=0; iCount < sheetCount;iCount++) {
					if(tcFlag) {
						testcaseWorksheet = workbook.getSheet(iCount).getName();
					}

					if(!(testcaseWorksheet.equalsIgnoreCase("main")||testcaseWorksheet.equalsIgnoreCase("resource")||testcaseWorksheet.equalsIgnoreCase("contigencyPlan")||testcaseWorksheet.contains("#") || testcaseWorksheet.trim().startsWith("DD_", 0))) {
						testCount++;
					}
				}
				istreamWB.close();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testCount;
	}

	public int instanceCount() {
		NodeList list=null;
		try {
			String filepath = "BuildtestNG.xml";
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);

			list = doc.getElementsByTagName("test");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
		return (list.getLength());
	 }

	public void createTestFiles(int instances) {
		validTestCnt=totalTestCount();
		notInuse=false;
		String TestSuitFile=null;;
		int linesPerFile=validTestCnt/instances;
		String linesOfFile=null;
		BufferedWriter fileWriter = null;
		String fileName=null;
		File checkforfile = null;
		StringBuffer testCases=new StringBuffer();

		try{
	        File validTestFile = new File("res//validTestCases.txt");
	          if (validTestFile.exists()){
	        	  TestSuitFile="res//validTestCases.txt";
	          } else {
	        	  TestSuitFile="res//TestCase.txt";
	          }
	    }catch(Exception e){
	         e.printStackTrace();
	    }

		if(validTestCnt > instances) {
			int i=1;
			int j=0;
			try {
				BufferedReader reader = new BufferedReader(new FileReader(TestSuitFile));
				linesOfFile=null;
				fileName="res//TestCasePart"+i+".txt";
				checkforfile = new File(fileName);

					while((linesOfFile=reader.readLine())!=null) {
						if(linesOfFile.trim().length()> 0 && !(linesOfFile.startsWith("#"))) {
							testCases.append(linesOfFile + "\n");
							j++;
							}
						checkforfile = new File(fileName);
						if(!checkforfile.exists() && i!=instances && j==linesPerFile) {
							createOutputFile(fileName);
							fileWriter = new BufferedWriter(new FileWriter(fileName,true));
							fileWriter.write(testCases.toString());
							fileWriter.close();
							testCases.setLength(0);
							i++;
							j=0;
							fileName="res//TestCasePart"+i+".txt";
						}
					}

				if(i==instances){
					checkforfile = new File(fileName);
					if(!checkforfile.exists()) {
						createOutputFile(fileName);
						fileWriter = new BufferedWriter(new FileWriter(fileName,true));
						fileWriter.write(testCases.toString());
						fileWriter.close();
						testCases.setLength(0);
					}
				}
				reader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			String noTestCase="#This file does not contain any test case";
			int i=1;
			try {
				BufferedReader reader = new BufferedReader(new FileReader(TestSuitFile));
				while((linesOfFile=reader.readLine())!=null) {
					fileName="res//TestCasePart"+i+".txt";
					checkforfile = new File(fileName);
					if(!checkforfile.exists() && linesOfFile.trim().length()> 0 && !(linesOfFile.startsWith("#"))) {
						createOutputFile(fileName);
						fileWriter = new BufferedWriter(new FileWriter(fileName,true));
						fileWriter.write(linesOfFile + ",");
						fileWriter.close();
						i++;
					}
				}
				reader.close();
				for(i=1;i<=instances;i++){
				//while(i<=instances){
					fileName="res//TestCasePart"+i+".txt";
					checkforfile = new File(fileName);
					if(!checkforfile.exists())	{
						createOutputFile(fileName);
						fileWriter = new BufferedWriter(new FileWriter(fileName,true));
						fileWriter.write(noTestCase);
						fileWriter.close();
					}
					//i=i+1;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getPropertyValue(String file, String prop)	{
		Properties properties = new Properties();
		try {
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			if(in !=null){
				properties.load(in);
			}
	 }
		catch( Exception e )  {
			e.printStackTrace( );
		}
		System.out.println("Property Value  is : "+ properties.getProperty(prop));
		if(properties.getProperty(prop)!=null) {
			return properties.getProperty(prop);
		}
		else {
			return "propNotFound";
		}

	}

	public boolean fileDirExists(String fileName) {
		File f = new File(fileName);
		if(f.exists()) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean findString(String fileName, String searchString) {
		File f= new File(fileName);
	    boolean result = false;
	    Scanner in = null;
	    if(f.exists()) {
			try {
				in = new Scanner(new FileReader(f));
				while(in.hasNextLine() && !result) {
					result = in.nextLine().indexOf(searchString) >= 0;
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			finally {
				try { in.close() ; } catch(Exception e) { /* ignore */ }
			}
		}
	    return result;
	}


	public boolean execWinBatch(String fileName) {
		File f = new File(fileName);
		if(f.exists()) {
			String command="cmd /c start " + fileName;
			Runtime runtime = Runtime.getRuntime();
			try {
				Process p1 = runtime.exec(command);
				InputStream is = p1.getInputStream();
				int i = 0;
				while( (i = is.read() ) != -1) {
					System.out.print((char)i);
				}
			} catch(IOException ioException) {
				System.out.println(ioException.getMessage() );
			}
			return true;
		}
		else {
			return false;
		}
	}

	public void execUnixShell(String fileName) {
		try {
		    ProcessBuilder pb = new ProcessBuilder(fileName);
		    Process p = pb.start();
		    p.waitFor();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	public boolean isInteger(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch(NumberFormatException e) {
	        return false;
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}

	public boolean isNumber(String str)
	{
	  return str.matches("[+-]?\\d*(\\.\\d+)?");
	}

	public boolean isEqual(String firstVar, String secondVar) {
		if(isNumber(firstVar) && isNumber(secondVar)) {
			double num1=Double.parseDouble(firstVar);
			double num2=Double.parseDouble(secondVar);
			if(num1==num2) {
				return true;
			} else {
				return  false;
			}
		} else {
			if(firstVar.equals(secondVar)) {
				return true;
			} else {
				return false;
			}
		}
	}
}
