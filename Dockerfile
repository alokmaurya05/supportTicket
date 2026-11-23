FROM eclipse-temurin:17-jre-jammy

ADD target/support-ticket.jar support-ticket.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "support-ticket.jar"]