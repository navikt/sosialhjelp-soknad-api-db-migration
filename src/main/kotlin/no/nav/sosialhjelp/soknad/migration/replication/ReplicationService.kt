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

//    TODO fjerne
//    fun processUpdatesSince(since: LocalDateTime): LocalDateTime? {
//        // hent neste Dto for replikering
//        val next = replicationClient.getNext(since) ?: return null
//
//        soknadMetadataService.addOrUpdate(next.soknadMetadata)
//
//        next.soknadUnderArbeid?.let {
//            soknadUnderArbeidService.addOrUpdate(it)
//            it.opplastetVedleggListe.forEach { vedlegg ->
//                opplastetVedleggService.add(vedlegg)
//            }
//        }
//        next.oppgave?.let { oppgaveService.addOrUpdate(it) }
//
//        // lagre siste sistEndretDato?
//
//        return next.soknadMetadata.sistEndretDato
//    }
}
