FROM gradle:jdk17-alpine AS temp_build_image

ENV APP=/usr/src/server
WORKDIR $APP

COPY ./common common/
COPY ./server server/
RUN mkdir client

COPY ./gradle gradle/
COPY ./build.gradle.kts .
COPY ./settings.gradle.kts .
COPY ./gradlew .

RUN ./gradlew :server:build

FROM openjdk:17-oracle

ENV APP_SRC=/usr/src/server
ENV APP=/usr/server
WORKDIR /usr/server

COPY --from=temp_build_image $APP_SRC/server/build/libs/server.jar .

ENV REPO_PATH=repo.xml
RUN echo "<collection></collection>" > $REPO_PATH

EXPOSE 4891
CMD ["java", "-jar", "server.jar"]