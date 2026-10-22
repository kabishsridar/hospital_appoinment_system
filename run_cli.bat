@echo off
setlocal
cd /d "%~dp0"

echo ======================================================================
echo    SRM TRICHY  -  CSE AIML A  -  CONSOLE / CLI APPOINTMENT SYSTEM
echo ======================================================================
echo.
if not exist "bin" mkdir "bin"

echo Compiling Java source files...
javac -d bin -sourcepath src src/hospital/Main.java src/hospital/model/*.java src/hospital/dao/*.java src/hospital/service/*.java src/hospital/exception/*.java src/hospital/ui/*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Starting interactive CLI...
java -cp bin hospital.Main --cli

pause
