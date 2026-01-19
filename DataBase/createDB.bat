@echo off
chcp 65001 > nul
title Установка базы данных bookstore
color 0A

echo ============================================
echo   УСТАНОВКА БАЗЫ ДАННЫХ BOOKSTORE
echo ============================================
echo.

REM Проверка наличия PostgreSQL
where psql >nul 2>nul
if %errorlevel% neq 0 (
    echo ОШИБКА: PostgreSQL не установлен или не добавлен в PATH
    echo Установите PostgreSQL и добавьте его в переменную PATH
    pause
    exit /b 1
)

REM Настройки подключения
set PGHOST=localhost
set PGPORT=5433
set PGUSER=postgres
set PGPASSWORD=1234
set DBNAME=bookstore

echo Настройки подключения:
echo Хост: %PGHOST%
echo Порт: %PGPORT%
echo Пользователь: %PGUSER%
echo База данных: %DBNAME%
echo.

REM Запрос пароля, если он не установлен
if "%PGPASSWORD%"=="Введите пароль" (
    set /p PGPASSWORD="Введите пароль для пользователя %PGUSER%: "
)

echo.
echo Шаг 1: Удаление старой базы данных (если существует)...
echo --------------------------------------------------------
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -c "DROP DATABASE IF EXISTS %DBNAME%;"
if %errorlevel% neq 0 (
    echo Предупреждение: Не удалось удалить старую базу данных
    echo Продолжаем выполнение...
)

echo.
echo Шаг 2: Создание базы данных и структуры...
echo --------------------------------------------
REM Создаем базу данных сначала
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -c "CREATE DATABASE %DBNAME% ENCODING 'UTF8';"
if %errorlevel% neq 0 (
    echo ОШИБКА при создании базы данных
    pause
    exit /b
)

echo.
echo Шаг 3: Создание таблиц...
echo --------------------------

echo Создание таблицы status (должна быть первой!)...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DBNAME% -f "Create table\Create status.sql"
if %errorlevel% neq 0 (
    echo ОШИБКА при создании таблицы status
    echo Но продолжаем...
)

echo Создание таблицы book...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DBNAME% -f "Create table\Create book.sql"
if %errorlevel% neq 0 (
    echo ОШИБКА при создании таблицы book
    pause
    exit /b 1
)

echo Создание таблицы bookCopy...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DBNAME% -f "Create table\Create bookCopy.sql"
if %errorlevel% neq 0 (
    echo ОШИБКА при создании таблицы bookCopy
    pause
    exit /b 1
)

echo Создание таблицы orderItem...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DBNAME% -f "Create table\Create orderItem.sql"
if %errorlevel% neq 0 (
    echo ОШИБКА при создании таблицы orderItem
    pause
    exit /b 1
)

echo Создание таблицы orders...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DBNAME% -f "Create table\Create orders.sql"
if %errorlevel% neq 0 (
    echo ОШИБКА при создании таблицы orders
    pause
    exit /b 1
)

echo Создание таблицы request...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DBNAME% -f "Create table\Create request.sql"
if %errorlevel% neq 0 (
    echo ОШИБКА при создании таблицы request
    pause
    exit /b 1
)

echo Создание таблицы stok...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DBNAME% -f "Create table\Create stok.sql"
if %errorlevel% neq 0 (
    echo ОШИБКА при создании таблицы stok
    pause
    exit /b 1
)

echo.
echo Шаг 4: Заполнение начальными данными...
echo ---------------------------------------

echo Заполнение таблицы status...
psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DBNAME% -f "Insert\Insert status.sql"
if %errorlevel% neq 0 (
    echo ОШИБКА при заполнении таблицы status
    pause
    exit /b 1
)

echo.

echo.
echo ============================================
echo   УСТАНОВКА УСПЕШНО ЗАВЕРШЕНА!
echo ============================================
echo.
echo Создана база данных: %DBNAME%
echo.
echo Для подключения к базе используйте:
echo   psql -h %PGHOST% -p %PGPORT% -U %PGUSER% -d %DBNAME%
echo.
timeout /t 30
pause
