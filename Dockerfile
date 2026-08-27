FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /build

COPY . .

RUN mvn -B package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN useradd --system --create-home spring

COPY --from=build /build/target/*.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]