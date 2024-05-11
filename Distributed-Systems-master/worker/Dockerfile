FROM openjdk:17-jdk-slim

COPY worker/build/libs/worker-1.0.0-SNAPSHOT.jar worker.jar

ENTRYPOINT ["java","-jar","worker.jar"]