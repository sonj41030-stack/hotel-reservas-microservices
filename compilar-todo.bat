@echo off
set JAVA_HOME=C:\Users\sonj4\.jdks\ms-17.0.19
set BASE=C:\Users\sonj4\hotel-reservas-microservices

echo ========================================
echo  COMPILANDO MICROSERVICIOS
echo ========================================

echo [1/7] ms-auth...
cd %BASE%\ms-auth
call .\mvnw.cmd package -DskipTests -q
if %errorlevel% neq 0 ( echo ERROR en ms-auth & pause & exit )
echo ms-auth OK

echo [2/7] ms-clientes...
cd %BASE%\ms-clientes\ms-clientes
call .\mvnw.cmd package -DskipTests -q
if %errorlevel% neq 0 ( echo ERROR en ms-clientes & pause & exit )
echo ms-clientes OK

echo [3/7] ms-reservas...
cd %BASE%\ms-reservas\ms-reserva
call .\mvnw.cmd package -DskipTests -q
if %errorlevel% neq 0 ( echo ERROR en ms-reservas & pause & exit )
echo ms-reservas OK

echo [4/7] ms-pagos...
cd %BASE%\ms-pagos\ms-pagos
call .\mvnw.cmd package -DskipTests -q
if %errorlevel% neq 0 ( echo ERROR en ms-pagos & pause & exit )
echo ms-pagos OK

echo [5/7] ms-habitaciones...
cd %BASE%\ms-habitaciones\ms-habitacion
call .\mvnw.cmd package -DskipTests -q
if %errorlevel% neq 0 ( echo ERROR en ms-habitaciones & pause & exit )
echo ms-habitaciones OK

echo [6/7] ms-hoteles...
cd %BASE%\ms-hoteles
call .\mvnw.cmd package -DskipTests -q
if %errorlevel% neq 0 ( echo ERROR en ms-hoteles & pause & exit )
echo ms-hoteles OK

echo [7/7] ms-servicios...
cd %BASE%\ms-servicios
call .\mvnw.cmd package -DskipTests -q
if %errorlevel% neq 0 ( echo ERROR en ms-servicios & pause & exit )
echo ms-servicios OK

echo.
echo ========================================
echo  LEVANTANDO DOCKER COMPOSE
echo ========================================
cd %BASE%
docker-compose up -d --build

echo.
echo ========================================
echo  LISTO! Revisa Docker Desktop
echo ========================================
pause
