@echo off
echo Compiling...
if not exist bin mkdir bin
javac -d bin src/hospital/model/*.java src/hospital/util/*.java src/hospital/service/*.java src/hospital/HospitalApp.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Running Hospital Management System...
java -cp bin hospital.HospitalApp
pause
