@echo off
title Doctor Appointment System - Web Portal
echo =====================================================================
echo    Doctor Appointment System - Modern Web Portal
echo    SRM TRICHY - CSE AIML A - Advanced Programming Practice (APP)
echo =====================================================================
echo.
echo Starting Python web server on http://localhost:8000 ...
echo Opening your default browser...
start http://localhost:8000/web/index.html
echo.
python server.py 8000
pause

