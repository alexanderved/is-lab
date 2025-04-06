FROM gradle:jdk17-alpine AS temp_build_image

ENV APP=/usr/src/client
WORKDIR $APP

COPY ./common common/
COPY ./client client/
RUN mkdir server

COPY ./gradle gradle/
COPY ./build.gradle.kts .
COPY ./settings.gradle.kts .

RUN gradle wrapper
RUN ./gradlew :client:build

FROM openjdk:17-oracle

ENV APP_SRC=/usr/src/client
ENV APP=/usr/client
WORKDIR /usr/client

COPY --from=temp_build_image $APP_SRC/client/build/libs/client.jar .

EXPOSE 4891
CMD ["java", "-jar", "client.jar"]