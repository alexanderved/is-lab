FROM openjdk:17-oracle

WORKDIR /app

RUN mkdir -p common/
COPY ./common common/

RUN mkdir -p server/src
RUN mkdir -p server/META-INF
COPY ./server/src server/src
COPY ./server/META-INF server/META-INF

RUN ls
RUN javac -cp "common/src/:server/src/" -d build/server server/src/server/Main.java common/src/common/network/responses/* common/src/common/network/requests/*
RUN jar cfm build/server.jar server/META-INF/MANIFEST.MF -C build/server .

CMD ["java", "-jar build/server.jar"]