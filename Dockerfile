FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD ./out/artifacts/magic_api_jar/magic-api.jar magic-api.jar
ENTRYPOINT ["java", "-jar", "/magic-api.jar"]