@echo off
javac -encoding UTF-8 SuperArrayCalculatorGUI.java
if exist SuperArrayCalculatorGUI.class (
    java -Dfile.encoding=UTF-8 SuperArrayCalculatorGUI
) else (
    echo Compilation error!
)
pause