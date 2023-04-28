package no.nav.sosialhjelp.soknad.migration.replication

import no.nav.sosialhjelp.soknad.migration.oppgave.OppgaveService
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.OpplastetVedleggService
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.SoknadMetadataService
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.SoknadUnderArbeidService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDateTime

@Controller
@RequestMapping("/migration")
class ReplicationController(
    private val replicationService: ReplicationService,
    private val opplastetVedleggService: OpplastetVedleggService,
    private val soknadUnderArbeidService: SoknadUnderArbeidService,
    private val soknadMetadataService: SoknadMetadataService,
    private val oppgaveService: OppgaveService
) {

    @PostMapping("/replicateAll")
    fun replicateAllEntries() {

        var nesteDato = LocalDateTime.MIN

        var nesteEntryForReplikering = replicationService.hentNesteDataForReplikering(nesteDato)

        while (nesteEntryForReplikering != null && nesteEntryForReplikering.soknadMetadata.sistEndretDato.isAfter(nesteDato)) {

            nesteEntryForReplikering.soknadUnderArbeid?.let {
                soknadUnderArbeidService.addOrUpdate(it)
                it.opplastetVedleggListe.forEach { vedlegg ->
                    opplastetVedleggService.add(vedlegg)
                }
            }

            nesteEntryForReplikering.soknadMetadata.let {
                soknadMetadataService.addOrUpdate(it)
            }

            nesteEntryForReplikering.oppgave?.let {
                oppgaveService.addOrUpdate(it)
            }

            nesteDato = nesteEntryForReplikering.soknadMetadata.sistEndretDato

            nesteEntryForReplikering = replicationService.hentNesteDataForReplikering(nesteDato)
        }
    }

    // TODO:
//  endepunkt for å verifisere at replikering er fullført? Se /migration/sjekksum i soknad-api
//  sletting av data
}
