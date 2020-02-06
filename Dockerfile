FROM tomcat:jre8-alpine

ADD target/DelegationProvider.war /usr/local/tomcat/webapps/dp.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
