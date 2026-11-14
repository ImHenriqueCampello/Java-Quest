@echo off
rem ==============================================================================
rem Java Quest: The Bytecode Chronicles - Script de Testes para Windows
rem ==============================================================================
setlocal enabledelayedexpansion

set "DIR=%~dp0"
cd /d "%DIR%"

if not exist "out\test" mkdir "out\test"

dir /s /b src\*.java test\*.java > "%TEMP%\sources_%RANDOM%.txt"
for /f "tokens=*" %%F in ("%TEMP%\sources_%RANDOM%.txt") do set "SOURCES_FILE=%%F"
dir /s /b src\*.java test\*.java > "%DIR%sources_test.txt"

javac -cp "lib\junit-platform-console-standalone.jar;src" -d "out\test" @"%DIR%sources_test.txt"
set "JAVAC_STATUS=%ERRORLEVEL%"
if exist "%DIR%sources_test.txt" del "%DIR%sources_test.txt"
if %JAVAC_STATUS% NEQ 0 (
    echo ERRO: Falha na compilacao dos testes.
    exit /b %JAVAC_STATUS%
)

java -cp "out\test;lib\junit-platform-console-standalone.jar;src" test.TestRunner %*
exit /b %ERRORLEVEL%
