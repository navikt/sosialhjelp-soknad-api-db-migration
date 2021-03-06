
object Versions {
    const val springboot = "2.6.6"
    const val kotlin = "1.6.10"
    const val coroutines = "1.6.0"
    const val flyway = "8.5.5"
    const val postgresql = "42.3.3"
    const val jackson = "2.13.2"
    const val micrometer = "1.8.4"
    const val javaJwt = "3.19.1"
    const val jwksRsa = "0.21.1"
    const val logstash = "7.0.1"
    const val nimbusJoseJwt = "9.21"
    const val jakartaXml = "2.3.3"
    const val jaxbJavaTimeAdapters = "1.1.3"
    const val filformat = "1.2022.03.31-14.09-4daafcd63deb"

    // test
    const val junit = "5.8.2"
    const val mockk = "1.12.3"
    const val h2 = "2.1.210"
}

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.6"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("org.flywaydb.flyway") version "8.5.5"
}

group = "no.nav.sosialhjelp"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

ktlint {
    this.version.set("0.45.1")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("org.springframework.boot:spring-boot-starter-webflux:${Versions.springboot}")
    implementation("org.springframework.boot:spring-boot-starter-actuator:${Versions.springboot}")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:${Versions.springboot}")

    implementation("org.flywaydb:flyway-core:${Versions.flyway}")
    implementation("org.postgresql:postgresql:${Versions.postgresql}")

    implementation("io.micrometer:micrometer-registry-prometheus:${Versions.micrometer}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.coroutines}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}")
    implementation("com.auth0:java-jwt:${Versions.javaJwt}")
    implementation("com.auth0:jwks-rsa:${Versions.jwksRsa}")
    implementation("com.nimbusds:nimbus-jose-jwt:${Versions.nimbusJoseJwt}")
    implementation("net.logstash.logback:logstash-logback-encoder:${Versions.logstash}")

    implementation("no.nav.sbl.dialogarena:soknadsosialhjelp-filformat:${Versions.filformat}")

    // xml
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:${Versions.jakartaXml}")
    implementation("com.migesok:jaxb-java-time-adapters:${Versions.jaxbJavaTimeAdapters}")

    testRuntimeOnly("com.h2database:h2:${Versions.h2}")
    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${Versions.springboot}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    this.archiveFileName.set("sosialhjelp-soknad-api-db-migration.jar")
}
