package no.nav.sosialhjelp.soknad.migration.replication

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReplicationService(
    private val replicationClient: ReplicationClient
) {

    fun hentNesteDataForReplikering(since: LocalDateTime): ReplicationDto? {

        return replicationClient.getNext(since)
    }
}
