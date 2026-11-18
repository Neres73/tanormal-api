# Etapa 1: compila o projeto com Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# Etapa 2: imagem leve só com o Java para rodar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/tanormal-api.jar app.jar
ENV TZ=America/Sao_Paulo
EXPOSE 8080
# -Xmx baixo para caber no plano gratuito do Render (512 MB)
ENTRYPOINT ["java", "-Xmx300m", "-jar", "app.jar"]
