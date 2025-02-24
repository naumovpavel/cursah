FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/demo.jar
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]