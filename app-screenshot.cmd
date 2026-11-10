@echo off
set SRC=C:\Users\ali\Documents\Craft Anything\base\checks\app-screenshot.png
if "%~1"=="" (set OUT=C:\Users\ali\Documents\Craft Anything\base\checks\app-screenshot.png) else (set OUT=%~1)
for %%F in ("%OUT%") do if not exist "%%~dpF" mkdir "%%~dpF"
copy /Y "%SRC%" "%OUT%" >nul
exit /b 0
