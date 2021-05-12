@echo off
title grid

%JAVA_HOME%\bin\java.exe -jar selenium-server-standalone-2.44.0.jar -role webdriver -hub http://localhost:4444/grid/register -maxSession 20 -port 5555
