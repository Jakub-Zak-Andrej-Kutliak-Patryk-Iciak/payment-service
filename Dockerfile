FROM openjdk:17-oracle
ADD build/libs/payment-service-0.0.1-SNAPSHOT.jar payment-service-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "payment-service-0.0.1-SNAPSHOT.jar"]
