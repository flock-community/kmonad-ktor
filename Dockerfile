FROM openjdk:17-alpine

WORKDIR /app
COPY build/libs/kmonad-*.jar /app
EXPOSE 8080

CMD java -jar kmonad-*.jar
