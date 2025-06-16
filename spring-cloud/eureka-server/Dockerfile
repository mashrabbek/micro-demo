FROM eclipse-temurin:17.0.5_8-jre-focal

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"] 