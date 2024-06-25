FROM openjdk:11
EXPOSE 8763
ADD /target/gateway-0.0.1-SNAPSHOT.war gateway-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "gateway-0.0.1-SNAPSHOT.jar"]