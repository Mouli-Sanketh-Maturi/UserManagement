FROM amazoncorretto:17

WORKDIR /opt/app/
EXPOSE 8080
COPY ./gradlew .
COPY ./gradle ./gradle
COPY ./build.gradle .
COPY ./settings.gradle .
COPY ./src ./src

RUN ./gradlew --version
RUN ./gradlew clean build -xtest
RUN cp build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]