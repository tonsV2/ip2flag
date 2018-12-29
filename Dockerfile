FROM openjdk:8-jdk-alpine as builder
WORKDIR /src
ADD . /src
RUN ./gradlew -DskipTests clean bootJar

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=builder /src/build/libs/*-SNAPSHOT.jar .
USER guest
CMD java -jar *.jar
