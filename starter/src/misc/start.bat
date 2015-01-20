@echo off
cd /d %~dp0
mkdir -p logs

ECHO %PROCESSOR_ARCHITECTURE%|FINDSTR AMD64>NUL && SET JAVA_HOME=%~dp0\x64 || SET JAVA_HOME=%~dp0\i586
echo "JAVA_HOME set to %JAVA_HOME%"
SET PATH=%JAVA_HOME%;%PATH%
start %JAVA_HOME%\bin\javaw -jar swap-starter.jar -jars lib -main-class org.projectsforge.swap.proxy.starter.Main -output "logs\log-%DATE:/=-%-%time::=-%.txt"

