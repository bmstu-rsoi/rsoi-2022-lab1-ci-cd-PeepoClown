FROM openjdk:11-jdk
EXPOSE 8080
RUN mkdir /app
WORKDIR app
ADD ./build/libs/rsoi1-cicd-1.0.0-SNAPSHOT.jar ./rsoi1-cicd.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/rsoi1-cicd.jar"]
