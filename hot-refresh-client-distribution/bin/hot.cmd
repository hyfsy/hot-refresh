@echo off

rem Get home root path
pushd %~dp0..
set BASE_DIR=%CD%
popd

rem Which java to use
IF ["%JAVA_HOME%"] EQU [""] (
	set JAVA=java
) ELSE (
	set JAVA="%JAVA_HOME%/bin/java"
)

rem Set debug instruct
set REMOTE_DEBUG_INST=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
set REMOTE_DEBUG_OPTS=
set REMOTE_DEBUG_WAIT_OPTS=
for %%a in (%*) do (
  if "%%a"=="-d" set REMOTE_DEBUG_OPTS=%REMOTE_DEBUG_INST%
  if "%%a"=="--debug" set REMOTE_DEBUG_OPTS=%REMOTE_DEBUG_INST%
  if "%%a"=="--wait" set REMOTE_DEBUG_WAIT_OPTS=-DhotRefreshClientStartWaitSeconds=3
)

set COMMAND=%JAVA% %REMOTE_DEBUG_OPTS% %REMOTE_DEBUG_WAIT_OPTS% -jar %BASE_DIR%\lib\hot-refresh-client.jar %*
rem echo %COMMAND%
%COMMAND%