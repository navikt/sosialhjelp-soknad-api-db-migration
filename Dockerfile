FROM ghcr.io/navikt/baseimages/temurin:17
COPY build/libs/sosialhjelp-soknad-api-db-migration.jar app.jar
EXPOSE 8080
