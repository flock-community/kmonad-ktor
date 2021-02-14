FROM openjdk:12-alpine

WORKDIR /app
COPY build/libs/ktor-demo-*-SNAPSHOT.jar /app
EXPOSE 8080

CMD java -jar ktor-demo-*-SNAPSHOT.jar
