@echo off
rmdir /s /q bin
mkdir bin
javac -d bin -sourcepath src src\ifce\ppd\game\LoginWindow.java
