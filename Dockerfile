
# For Java 11, try this
FROM adoptopenjdk/openjdk11

EXPOSE 8080

# Refer to Maven build -> finalName
ARG JAR_FILE

# cd /opt/app
WORKDIR /opt/app

COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
