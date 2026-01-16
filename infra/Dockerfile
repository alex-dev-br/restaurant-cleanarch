# ----- build ------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline   # Baixar e deixar em cache sas dependências do Maven

COPY src ./src
RUN mvn -q -DskipTests package

# ------ run (JRE) ------
FROM eclipse-temurin:21-jre
WORKDIR /app

# instala curl para o helthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copia o jar e padroniza o nome
COPY --from=build /build/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]