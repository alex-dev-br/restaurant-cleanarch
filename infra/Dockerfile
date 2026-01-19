# ----- build ------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package \
    && JAR=$(ls -1 target/*.jar | grep -v "original-" | head -n 1) \
    && cp "$JAR" /build/app.jar

# ------ run (JRE) ------
FROM eclipse-temurin:21-jre
WORKDIR /app

# instala curl para o healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /build/app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
