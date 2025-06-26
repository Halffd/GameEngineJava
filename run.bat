@echo off
setlocal

set SCRIPT_DIR=%~dp0

java -Dorg.lwjgl.librarypath="%SCRIPT_DIR%build\libs" ^
     -cp "%SCRIPT_DIR%build\classes\java\main;%SCRIPT_DIR%build\resources\main;%SCRIPT_DIR%build\libs\*" ^
     com.half.Main %*
