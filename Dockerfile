FROM eclipse-temurin:21-jdk AS build
WORKDIR /
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /
COPY --from=build /target/*.jar application.jar
COPY .env .env
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "application.jar"]