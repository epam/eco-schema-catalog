FROM openjdk:8-jdk-alpine as build

# Update SSL so that wget can read https sites
RUN apk update
RUN apk add ca-certificates wget && update-ca-certificates

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY client/src client/src
COPY client/pom.xml client/pom.xml
COPY commons/src commons/src
COPY commons/pom.xml commons/pom.xml
COPY fts/src fts/src
COPY fts/pom.xml fts/pom.xml
COPY rest/src rest/src
COPY rest/pom.xml rest/pom.xml
COPY store/src store/src
COPY store/pom.xml store/pom.xml

RUN chmod +x ./mvnw

RUN ./mvnw clean package -P packageOnly

FROM openjdk:8-jdk-alpine

WORKDIR /app

VOLUME /config

COPY --from=build /app/rest/target/schema-catalog-rest-*.jar /app/schema-catalog-rest.jar

ENV SERVER_PORT=${SERVER_PORT:-8082}

HEALTHCHECK --interval=5s --timeout=1s --retries=20 CMD wget --quiet --tries=1 --spider http://localhost:$SERVER_PORT/actuator/health || exit 1

ENTRYPOINT exec java $JAVA_OPTS -jar schema-catalog-rest.jar
