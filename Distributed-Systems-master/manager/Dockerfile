FROM openjdk:17-jdk-slim

COPY manager/build/libs/manager-1.0.0-SNAPSHOT.jar manager.jar

ENTRYPOINT ["java","-jar","manager.jar"]