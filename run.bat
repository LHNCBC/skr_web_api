::
:: usage: run.bat [jvmoptions] class [programoptions]
::
set BASEDIR=%CD%

set CP=%BASEDIR%/lib/httpclient-4.5.13.jar;%BASEDIR%/lib/httpclient-cache-4.5.13.jar
set CP=%CP%;%BASEDIR%/lib/httpcore-4.4.13.jar;%BASEDIR%/lib/httpmime-4.5.13.jar
set CP=%CP%;%BASEDIR%/lib/commons-logging-1.2.jar
set CP=%CP%;%BASEDIR%/lib/skrAPI.jar

java -cp %BASEDIR%/classes;%CP% %*
