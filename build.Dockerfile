FROM eclipse-temurin:21.0.1_12-jdk-alpine as builder
ARG repoUsername
ARG repoPassword
ENV ORG_GRADLE_PROJECT_repoUsername=$repoUsername
ENV ORG_GRADLE_PROJECT_repoPassword=$repoPassword

WORKDIR /build
COPY . ./
RUN apk --no-cache add bash # for git-version plugin

RUN ./gradlew -x test :lite-core:build -Dicure.optional.regions=be
RUN ./gradlew -x test :lite-core:publish -Dicure.optional.regions=be

RUN rm lite-core/build/libs/*-plain.jar

FROM scratch
COPY --from=builder /build/lite-core/build/libs/*.jar /build/
