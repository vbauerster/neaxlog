set cp=.;./lib/commons-dbcp-1.4.jar;./lib/commons-logging-1.1.1.jar;./lib/commons-pool-1.5.4.jar;./lib/log4j-1.2.17.jar;./lib/sqlserver-4.jar
set cp=%cp%;./lib/org.springframework.aop-3.0.7.RELEASE.jar
set cp=%cp%;./lib/org.springframework.asm-3.0.7.RELEASE.jar
set cp=%cp%;./lib/org.springframework.beans-3.0.7.RELEASE.jar
set cp=%cp%;./lib/org.springframework.context-3.0.7.RELEASE.jar
set cp=%cp%;./lib/org.springframework.core-3.0.7.RELEASE.jar
set cp=%cp%;./lib/org.springframework.expression-3.0.7.RELEASE.jar
set cp=%cp%;./lib/org.springframework.jdbc-3.0.7.RELEASE.jar
set cp=%cp%;./lib/org.springframework.transaction-3.0.7.RELEASE.jar
set cp=%cp%;./lib/org.springframework.web-3.0.7.RELEASE.jar

REM Run application
java.exe -cp %cp% bauer.neax.CallCollector
