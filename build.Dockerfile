FROM gradle:8.5.0-jdk21 as builder
ARG repoUsername
ARG repoPassword
ENV ORG_GRADLE_PROJECT_repoUsername=$repoUsername
ENV ORG_GRADLE_PROJECT_repoPassword=$repoPassword
ENV ORG_GRADLE_PROJECT_mavenRepository=https://maven.taktik.be/content/groups/public
ENV ORG_GRADLE_PROJECT_mavenReleasesRepository=https://maven.taktik.be/content/repositories/releases/
ENV ORG_GRADLE_PROJECT_mavenSnapshotsRepository=https://maven.taktik.be/content/repositories/snapshots/

WORKDIR /build
COPY . ./

RUN mv ci.settings.kts settings.gradle.kts

# RUN apk --no-cache add bash # for git-version plugin

RUN gradle -x test :dto:publish :domain:publish

RUN rm core/build/libs/*-plain.jar

FROM scratch
COPY --from=builder /build/core/build/libs/*.jar /build/
