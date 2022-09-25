FROM openjdk

COPY /build/libs/Bomberman-0.0.1-SNAPSHOT.jar /usr/src/myapp/Bomberman.jar

WORKDIR /usr/src/myapp

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "Bomberman.jar"]
