FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/assessment-0.0.1-SNAPSHOT.jar /app/assessment.jar
EXPOSE 1234
CMD ["java", "-jar", "assessment.jar"]
