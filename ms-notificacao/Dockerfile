FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./

RUN mvn -B -q -e -DskipTests dependency:go-offline || true

COPY src ./src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /opt/app

RUN apk add --no-cache curl

COPY --from=build /app/target/ms-notificacao-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]