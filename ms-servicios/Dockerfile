FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/ms-servicios-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=docker"]