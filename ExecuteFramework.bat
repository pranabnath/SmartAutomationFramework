@REM Author: Pranab
@REM This batch script will execute Selenium Framework and send test results through email.

echo Renaming TestResults Folder
REN "res\TestResults" "TestResults_%date:/=-% %time::=-%"

echo Create new TestResults Folder
mkdir res\TestResults

echo Set ANT_HOME in classpath
set ANT_HOME=..\..\..\..\apache-ant-1.8.1

echo Calling Main Function
call %ANT_HOME%\bin\ant MainCall

echo Sending Test Results Mail
wscript "TestResults_Mail.vbs"