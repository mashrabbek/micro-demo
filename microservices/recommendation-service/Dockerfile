#FROM eclipse-temurin:17.0.5_8-jre-focal as builder
#WORKDIR extracted
#ADD ./build/libs/*.jar app.jar
#RUN java -Djarmode=tools -jar app.jar extract --layers --launcher
#
#FROM eclipse-temurin:17.0.5_8-jre-focal
#WORKDIR application
#COPY --from=builder extracted/dependencies/ ./
#COPY --from=builder extracted/spring-boot-loader/ ./
#COPY --from=builder extracted/snapshot-dependencies/ ./
#COPY --from=builder extracted/application/ ./
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

FROM eclipse-temurin:17.0.5_8-jre-focal
WORKDIR /application
ADD ./build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]