FROM ghcr.io/navikt/baseimages/temurin:20
COPY build/libs/sosialhjelp-soknad-api-db-migration.jar app.jar
EXPOSE 8080
