package no.nav.sosialhjelp.soknad.migration.replication

import no.nav.sosialhjelp.soknad.migration.oppgave.OppgaveService
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.OpplastetVedleggService
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.SoknadMetadataService
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.SoknadUnderArbeidService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReplicationService(
    private val replicationClient: ReplicationClient,
    private val soknadMetadataService: SoknadMetadataService,
    private val soknadUnderArbeidService: SoknadUnderArbeidService,
    private val opplastetVedleggService: OpplastetVedleggService,
    private val oppgaveService: OppgaveService,
) {

    fun processUpdatesSince(since: LocalDateTime): LocalDateTime? {
        // hent neste Dto for replikering
        val next = replicationClient.getNext(since) ?: return null

        soknadMetadataService.addOrUpdate(next.soknadMetadata)

        next.soknadUnderArbeid?.let {
            soknadUnderArbeidService.addOrUpdate(it)
            it.opplastetVedleggListe.forEach { vedlegg ->
                opplastetVedleggService.add(vedlegg)
            }
        }
        next.oppgave?.let { oppgaveService.addOrUpdate(it) }

        // lagre siste sistEndretDato?

        return next.soknadMetadata.sistEndretDato
    }
}
