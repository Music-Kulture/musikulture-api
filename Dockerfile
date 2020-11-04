FROM gradle:6.6.1-jdk8 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:8-jre-slim

EXPOSE 8080

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/musikulture-api-jar.jar

EXPOSE 80 443

CMD ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar /app/musikulture-api-jar.jar"]
