FROM eclipse-temurin:21-jdk


ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} application.jar
COPY .env .env

ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/application.jar"]