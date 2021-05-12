@echo off
@title hub

set WinTitle=Admisnistrator: hub
echo %WinTitle%
%JAVA_HOME%\bin\java.exe -jar selenium-server-standalone-2.44.0.jar -role hub
