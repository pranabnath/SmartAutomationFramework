package com.auto.pkg.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.concurrent.TimeUnit;
import java.io.FileInputStream;
import java.io.DataInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import java.io.InputStream;
import java.util.Properties;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import jxl.Sheet;
import jxl.Workbook;


public class GenerateHTMLReport {

	static int passCount;
	static int failCount;
	static String baseURL;
	static String webDriver;
	static String logFile;
	static String testCaseFile="res//TestCase.txt";

	static Date date = new Date();
	static StringBuffer htmlReportInitialize=new StringBuffer();
	static StringBuffer htmlReportResult=new StringBuffer();
	static StringBuffer htmlReportPassedLines=new StringBuffer();
	static StringBuffer htmlReportFailedLines=new StringBuffer();
	static StringBuffer htmlReportClose=new StringBuffer();


	static DateFormat execDate= new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	static String dateWithSpace=execDate.format(date);
	static String dateWithoutSpace=dateWithSpace.replace(' ','_');
	static String validFileString=dateWithoutSpace.replace(':','-');
	static String fileName="TestResults\\Reports" + "_" + validFileString + ".html";

	public static int instanceCount() {
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

	public static int validTestCount() {
		int lines = 0;
			//String line=null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testCaseFile));
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

	public static int totalTestCount() {
		int testCount=0;
		String testcaseWorkbook;
		String testcaseWorksheet;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(testCaseFile));
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


	public static boolean createOutputFile( String outputFilePath )
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


	public String getPropertyValue(String file, String prop)	{
			Properties properties = new Properties();
			try {
				InputStream in = getClass().getResourceAsStream("/conf/" + file);
				properties.load(in);
			}
			catch( Exception e )  {
				e.printStackTrace( );
			}
			return properties.getProperty(prop);
		}

	public static void ReadCsvProps() {
		String strLine=null;
		try {
			FileInputStream fstream = new FileInputStream("conf\\TestProps.csv");
			DataInputStream in = new DataInputStream(fstream);

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			br.readLine(); // to exempt header line (first row) of CSV file
			strLine = br.readLine(); // fetches the second line in CSV file.
			String[] values = strLine.split(",");
			webDriver=values[5];
			baseURL=values[6];
			logFile=values[7];
			br.close();
		} catch (Exception e) {
					e.printStackTrace();
		}
	}

	public static void HtmlReport_initialize(String baseUrl, String webDriver)
	  {
		fileName="TestResults\\Reports" + "_" + validFileString + ".html";
		htmlReportInitialize.append("<html>");
		htmlReportInitialize.append("<head>\n");
		htmlReportInitialize.append("<script type=\"text/javascript\">\n");
		htmlReportInitialize.append("        var people, asc1 = 1,\n");
		htmlReportInitialize.append("            asc2 = 1,\n");
		htmlReportInitialize.append("            asc3 = 1;\n");
		htmlReportInitialize.append("        window.onload = function () {\n");
		htmlReportInitialize.append("            people = document.getElementById(\"people\");\n");
		htmlReportInitialize.append("        }\n");
		htmlReportInitialize.append("        function sort_table(tbody, col, asc)\n");
		htmlReportInitialize.append("		{\n");
		htmlReportInitialize.append("		    var rows = tbody.rows;\n");
		htmlReportInitialize.append("		    var rlen = rows.length;\n");
		htmlReportInitialize.append("		    var arr = new Array();\n");
		htmlReportInitialize.append("		    var i, j, cells, clen;\n");
		htmlReportInitialize.append("		    // fill the array with values from the table\n");
		htmlReportInitialize.append("		    for(i = 0; i < rlen; i++)\n");
		htmlReportInitialize.append("		    {\n");
		htmlReportInitialize.append("		        cells = rows[i].cells;\n");
		htmlReportInitialize.append("		        clen = cells.length;\n");
		htmlReportInitialize.append("		        arr[i] = new Array();\n");
		htmlReportInitialize.append("		      for(j = 0; j < clen; j++) { arr[i][j] = cells[j].innerHTML; }\n");
		htmlReportInitialize.append("		    }\n");
		htmlReportInitialize.append("		    // sort the array by the specified column number (col) and order (asc)\n");
		htmlReportInitialize.append("		    arr.sort(function(a, b)\n");
		htmlReportInitialize.append("		    {\n");
		htmlReportInitialize.append("		        var retval=0;\n");
		htmlReportInitialize.append("		        var fA=parseFloat(a[col]);\n");
		htmlReportInitialize.append("		        var fB=parseFloat(b[col]);\n");
		htmlReportInitialize.append("		        if(a[col] != b[col])\n");
		htmlReportInitialize.append("		        {\n");
		htmlReportInitialize.append("		            if((fA==a[col]) && (fB==b[col]) ){ retval=( fA > fB ) ? asc : -1*asc; } //numerical\n");
		htmlReportInitialize.append("		            else { retval=(a[col] > b[col]) ? asc : -1*asc;}\n");
		htmlReportInitialize.append("		        }\n");
		htmlReportInitialize.append("		        return retval;\n");
		htmlReportInitialize.append("		    });\n");
		htmlReportInitialize.append("		    for(var rowidx=0;rowidx<rlen;rowidx++)\n");
		htmlReportInitialize.append("		    {\n");
		htmlReportInitialize.append("		        for(var colidx=0;colidx<arr[rowidx].length;colidx++){ tbody.rows[rowidx].cells[colidx].innerHTML=arr[rowidx][colidx]; }\n");
		htmlReportInitialize.append("		    }\n");
		htmlReportInitialize.append("		}\n");
		htmlReportInitialize.append("    </script>\n");
		htmlReportInitialize.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
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
		htmlReportInitialize.append("</STYLE>\n");
		htmlReportInitialize.append("</head>\n");
		htmlReportInitialize.append("<body scrollable=\"no\">\n");
		htmlReportInitialize.append("<table width=\"100%\" border=\"1\">\n");
		htmlReportInitialize.append("<tr>\n");
		htmlReportInitialize.append("<td width=\"86%\">\n");
		htmlReportInitialize.append("<table bgcolor=\"#8181F7\" width=\"100%\" border=\"2\">\n");
		htmlReportInitialize.append("<tr>\n");
		htmlReportInitialize.append("<td align=\"center\" colspan=\"4\" width=\"103%\">\n");
		htmlReportInitialize.append("<h3>");
		htmlReportInitialize.append("<b>UI AUTOMATION TEST RESULTS</b>");
		htmlReportInitialize.append("</h3>");
		htmlReportInitialize.append("</td>\n");
		htmlReportInitialize.append("</tr>\n");
		htmlReportInitialize.append("<tr>\n");
		htmlReportInitialize.append("<td align=\"left\"><b>APPLICATION URL : <font color=\"#0000FF\">" + baseUrl + "</font></b></td>\n<td align=\"left\"><b>WebDriver : " +webDriver+ "</b></td>\n<td align=\"left\"><b>Test Execution Date : "+execDate.format(date)+"</b></td>\n<td align=\"left\"><b></b></td>\n");
		htmlReportInitialize.append("</tr>\n");
	}

	public static void HtmlReport_result(int testCount, int testPassed, int testFailed)
		{
		int testNotRun=testCount - (testPassed+testFailed);
		htmlReportResult.append("<tr>\n");
		htmlReportResult.append("<td align=\"left\"><b>TOTAL TESTS</b> : " + testCount + "</td>\n<td align=\"left\"><b>Test Passed : </b>" +testPassed+ "</td>\n<td align=\"left\"><b>Test Failed : </b><Font color=\"#ff0000\">" +testFailed+ "</font></td>\n<td align=\"left\"><b>Test Not Run : </b><Font color=\"#ff0000\">" +testNotRun+ "</font></td>\n");
		htmlReportResult.append("</tr>\n");
		htmlReportResult.append("</table>\n");
		htmlReportResult.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">\n");
		htmlReportResult.append("<tr>\n");
		htmlReportResult.append("<th align=\"left\"></th>\n");
		htmlReportResult.append("</tr>\n");
		htmlReportResult.append("</table>\n");
		htmlReportResult.append("<table width=\"100%\" bgcolor=\"#ECEEF9\" border=\"2\">\n");
		htmlReportResult.append("<thead><tr>\n");
		htmlReportResult.append("<th onclick=\"sort_table(report, 0, asc1); asc1 *= -1; asc2 = 1; asc3 = 1; asc4 = 1; asc5 = 1;\" bgcolor=\"#BEC6E7\" align=\"left\">TEST FILE</th>\n<th onclick=\"sort_table(report, 1, asc2); asc2 *= -1; asc5 = 1; asc4 = 1; asc3 = 1; asc1 = 1;\" bgcolor=\"#BEC6E7\" align=\"left\">TEST NAME</th>\n<th onclick=\"sort_table(report, 2, asc3); asc3 *= -1; asc5 = 1; asc4 = 1; asc2 = 1; asc1 = 1;\" bgcolor=\"#BEC6E7\" align=\"left\">STATUS</th>\n<th onclick=\"sort_table(report, 3, asc4); asc4 *= -1; asc5 = 1; asc3 = 1; asc3 = 1; asc1 = 1;\" bgcolor=\"#BEC6E7\" align=\"left\">ERROR MESSAGE</th>\n<th onclick=\"sort_table(report, 4, asc5); asc5 *= -1; asc4 = 1; asc3 = 1; asc2 = 1; asc1 = 1;\" bgcolor=\"#BEC6E7\" align=\"left\">Execution Time</th>\n");
		htmlReportResult.append("</tr></thead>\n");
		htmlReportResult.append("<tbody id=\"report\">\n");
	}


	public static void HtmlReport_lines() {
		passCount=0;
		failCount=0;

		String testcaseWorkbook=null;
		String testcaseWorksheet=null;
		String errorMsg=null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(logFile));
			String text=null;
			while((text=reader.readLine())!=null) {
				String passStr=null;
				String execTime=null;
				String failStr=null;
				//String[] testCase=null;
				if(text.trim().length()> 0 && text.contains("Passed")) {
					passStr=StringUtils.substringBetween(text, "Testcase ", " Status:Passed");
					execTime=StringUtils.substringBetween(text, "Execution Time: ", " ######");
					if(passStr!=null) {
						String[] passTestCase= passStr.split(",");
						testcaseWorkbook=passTestCase[0];
						testcaseWorksheet=passTestCase[1];
						htmlReportPassedLines.append("<tr>\n");
						htmlReportPassedLines.append("<td align=\"left\">" + testcaseWorkbook + "</td>\n<td align=\"left\">" + testcaseWorksheet + "</td>\n<td width=\"20%\" align=\"left\">" + "Passed" + "</td>\n<td align=\"left\"></td>" + "\n<td align=\"left\">"+ execTime + " </td>\n");
						htmlReportPassedLines.append("</tr>\n");
						passCount++;
					}
				}
				if(text.trim().length()> 0 && text.contains("Failed")) {
					if(!(text.endsWith("######"))) {
						String nextLine;
						while((nextLine=reader.readLine())!=null) {
						 	text=text + " " + nextLine;
						 	if(text.endsWith("######")) {
								break;
						 	}
 						}
					}
					failStr = StringUtils.substringBetween(text, "Testcase ", " Status:Failed");
					errorMsg=StringUtils.substringBetween(text, "Error Message : ", " Execution Time");
					execTime=StringUtils.substringBetween(text, "Execution Time: ", " ######");
					if(failStr!=null) {
						String[] failTestCase= failStr.split(",");
						testcaseWorkbook=failTestCase[0];
						testcaseWorksheet=failTestCase[1];
						htmlReportFailedLines.append("<tr>\n");
						htmlReportFailedLines.append("<td align=\"left\">" + testcaseWorkbook + "</td>\n<td align=\"left\">" + testcaseWorksheet + "</td>\n<td width=\"20%\" align=\"left\"><font color=\"#ff0000\">" + "Failed" + "</font></td>\n<td align=\"left\">" + errorMsg + "</td>" + "\n<td align=\"left\">" + execTime + "</td>\n");
						htmlReportFailedLines.append("</tr>\n");
						failCount++;
					}
				}
			}
			reader.close();
		}
		catch (Exception e) {
					e.printStackTrace();
		}
	}

	public static void HtmlReport_close()
	{
		htmlReportClose.append("</tbody>\n");
		htmlReportClose.append("</table>\n");
		htmlReportClose.append("</td>\n");
		htmlReportClose.append("</tr>\n");
		htmlReportClose.append("</table>\n");
		htmlReportClose.append("</body>\n");
		htmlReportClose.append("</html> ");
	}

	public static void HtmlReport_generator()
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
			writer.write(htmlReportPassedLines.toString());
			writer.write(htmlReportFailedLines.toString());
			writer.write(htmlReportClose.toString());
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

	public static void main(String [] args) {
		ReadCsvProps();
		HtmlReport_initialize(baseURL, webDriver);
		HtmlReport_lines();
		HtmlReport_result(totalTestCount(), passCount, failCount);
		HtmlReport_close();
		HtmlReport_generator();
	}
}