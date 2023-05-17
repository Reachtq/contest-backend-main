FROM maven:3.6.3-openjdk-17 AS MAVEN_BUILD
COPY ./ ./
RUN mvn -B -DskipTests clean package

FROM openjdk:17
COPY --from=MAVEN_BUILD /target/contest-back-0.0.1-SNAPSHOT.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
