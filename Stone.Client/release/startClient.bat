@ECHO OFF
IF NOT EXIST "C:\Program Files\Java\jre6" GOTO NOJAVADIR
set JAVA_HOME="C:\Program Files\Java\jre6"
set CLASSPATH=%JAVA_HOME%/bin;%CLASSPATH%
set PATH=%JAVA_HOME%/bin;%PATH%
:NOJAVADIR
start javaw -jar .\MageClient-${project.version}.jar