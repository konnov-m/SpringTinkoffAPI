FROM tomcat:9.0
LABEL maintainer="str@ya.ru"

ADD /build/libs/tinkoff.war /usr/local/tomcat/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]