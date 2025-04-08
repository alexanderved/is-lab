FROM gradle:jdk17-alpine AS builder

ENV APP=/usr/src/client
WORKDIR $APP

COPY ./common common/
COPY ./client client/
RUN mkdir server

COPY ./gradle gradle/
COPY ./settings.gradle.kts .
COPY ./gradlew .

RUN ./gradlew :client:build
RUN ./gradlew :client:installDist

FROM openjdk:17-oracle

ENV APP_SRC=/usr/src/client
ENV APP=/usr/client
WORKDIR /usr/client

RUN microdnf install findutils

COPY ./test.script .
COPY --from=builder $APP_SRC/client/build/install/client/bin/ bin/
COPY --from=builder $APP_SRC/client/build/install/client/lib/ lib/

CMD ["bin/client"]