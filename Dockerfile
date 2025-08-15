FROM openjdk:17-jdk-slim

COPY ./build/libs/*.war /app/mazegen.war

WORKDIR /app

CMD ["java", "-jar", "mazegen.war"]

