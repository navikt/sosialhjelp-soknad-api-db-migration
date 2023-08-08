
object Versions {
    const val springboot = "3.1.2"
    const val kotlin = "1.8.10"
    const val coroutines = "1.6.4"
    const val flyway = "9.15.2"
    const val postgresql = "42.5.4"
    const val jackson = "2.14.2"
    const val micrometer = "1.10.4"
    const val javaJwt = "4.3.0"
    const val jwksRsa = "0.22.0"
    const val logstash = "7.3"
    const val nimbusJoseJwt = "9.31"
    const val jakartaXml = "4.0.0"
    const val filformat = "1.2023.06.21-14.54-583dfcc41d77"

    // constraints
    const val snakeYaml = "2.0"
    const val netty = "4.1.94.Final"

    // test
    const val junit = "5.9.2"
    const val mockk = "1.13.3"
    const val h2 = "2.2.220"
}

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.9.0"
    id("org.springframework.boot") version "3.1.2"
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1"
    id("org.flywaydb.flyway") version "9.21.0"
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

    testRuntimeOnly("com.h2database:h2:${Versions.h2}")
    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.junit}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${Versions.springboot}")

    constraints {
        implementation("org.yaml:snakeyaml:${Versions.snakeYaml}") {
            because("https://github.com/advisories/GHSA-mjmj-j48q-9wg2")
        }
        implementation("io.netty:netty-handler:${Versions.netty}") {
            because("https://github.com/advisories/GHSA-6mjq-h674-j845")
        }
    }
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
