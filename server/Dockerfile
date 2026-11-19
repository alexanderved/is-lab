FROM gradle:jdk21-alpine AS builder

ENV APP=/usr/src/server
WORKDIR $APP

COPY ./common common/
COPY ./server server/
RUN mkdir client

COPY ./gradle gradle/
COPY ./settings.gradle.kts .
COPY ./gradlew .

RUN ./gradlew :server:war

FROM quay.io/wildfly/wildfly:41.0.1.Final-jdk21

ENV APP_SRC=/usr/src/server
ENV JBOSS_HOME=/opt/jboss/wildfly
ENV DEPLOYMENT_DIR=$JBOSS_HOME/standalone/deployments
ENV ECLIPSELINK_MODULE_DIR=$JBOSS_HOME/modules/system/layers/base/org/eclipse/persistence/main

ARG ECLIPSELINK_VERSION=5.0.2
ARG ASM_VERSION=9.10.1

USER root

RUN microdnf install -y findutils net-tools curl unzip && \
    microdnf clean all

COPY --from=builder $APP_SRC/server/build/libs/server.war $DEPLOYMENT_DIR/

RUN set -eux; \
    mkdir -p "$ECLIPSELINK_MODULE_DIR"; \
    curl -fsSL -o "$ECLIPSELINK_MODULE_DIR/eclipselink.jar" \
      "https://repo.maven.apache.org/maven2/org/eclipse/persistence/eclipselink/${ECLIPSELINK_VERSION}/eclipselink-${ECLIPSELINK_VERSION}.jar"; \
    curl -fsSL -o "$ECLIPSELINK_MODULE_DIR/asm.jar" \
      "https://repo.maven.apache.org/maven2/org/ow2/asm/asm/${ASM_VERSION}/asm-${ASM_VERSION}.jar"; \
    curl -fsSL -o "$ECLIPSELINK_MODULE_DIR/asm-commons.jar" \
      "https://repo.maven.apache.org/maven2/org/ow2/asm/asm-commons/${ASM_VERSION}/asm-commons-${ASM_VERSION}.jar"; \
    curl -fsSL -o "$ECLIPSELINK_MODULE_DIR/asm-tree.jar" \
      "https://repo.maven.apache.org/maven2/org/ow2/asm/asm-tree/${ASM_VERSION}/asm-tree-${ASM_VERSION}.jar"; \
    test -s "$ECLIPSELINK_MODULE_DIR/eclipselink.jar"; \
    test -s "$ECLIPSELINK_MODULE_DIR/asm.jar"; \
    test -s "$ECLIPSELINK_MODULE_DIR/asm-commons.jar"; \
    test -s "$ECLIPSELINK_MODULE_DIR/asm-tree.jar"; \
    JIPIJA_JAR="$(basename "$(find "$ECLIPSELINK_MODULE_DIR" -maxdepth 1 -type f -name 'jipijapa-eclipselink-*.jar' -print -quit)")"; \
    test -n "$JIPIJA_JAR"; \
    printf 'Using WildFly JPA adapter: %s\n' "$JIPIJA_JAR"; \
    cat > "$ECLIPSELINK_MODULE_DIR/module.xml" <<EOF_MODULE
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.eclipse.persistence">
    <resources>
        <resource-root path="${JIPIJA_JAR}"/>
        <resource-root path="eclipselink.jar">
            <filter>
                <exclude path="jakarta/**"/>
            </filter>
        </resource-root>
        <!-- EclipseLink 5.0.2 requires the standard OW2 ASM artifacts at runtime. -->
        <resource-root path="asm.jar"/>
        <resource-root path="asm-commons.jar"/>
        <resource-root path="asm-tree.jar"/>
    </resources>
    <dependencies>
        <module name="java.logging"/>
        <module name="java.management"/>
        <module name="java.naming"/>
        <module name="java.sql"/>
        <module name="java.instrument"/>
        <module name="java.rmi"/>
        <module name="java.desktop"/>
        <module name="jakarta.annotation.api"/>
        <module name="jakarta.enterprise.api"/>
        <module name="jakarta.json.api" optional="true"/>
        <module name="jakarta.persistence.api"/>
        <module name="jakarta.transaction.api"/>
        <module name="jakarta.validation.api"/>
        <module name="jakarta.xml.bind.api"/>
        <module name="org.antlr"/>
        <module name="org.jboss.as.jpa.spi"/>
        <module name="org.jboss.logging"/>
        <module name="org.jboss.vfs"/>
    </dependencies>
</module>
EOF_MODULE

RUN mkdir -p "$JBOSS_HOME/modules/system/layers/base/org/postgresql/main" && \
    curl -fsSL -o "$JBOSS_HOME/modules/system/layers/base/org/postgresql/main/postgresql-42.7.5.jar" \
      "https://repo.maven.apache.org/maven2/org/postgresql/postgresql/42.7.5/postgresql-42.7.5.jar" && \
    cat > "$JBOSS_HOME/modules/system/layers/base/org/postgresql/main/module.xml" <<'EOF_PG'
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="org.postgresql">
    <resources>
        <resource-root path="postgresql-42.7.5.jar"/>
    </resources>
    <dependencies>
        <module name="java.desktop"/>
        <module name="java.logging"/>
        <module name="java.management"/>
        <module name="java.naming"/>
        <module name="java.security.jgss"/>
        <module name="java.security.sasl"/>
        <module name="java.sql"/>
        <module name="java.transaction.xa"/>
        <module name="java.xml"/>
    </dependencies>
</module>
EOF_PG

USER jboss

RUN $JBOSS_HOME/bin/jboss-cli.sh --commands="embed-server --server-config=standalone.xml,/subsystem=undertow/application-security-domain=other:write-attribute(name=integrated-jaspi,value=false),stop-embedded-server"
RUN $JBOSS_HOME/bin/add-user.sh -u admin -p admin --silent

RUN $JBOSS_HOME/bin/jboss-cli.sh --commands="embed-server --server-config=standalone.xml,\
    /subsystem=undertow/application-security-domain=other:write-attribute(name=integrated-jaspi, value=false),\
    /subsystem=undertow/application-security-domain=other:write-attribute(name=enable-jacc, value=true),\
    /subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql,driver-class-name=org.postgresql.Driver),\
    /subsystem=datasources/data-source=PostgresDS:add(jndi-name=java:jboss/datasources/PostgresDS, driver-name=postgresql, connection-url=jdbc:postgresql://postgres:5432/studs, user-name=postgres, password=postgres, enabled=true),\
    stop-embedded-server"

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
