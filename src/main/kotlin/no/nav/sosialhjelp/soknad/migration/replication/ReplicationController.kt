package no.nav.sosialhjelp.soknad.migration.replication

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
    private val soknadMetadataService: SoknadMetadataService
) {

    @PostMapping("/replicateAll")
    fun replicateAllEntries() {

        var nesteDato = LocalDateTime.MIN

        var nesteEntryForReplikering = replicationService.hentNesteDataForReplikering(nesteDato)

        while (nesteEntryForReplikering!=null && nesteEntryForReplikering.soknadMetadata.sistEndretDato.isAfter(nesteDato))  {

            nesteEntryForReplikering.soknadUnderArbeid?.let {
                soknadUnderArbeidService.addOrUpdate(it)
                it.opplastetVedleggListe.forEach { vedlegg ->
                    opplastetVedleggService.add(vedlegg)
                }
            }

            nesteEntryForReplikering.soknadMetadata.let {
                soknadMetadataService.addOrUpdate(it)
            }

            nesteDato = nesteEntryForReplikering.soknadMetadata.sistEndretDato

            nesteEntryForReplikering = replicationService.hentNesteDataForReplikering(nesteDato)

        }





//        pseudokode:
//        hent data for replikering x
//        Oppdater opplastet_vedlegg x
//        Oppdater soknad_under_arbeid x
//        oppdater soknadmetadata x
//          oppdater oppgave

        // todo: innkommende rest-kall (migrering trigges via curl e.l.)

        // hent siste sistEndretDato, eller bruk LocalDateTime.MIN hvis ingen finnes.
        // Kall replicationService med sistEndretDato og lagre resultat som sistEndretDato
        // Iterere videre med bruk av returnert sistEndretDato som input til replicationService, frem til
        // replicationService returnerer null?
    }

    // todo: endepunkt for å verifisere at replikering er fullført? Se /migration/sjekksum i soknad-api
}
