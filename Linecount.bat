@echo off
cls
echo ------------------------------P3-Code----------------------------------------
LineCounter\cloc "src/main"
pause
cls
echo ------------------------------P3-Test-----------------------------------
LineCounter\cloc "src/test"
echo Press enter to see total
pause
cls
echo ------------------------------P3-Total---------------------------------------
LineCounter\cloc "src"
pause