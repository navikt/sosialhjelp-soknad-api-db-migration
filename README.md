# sosialhjelp-soknad-api-db-migration
Applikasjon for å migrere data fra oracle til postgres

## Henvendelser
Spørsmål knyttet til koden eller teamet kan stilles til teamdigisos@nav.no.

### For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #team_digisos.

## Teknologi
* Kotlin
* JDK 17
* Gradle
* Spring boot
* Postgres

### Krav
* JDK 17

## Annet

### Ktlint
Ktlint brukes for linting av kotlin-kode, vha [ktlint-gradle plugin](https://github.com/JLLeitschuh/ktlint-gradle)\
Sjekk kode: `./gradlew ktlintCheck`\
Formater kode: `./gradlew ktlintFormat`

Man kan endre IntelliJ autoformateringskonfigurasjon for det aktuelle prosjektet: `./gradlew ktlintApplyToIdea`

Installere pre-commit hook lokalt i aktuelt repo, som enten sjekker eller formatterer koden:\
`./gradlew addKtlintCheckGitPreCommitHook` eller `./gradlew addKtlintFormatGitPreCommitHook`
