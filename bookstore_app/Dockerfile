FROM maven:3.9.6-eclipse-temurin-25 AS builder
WORKDIR /app
COPY pom.xml .
COPY bookstore/pom.xml ./bookstore/
RUN mvn dependency:go-offline -B
COPY bookstore/src ./bookstore/src
RUN mvn -pl bookstore -am clean package -DskipTests
FROM tomcat:11.0-jdk25
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /app/bookstore/target/bookstore.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir -p /usr/local/tomcat/logs/bookstore
EXPOSE 8080
CMD ["catalina.sh", "run"]