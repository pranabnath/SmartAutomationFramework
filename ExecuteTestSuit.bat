echo off
@REM Author: Pranab
@REM This batch script will execute Selenium Framework and send test results through email.

set HOME=C:\\Selenium\QA\Automation\UI_Automation\Selenium_Framework

echo Renaming TestResults Folder
MOVE "%HOME%\res\TestResults" "%HOME%\res\TestResults_%date:/=-% %time::=-%"

echo Create new TestResults Folder
mkdir %HOME%\res\TestResults

echo Set ANT_HOME in classpath
set ANT_HOME=C:\\Selenium\apache-ant-1.8.1

cd %HOME%

echo Calling Main Function
call %ANT_HOME%\bin\ant MainCall -f %HOME%\build.xml

echo Sending Test Results Mail
if EXIST "%HOME%\res\TestResults\*.txt" (
	if EXIST "%HOME%\res\TestResults\*.log" ( 
		rem wscript %HOME%\"TestResults_Mail.vbs"
		echo "file exists"
	)
) else ( 
	echo "Email sending Failed: Check if the Test execution logs are generated" 
) 

