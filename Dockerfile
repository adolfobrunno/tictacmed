FROM eclipse-temurin:24-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle gradle
COPY src src

RUN chmod +x gradlew
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:24-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
