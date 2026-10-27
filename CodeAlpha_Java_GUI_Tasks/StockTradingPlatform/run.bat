@echo off
if not exist out mkdir out
javac -d out src\StockTradingPlatform.java
if errorlevel 1 pause & exit /b 1
java -cp out StockTradingPlatform
pause
