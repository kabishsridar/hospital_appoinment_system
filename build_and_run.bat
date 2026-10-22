@echo off
setlocal
cd /d "%~dp0"

echo ======================================================================
echo    SRM TRICHY  -  CSE AIML A  -  DOCTOR APPOINTMENT SYSTEM
echo ======================================================================
echo.
echo [1/3] Creating output directory bin/...
if not exist "bin" mkdir "bin"

echo [2/3] Compiling Java source files...
javac -d bin -sourcepath src src/hospital/Main.java src/hospital/model/*.java src/hospital/dao/*.java src/hospital/service/*.java src/hospital/exception/*.java src/hospital/ui/*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed! Please check syntax errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo [3/3] Launching Doctor Appointment System (GUI)...
echo.
java -cp bin hospital.Main

pause
