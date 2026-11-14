@echo off
rem ==============================================================================
rem Java Quest: The Bytecode Chronicles - Script de Empacotamento para Windows
rem Gera distribuição standalone com Java embutido (jpackage com --win-console)
rem ==============================================================================
setlocal enabledelayedexpansion

echo ================================================================================
echo    JAVA QUEST: BUILD & DISTRIBUICAO AUTOMATIZADA COM JPACKAGE (WINDOWS)
echo ================================================================================

where javac >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERRO: javac nao encontrado no PATH.
    exit /b 1
)

where jpackage >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERRO: jpackage nao encontrado no PATH.
    exit /b 1
)

set "APP_NAME=JavaQuest"
set "MAIN_CLASS=Main"

rem Determinar versao (suporta argumento de linha de comando ou tag do GitHub Actions)
set "APP_VERSION=%~1"
if "%APP_VERSION%"=="" (
    if defined GITHUB_REF_NAME (
        set "APP_VERSION=%GITHUB_REF_NAME%"
    ) else (
        set "APP_VERSION=1.0.0"
    )
)
if "%APP_VERSION:~0,1%"=="v" set "APP_VERSION=%APP_VERSION:~1%"

set "BUILD_DIR=build"
set "DIST_DIR=dist"
set "WIN_DIST=%DIST_DIR%\windows\%APP_NAME%"

echo [1/5] Limpando diretorios temporarios...
if exist "%BUILD_DIR%" rd /s /q "%BUILD_DIR%"
if exist "%DIST_DIR%\windows" rd /s /q "%DIST_DIR%\windows"
mkdir "%BUILD_DIR%\classes"
mkdir "%BUILD_DIR%\jar"
mkdir "%DIST_DIR%\windows"

echo [2/5] Compilando codigo-fonte de 'src'...
dir /s /b src\*.java > "%BUILD_DIR%\sources.txt"
javac -d "%BUILD_DIR%\classes" @"%BUILD_DIR%\sources.txt"
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%
copy src\*.properties "%BUILD_DIR%\classes\" >nul 2>&1

echo [3/5] Gerando arquivo JAR executavel (%APP_NAME%.jar)...
(
echo Manifest-Version: 1.0
echo Main-Class: %MAIN_CLASS%
echo Created-By: Java Quest Build System
) > "%BUILD_DIR%\manifest.txt"
jar --create --file "%BUILD_DIR%\jar\%APP_NAME%.jar" --manifest="%BUILD_DIR%\manifest.txt" -C "%BUILD_DIR%\classes" .
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%

echo [4/5] Executando jpackage (embutindo Java Runtime com suporte a console)...
jpackage ^
    --type app-image ^
    --input "%BUILD_DIR%\jar" ^
    --main-jar "%APP_NAME%.jar" ^
    --main-class "%MAIN_CLASS%" ^
    --name "%APP_NAME%" ^
    --app-version "%APP_VERSION%" ^
    --dest "%DIST_DIR%\windows" ^
    --add-modules java.base ^
    --jlink-options "--strip-debug --no-man-pages --no-header-files" ^
    --win-console
if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%

echo [5/5] Criando launcher de dois cliques (Play-JavaQuest.bat)...
(
echo @echo off
echo title Java Quest: The Bytecode Chronicles
echo cd /d "%%~dp0"
echo "%%~dp0bin\JavaQuest.exe" %%*
echo if %%ERRORLEVEL%% NEQ 0 (
echo     echo.
echo     pause
echo ^)
) > "%WIN_DIST%\Play-JavaQuest.bat"

(
echo ================================================================================
echo           JAVA QUEST: THE BYTECODE CHRONICLES - INSTRUCOES WINDOWS
echo ================================================================================
echo.
echo COMO JOGAR COM 2 CLIQUES:
echo 1. De dois cliques no arquivo 'Play-JavaQuest.bat' ou 'bin\JavaQuest.exe'.
echo 2. O jogo abrira automaticamente no Prompt de Comando / Windows Terminal!
echo 3. Nao e necessario instalar Java nem configurar variaveis de ambiente.
echo.
echo ================================================================================
) > "%WIN_DIST%\COMO_JOGAR.txt"

echo Gerando pacote compactado ZIP: %DIST_DIR%\JavaQuest-Windows-x64.zip...
powershell -NoProfile -Command "Compress-Archive -Path '%WIN_DIST%' -DestinationPath '%DIST_DIR%\JavaQuest-Windows-x64.zip' -Force"

echo.
echo ================================================================================
echo    BUILD CONCLUIDO COM SUCESSO! PACOTE WINDOWS PRONTO PARA DISTRIBUICAO!
echo ================================================================================
echo Pasta Standalone : %WIN_DIST%
echo Arquivo Release  : %DIST_DIR%\JavaQuest-Windows-x64.zip
echo ================================================================================
