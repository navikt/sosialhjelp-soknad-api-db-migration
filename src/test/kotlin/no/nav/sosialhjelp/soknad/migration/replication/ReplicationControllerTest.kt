package no.nav.sosialhjelp.soknad.migration.replication

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.OpplastetVedleggRepository
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.OpplastetVedleggService
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.domain.OpplastetVedlegg
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.domain.OpplastetVedleggType
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.dto.OpplastetVedleggDto
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.dto.SoknadMetadataDto
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.domain.SoknadUnderArbeidStatus
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.dto.SoknadUnderArbeidDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class ReplicationControllerTest {

    private val replicationServiceMock: ReplicationService = mockk()
    private val opplastedVedleggRepositoryMock: OpplastetVedleggRepository = mockk()

    private val opplastetVedleggService = OpplastetVedleggService(opplastedVedleggRepositoryMock)

    private val replicationController = ReplicationController(replicationServiceMock, opplastetVedleggService)


    @Test
    internal fun `skal oppdatere vedlegg tabell når replikeringsdata inneholder vedlegg`() {

        val opplastetVedleggSlot = slot<OpplastetVedlegg>()
        every { replicationServiceMock.hentNesteDataForReplikering(any()) } returns lagReplikeringsresponsMedVedlegg()
        every { opplastedVedleggRepositoryMock.opprett(capture(opplastetVedleggSlot)) }
        every { opplastedVedleggRepositoryMock.opprett(any()) } returns UUID.randomUUID().toString()

        replicationController.replicateAllEntries()

        assertThat(opplastetVedleggSlot.captured.eier).isEqualTo("fnr")

    }

    private fun lagReplikeringsresponsMedVedlegg(): ReplicationDto? {

        return ReplicationDto("123", createSoknadMetadata(), createSoknaUnderArbeidMedVedlegg(), null)

    }

    private fun createSoknadMetadata(): SoknadMetadataDto {

        return SoknadMetadataDto(
            id = 1L,
            behandlingsId = "id",
            tilknyttetBehandlingsId = "behandlingsId",
            fnr = "fnr",
            skjema = null,
            orgnr = null,
            navEnhet = null,
            fiksForsendelseId = null,
            vedlegg = null,
            type = null,
            status = null,
            opprettetDato = LocalDateTime.now(),
            sistEndretDato = LocalDateTime.now(),
            innsendtDato = LocalDateTime.now(),
            lestDittNav = false
        )
    }

    private fun createSoknaUnderArbeidMedVedlegg(): SoknadUnderArbeidDto {

        val opplastetVedleggListe = listOf(
            OpplastetVedleggDto(
                uuid = UUID.randomUUID().toString(),
                eier = "fnr",
                vedleggType = OpplastetVedleggType("annet|annet"),
                data = "hello".toByteArray(),
                soknadId = 123,
                filnavn = "filnavn",
                sha512 = "sha"
            )
        )

        return SoknadUnderArbeidDto(
            soknadId = 123,
            versjon = 456,
            behandlingsId = "id",
            tilknyttetBehandlingsId = null,
            eier = "fnr",
            jsonInternalSoknad = null,
            status = SoknadUnderArbeidStatus.UNDER_ARBEID,
            opprettetDato = LocalDateTime.now(),
            sistEndretDato = LocalDateTime.now(),
            opplastetVedleggListe = opplastetVedleggListe
        )
    }
}