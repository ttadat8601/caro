@echo off
chcp 65001 >nul
title Khởi Động Game Cờ Caro (Gomoku) - CS311A1
echo ========================================================
echo       GAME CỜ CARO (GOMOKU) - ĐỒ ÁN MÔN HỌC CS311A1
echo ========================================================
echo.
echo Đang kiểm tra môi trường Java...

set JAVA_EXE=java
where java >nul 2>&1
if %ERRORLEVEL% equ 0 (
    goto :RUN_JAR
)

if exist "D:\spss\JRE\bin\java.exe" (
    set JAVA_EXE="D:\spss\JRE\bin\java.exe"
    goto :RUN_JAR
)

if exist "C:\Program Files\Java\jdk*\bin\java.exe" (
    for /d %%i in ("C:\Program Files\Java\jdk*") do set JAVA_EXE="%%i\bin\java.exe"
    goto :RUN_JAR
)

echo [LỖI] Không tìm thấy Java trên máy tính.
echo Vui lòng cài đặt Java JRE/JDK 8 trở lên để chạy game!
pause
exit /b 1

:RUN_JAR
echo Đang khởi chạy Game Caro...
if exist "CaroGame.jar" (
    %JAVA_EXE% -jar "CaroGame.jar"
) else (
    %JAVA_EXE% -cp "bin" com.caro.Main
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo [LỖI] Không thể khởi chạy game. Mã lỗi: %ERRORLEVEL%
    pause
)
