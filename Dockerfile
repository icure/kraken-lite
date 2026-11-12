# Runtime image for kraken-lite.
#
# Build the boot jar first, then the image:
#   ./gradlew -x test :lite-core:bootJar
#   docker build -t kraken-lite .
#
# Configuring Spring at runtime (any of these, combinable):
#   1. Environment variables (Spring relaxed binding):
#        docker run -e ICURE_COUCHDB_URL=http://couchdb:5984 -e SERVER_PORT=8080 kraken-lite
#   2. Command-line arguments (appended after the image name):
#        docker run kraken-lite --icure.couchdb.url=http://couchdb:5984 --spring.profiles.active=app
#   3. A whole JSON blob:
#        docker run -e SPRING_APPLICATION_JSON='{"icure":{"couchdb":{"url":"http://couchdb:5984"}}}' kraken-lite
#   4. Properties/YAML files mounted in /app/config (picked up automatically by Spring Boot):
#        docker run -v ./my-config:/app/config:ro kraken-lite
#
# Extra JVM options (heap, debug, ...) go through JAVA_TOOL_OPTIONS:
#   docker run -e JAVA_TOOL_OPTIONS="-Xmx2g" kraken-lite

FROM gcr.io/distroless/java25-debian13:nonroot

WORKDIR /app
# The *-plain.jar is excluded by .dockerignore, so this matches only the boot jar.
COPY lite-core/build/libs/lite-core-*.jar /app/kraken-lite.jar

EXPOSE 16043

ENTRYPOINT ["/usr/bin/java", \
    "-XX:+HeapDumpOnOutOfMemoryError", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-Dfile.encoding=UTF-8", \
    "-Djava.net.preferIPv4Stack=true", \
    "-Djava.awt.headless=true", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "--add-modules", "java.se", \
    "--add-exports", "java.base/jdk.internal.ref=ALL-UNNAMED", \
    "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
    "--add-opens", "java.base/java.nio=ALL-UNNAMED", \
    "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", \
    "--add-opens", "java.management/sun.management=ALL-UNNAMED", \
    "--add-opens", "jdk.management/com.sun.management.internal=ALL-UNNAMED", \
    "-jar", "/app/kraken-lite.jar"]
# Arguments passed to `docker run <image> ...` replace this and are forwarded to Spring.
CMD []
