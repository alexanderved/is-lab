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
RUN ./gradlew :server:installDist

FROM openjdk:17-oracle

ENV APP_SRC=/usr/src/server
ENV APP=/usr/server
WORKDIR /usr/server

RUN microdnf install findutils
RUN microdnf install net-tools

COPY --from=builder $APP_SRC/server/build/install/server/bin/ bin/
COPY --from=builder $APP_SRC/server/build/install/server/lib/ lib/

ENV REPO_PATH=repo.xml
RUN echo "<collection></collection>" > $REPO_PATH

EXPOSE 4891
CMD ["bin/server", "localhost:5430/studs", "postgres", "postgres"]