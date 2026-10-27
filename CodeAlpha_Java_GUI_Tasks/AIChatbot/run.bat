@echo off
if not exist out mkdir out
javac -d out src\AIChatbot.java
if errorlevel 1 pause & exit /b 1
java -cp out AIChatbot
pause
