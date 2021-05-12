package com.auto.pkg.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.DataInputStream;
import jxl.*;

import com.auto.pkg.util.TCUtil;

public class TestCaseList extends TCUtil{

	private FileInputStream fstream;
	private DataInputStream in;
	private String strLine = null;
	private String[] values = null;
	BufferedReader br;
	BufferedWriter logBuffer;
	protected String cellValue;
	public long testStartTime;
	public long testEndTime;
	public String execTime;
	public static String exexMsg;
	public String testcaseWorksheet;
	public String testcaseWorkbook;
	String browserStr="InternetExplorerDriver";

	public void listValidTestCases() {
		String testcaseWorkbook;
		String testcaseWorksheet;
		BufferedWriter writer = null;

		try {
			File checkforfile = new File("res//validTestCases.txt");
     		if(!checkforfile.exists()) 	{
   	  			createOutputFile("res//validTestCases.txt");
   	  			writer = new BufferedWriter(new FileWriter("res//validTestCases.txt",true));
   	  			BufferedReader reader = new BufferedReader(new FileReader("res//TestCase.txt"));
				String testCaseName=null;
				while((testCaseName=reader.readLine())!=null) {
					if (!(testCaseName.trim().length()> 0) || (testCaseName.trim().startsWith("#"))) {
						continue;
					}
					String[] testcaseParam = testCaseName.split(",");
					testcaseWorkbook = testcaseParam[0];
					testcaseWorksheet = testcaseParam[1];

					if (testcaseWorksheet.trim().startsWith("DD_", 0)) {
						continue;
					}
					int sheetCount = 1;
					Sheet[] sheetArray;
					boolean tcFlag = false;
					FileInputStream istreamWB = new FileInputStream(testcaseWorkbook);
					Workbook workbook = Workbook.getWorkbook(istreamWB);

					if(testcaseWorksheet.toLowerCase().equals("all")) {
						sheetArray = workbook.getSheets();
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
							writer.write(testcaseWorkbook + "," + testcaseWorksheet);
							writer.newLine();
						}
					}
					istreamWB.close();
				}
				reader.close();
				writer.close();
			} else {
				System.out.println("File Exists");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkWebDriver(){
		try {
			fstream = new FileInputStream("conf\\TestProps.csv");
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			br.readLine();
			strLine = br.readLine();
		} catch (Exception e){
			e.getStackTrace();
		}

		values = strLine.split(",");
		String browserDriver = values[5];

		if(!(browserDriver.toLowerCase().contains(browserStr.toLowerCase()))) {
			return true;
		} else {
			return false;
		}

	}
	public static void main(String [] args) {
		TestCaseList tcl=new TestCaseList();
		if(tcl.checkWebDriver()) {
			tcl.listValidTestCases();
		}
	}
}