FROM gradle:8.11.1-jdk21 AS build

WORKDIR /app

COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./

RUN ./gradlew --no-daemon build -x test

COPY src src

RUN ./gradlew --no-daemon build -x test

FROM openjdk:21-jdk-slim AS runtime

RUN apt-get update && apt-get install -y bash curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/build/libs/store-manager.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/app/app.jar"]