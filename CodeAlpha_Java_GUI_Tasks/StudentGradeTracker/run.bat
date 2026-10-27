@echo off
if not exist out mkdir out
javac -d out src\StudentGradeTracker.java
if errorlevel 1 pause & exit /b 1
java -cp out StudentGradeTracker
pause
