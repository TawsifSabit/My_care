@echo off
echo Compiling MyCare Hospital Management System...

if not exist bin mkdir bin

for /r src\main\java %%f in (*.java) do javac -cp "lib/*" -d bin "%%f"

if %errorlevel% equ 0 (
    echo Compilation successful!
    echo To run the application:
    echo java -cp "bin;lib/*" com.mycare.view.LoginView
) else (
    echo Compilation failed!
)