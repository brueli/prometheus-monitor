SET scriptRoot=%~dp0

cd %scriptRoot%\monitoring-probe-core
CALL .\mvnw install

cd %scriptRoot%\monitoring-probe-sample
CALL .\mvnw install

cd %scriptRoot%\monitoring-probe-application
CALL .\mvnw package
