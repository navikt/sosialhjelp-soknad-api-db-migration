package no.nav.sosialhjelp.soknad.migration.replication

import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.OpplastetVedleggService
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
    private val soknadUnderArbeidService: SoknadUnderArbeidService
) {

    @PostMapping("/replicateAll")
    fun replicateAllEntries() {

        val nesteEntryForReplikering = replicationService.hentNesteDataForReplikering(
            LocalDateTime.MIN
        )

        nesteEntryForReplikering?.soknadUnderArbeid?.let {
            soknadUnderArbeidService.addOrUpdate(it)
            it.opplastetVedleggListe.forEach { vedlegg ->
                opplastetVedleggService.add(vedlegg)
            }
        }


//        pseudokode:
//        hent data for replikering
//        Oppdater opplastet_vedlegg
//        Oppdater soknad_under_arbeid
//        oppdater soknadmetadata
//          oppdater oppgave

        // todo: innkommende rest-kall (migrering trigges via curl e.l.)

        // hent siste sistEndretDato, eller bruk LocalDateTime.MIN hvis ingen finnes.
        // Kall replicationService med sistEndretDato og lagre resultat som sistEndretDato
        // Iterere videre med bruk av returnert sistEndretDato som input til replicationService, frem til
        // replicationService returnerer null?
    }

    // todo: endepunkt for å verifisere at replikering er fullført? Se /migration/sjekksum i soknad-api
}
