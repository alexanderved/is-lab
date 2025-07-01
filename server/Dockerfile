FROM gradle:jdk17-alpine AS builder

ENV APP=/usr/src/server
WORKDIR $APP

COPY ./common common/
COPY ./server server/
RUN mkdir client

COPY ./gradle gradle/
COPY ./settings.gradle.kts .
COPY ./gradlew .

RUN ./gradlew :server:build

FROM openjdk:17-oracle

ENV APP_SRC=/usr/src/server
ENV APP=/usr/server
WORKDIR /usr/server

RUN microdnf install findutils
RUN microdnf install net-tools

COPY --from=builder $APP_SRC/server/build/libs/server.jar .
COPY --from=builder $APP_SRC/server/build/libs/server-plain.jar .

EXPOSE 8080
CMD ["java", "-jar", "server.jar", "--db.url=jdbc:postgresql://postgres:5432/studs", "--db.user=postgres", "--db.password=postgres", "--minio.endpoint=http://minio:9000"]