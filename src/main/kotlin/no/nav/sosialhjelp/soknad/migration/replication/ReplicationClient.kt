package no.nav.sosialhjelp.soknad.migration.replication

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.LocalDateTime

@Component
class ReplicationClient(
    @Value("\${soknad_api_migration_api_url}") private val migrationApiBaseurl: String,
    webClientBuilder: WebClient.Builder
) {
    private val soknadApiClient = webClientBuilder
        .baseUrl(migrationApiBaseurl)
        .codecs {
            it.defaultCodecs().maxInMemorySize(150 * 1024 * 1024)
        }
        .build()

    /**
     * Hent neste Soknad for migrering
     */
    fun getNext(since: LocalDateTime): ReplicationDto? {
        return soknadApiClient
            .get()
            .uri("/feed?sistEndretDato={sistEndretDato}", since)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono<ReplicationDto>()
            .block()
    }

    /**
     * Hent sjekksum (sum av rader) fra oracle-db
     */
    fun getSjekkSum(): SjekksumDto? {
        return soknadApiClient
            .get()
            .uri("/sjekksum")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono<SjekksumDto>()
            .block()
    }
}
