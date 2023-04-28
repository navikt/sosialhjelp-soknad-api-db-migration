package no.nav.sosialhjelp.soknad.migration.replication

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.OpplastetVedleggRepository
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.OpplastetVedleggService
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.domain.OpplastetVedlegg
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.domain.OpplastetVedleggType
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.dto.OpplastetVedleggDto
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.SoknadMetadataRepository
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.SoknadMetadataService
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.domain.SoknadMetadata
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.dto.SoknadMetadataDto
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.SoknadUnderArbeidRepository
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.SoknadUnderArbeidService
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.domain.SoknadUnderArbeid
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.domain.SoknadUnderArbeidStatus
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.dto.SoknadUnderArbeidDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class ReplicationControllerTest {

    private val replicationServiceMock: ReplicationService = mockk()
    private val opplastedVedleggRepositoryMock: OpplastetVedleggRepository = mockk()
    private val soknadUnderArbeidRepositoryMock: SoknadUnderArbeidRepository = mockk()
    private val soknadMetadataRepositoryMock: SoknadMetadataRepository = mockk()

    private val opplastetVedleggService = OpplastetVedleggService(opplastedVedleggRepositoryMock)
    private val soknadUnderArbeidService = SoknadUnderArbeidService(soknadUnderArbeidRepositoryMock)
    private val soknadMetadataService = SoknadMetadataService(soknadMetadataRepositoryMock)

    private val replicationController =
        ReplicationController(
            replicationServiceMock,
            opplastetVedleggService,
            soknadUnderArbeidService,
            soknadMetadataService
        )

    @Test
    internal fun `skal oppdatere vedlegg tabell når replikeringsdata inneholder vedlegg som ikke er replikert fra før`() {

        val opplastetVedleggSlot = slot<OpplastetVedlegg>()
        val replikeringsrespons = lagReplikeringsresponsMedVedlegg(LocalDateTime.now())

        every { replicationServiceMock.hentNesteDataForReplikering(any()) } returns replikeringsrespons

        every { soknadMetadataRepositoryMock.exists(any()) } returns false
        every { soknadMetadataRepositoryMock.opprett(any()) } just Runs

        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns false
        every { soknadUnderArbeidRepositoryMock.opprett(any()) } returns 1L

        every { opplastedVedleggRepositoryMock.opprett(capture(opplastetVedleggSlot)) } returns UUID.randomUUID()
            .toString()
        every { opplastedVedleggRepositoryMock.exists(any()) } returns false

        replicationController.replicateAllEntries()

//        TODO vurdere å validere hele vedleggsobjektet - iom at dette er en engagsjobb med mye manuell test i forkant, er det mulig dette holder.
        assertThat(opplastetVedleggSlot.captured.eier).isEqualTo(replikeringsrespons.soknadUnderArbeid?.opplastetVedleggListe?.first()?.eier)
    }

    @Test
    internal fun `skal prosessere alle vedlegg`() {
        val opplastetVedleggListe: MutableList<OpplastetVedlegg> = mutableListOf()
        every { replicationServiceMock.hentNesteDataForReplikering(any()) } returns lagReplikeringsresponsMedFlereVedlegg(
            LocalDateTime.now()
        )
        every { soknadMetadataRepositoryMock.exists(any()) } returns false
        every { soknadMetadataRepositoryMock.opprett(any()) } just Runs

        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns false
        every { soknadUnderArbeidRepositoryMock.opprett(any()) } returns 1L

        every { opplastedVedleggRepositoryMock.opprett(capture(opplastetVedleggListe)) } returns UUID.randomUUID()
            .toString()
        every { opplastedVedleggRepositoryMock.exists(any()) } returns false

        replicationController.replicateAllEntries()

        assertThat(opplastetVedleggListe).size().isEqualTo(3)
    }

    @Test
    internal fun `skal opprette søknad under arbeid når soknad ikke eksisterer fra før`() {
        val soknadUnderArbeidSlot = slot<SoknadUnderArbeid>()

        val replikeringsrespons = lagReplikeringsresponsUtenVedlegg(LocalDateTime.now())
        every { soknadMetadataRepositoryMock.exists(any()) } returns false
        every { soknadMetadataRepositoryMock.opprett(any()) } just Runs

        every { replicationServiceMock.hentNesteDataForReplikering(any()) } returns replikeringsrespons

        every { soknadUnderArbeidRepositoryMock.opprett(capture(soknadUnderArbeidSlot)) } returns 1L
        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns false

        replicationController.replicateAllEntries()

        verify(exactly = 1) { soknadUnderArbeidRepositoryMock.opprett(any()) }
        verify(exactly = 0) { soknadUnderArbeidRepositoryMock.oppdater(any()) }
        assertThat(soknadUnderArbeidSlot.captured.eier).isEqualTo(replikeringsrespons.soknadUnderArbeid?.eier)
    }

    @Test
    internal fun `skal oppdatere søknad under arbeid dersom den finnes fra før`() {
        val soknadUnderArbeidSlot = slot<SoknadUnderArbeid>()

        val replikeringsrespons = lagReplikeringsresponsUtenVedlegg(LocalDateTime.now())
        every { soknadMetadataRepositoryMock.exists(any()) } returns false
        every { soknadMetadataRepositoryMock.opprett(any()) } just Runs

        every { replicationServiceMock.hentNesteDataForReplikering(any()) } returns replikeringsrespons

        every { soknadUnderArbeidRepositoryMock.oppdater(capture(soknadUnderArbeidSlot)) } just Runs

        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns true

        replicationController.replicateAllEntries()

        verify(exactly = 1) { soknadUnderArbeidRepositoryMock.oppdater(any()) }
        verify(exactly = 0) { soknadUnderArbeidRepositoryMock.opprett(any()) }
        assertThat(soknadUnderArbeidSlot.captured.eier).isEqualTo(replikeringsrespons.soknadUnderArbeid?.eier)
    }

    @Test
    internal fun `skal opprette alle soknader under arbeid`() {

        val soknadUnderArbeidCaptureListe: MutableList<SoknadUnderArbeid> = mutableListOf()

        val replikeringsresponsForste = lagReplikeringsresponsUtenVedlegg(LocalDateTime.now().minusDays(20))
        val replikeringsresponsAndre = lagReplikeringsresponsMedFlereVedlegg(LocalDateTime.now().minusDays(10))
        val replikeringsresponsTredje = lagReplikeringsresponsMedVedlegg(LocalDateTime.now().minusDays(1))

        every { replicationServiceMock.hentNesteDataForReplikering(LocalDateTime.MIN) } returns replikeringsresponsForste

        every {
            replicationServiceMock.hentNesteDataForReplikering(
                replikeringsresponsForste.soknadUnderArbeid?.sistEndretDato ?: LocalDateTime.now()
            )
        } returns replikeringsresponsAndre

        every {
            replicationServiceMock.hentNesteDataForReplikering(
                replikeringsresponsAndre.soknadUnderArbeid?.sistEndretDato ?: LocalDateTime.now()
            )
        } returns replikeringsresponsTredje

        every {
            replicationServiceMock.hentNesteDataForReplikering(
                replikeringsresponsTredje.soknadUnderArbeid?.sistEndretDato ?: LocalDateTime.now()
            )
        } returns null

        every { soknadMetadataRepositoryMock.exists(any()) } returns false
        every { soknadMetadataRepositoryMock.opprett(any()) } just Runs

        every { opplastedVedleggRepositoryMock.exists(any()) } returns false
        every { opplastedVedleggRepositoryMock.opprett(any()) } returns UUID.randomUUID().toString()

        every { soknadUnderArbeidRepositoryMock.opprett(capture(soknadUnderArbeidCaptureListe)) } returns 1L
        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns false

        replicationController.replicateAllEntries()

        assertThat(soknadUnderArbeidCaptureListe).size().isEqualTo(3)
        verify(exactly = 3) { soknadUnderArbeidRepositoryMock.opprett(any()) }
    }

    @Test
    internal fun `skal opprette søknad metadata dersom det ikke finnes fra før`() {

        val soknadMetadataSlot = slot<SoknadMetadata>()

        val replikeringsrespons = lagReplikeringsresponsUtenVedlegg(LocalDateTime.now())

        every { replicationServiceMock.hentNesteDataForReplikering(any()) } returns replikeringsrespons

        every { soknadUnderArbeidRepositoryMock.opprett(any()) } returns 1L
        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns false

        every { soknadMetadataRepositoryMock.exists(any()) } returns false
        every { soknadMetadataRepositoryMock.opprett(capture(soknadMetadataSlot)) } just Runs

        replicationController.replicateAllEntries()

        verify(exactly = 1) { soknadMetadataRepositoryMock.opprett(any()) }
        verify(exactly = 0) { soknadMetadataRepositoryMock.oppdater(any()) }
        assertThat(soknadMetadataSlot.captured.fnr).isEqualTo(replikeringsrespons.soknadMetadata.fnr)
    }

    @Test
    internal fun `skal opprette soknadmetadata for alle forekomster`() {

        val soknadMetadataCaptureListe: MutableList<SoknadMetadata> = mutableListOf()

        val replikeringsresponsForste = lagReplikeringsresponsUtenVedlegg(LocalDateTime.now().minusDays(20))
        val replikeringsresponsAndre = lagReplikeringsresponsMedFlereVedlegg(LocalDateTime.now().minusDays(10))
        val replikeringsresponsTredje = lagReplikeringsresponsMedVedlegg(LocalDateTime.now().minusDays(1))

        every { replicationServiceMock.hentNesteDataForReplikering(LocalDateTime.MIN) } returns replikeringsresponsForste

        every {
            replicationServiceMock.hentNesteDataForReplikering(
                replikeringsresponsForste.soknadUnderArbeid?.sistEndretDato ?: LocalDateTime.now()
            )
        } returns replikeringsresponsAndre

        every {
            replicationServiceMock.hentNesteDataForReplikering(
                replikeringsresponsAndre.soknadUnderArbeid?.sistEndretDato ?: LocalDateTime.now()
            )
        } returns replikeringsresponsTredje

        every {
            replicationServiceMock.hentNesteDataForReplikering(
                replikeringsresponsTredje.soknadUnderArbeid?.sistEndretDato ?: LocalDateTime.now()
            )
        } returns null

        every { opplastedVedleggRepositoryMock.exists(any()) } returns false
        every { opplastedVedleggRepositoryMock.opprett(any()) } returns UUID.randomUUID().toString()

        every { soknadUnderArbeidRepositoryMock.opprett(any()) } returns 1L
        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns false

        every { soknadMetadataRepositoryMock.exists(any()) } returns false
        every { soknadMetadataRepositoryMock.opprett(capture(soknadMetadataCaptureListe)) } just Runs

        replicationController.replicateAllEntries()

        assertThat(soknadMetadataCaptureListe).size().isEqualTo(3)
        verify(exactly = 3) { soknadMetadataRepositoryMock.opprett(any()) }
    }

    private fun lagReplikeringsresponsUtenVedlegg(sistEndretDato: LocalDateTime): ReplicationDto {
        return ReplicationDto(
            "123",
            createSoknadMetadata(sistEndretDato),
            createSoknaUnderArbeidUtenVedlegg(sistEndretDato),
            null
        )
    }

    private fun lagReplikeringsresponsMedFlereVedlegg(sistEndretDato: LocalDateTime): ReplicationDto {

        return ReplicationDto(
            "123",
            createSoknadMetadata(sistEndretDato),
            createSoknaUnderArbeidMedFlereVedlegg(sistEndretDato),
            null
        )
    }

    private fun lagReplikeringsresponsMedVedlegg(sistEndretDato: LocalDateTime): ReplicationDto {

        return ReplicationDto(
            "123",
            createSoknadMetadata(sistEndretDato),
            createSoknaUnderArbeidMedVedlegg(sistEndretDato),
            null
        )
    }

    private fun createSoknadMetadata(sistEndretDato: LocalDateTime): SoknadMetadataDto {

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
            sistEndretDato = sistEndretDato,
            innsendtDato = LocalDateTime.now(),
            lestDittNav = false
        )
    }

    private fun createSoknaUnderArbeidUtenVedlegg(sistEndretDato: LocalDateTime): SoknadUnderArbeidDto {

        return SoknadUnderArbeidDto(
            soknadId = 123,
            versjon = 456,
            behandlingsId = "id",
            tilknyttetBehandlingsId = null,
            eier = "fnr",
            jsonInternalSoknad = null,
            status = SoknadUnderArbeidStatus.UNDER_ARBEID,
            opprettetDato = LocalDateTime.now(),
            sistEndretDato = sistEndretDato,
            opplastetVedleggListe = emptyList()
        )
    }

    private fun createSoknaUnderArbeidMedVedlegg(sistEndretDato: LocalDateTime): SoknadUnderArbeidDto {

        val opplastetVedleggListe = listOf(createOpplastetVedleggDto())

        return SoknadUnderArbeidDto(
            soknadId = 123,
            versjon = 456,
            behandlingsId = "id",
            tilknyttetBehandlingsId = null,
            eier = "fnr",
            jsonInternalSoknad = null,
            status = SoknadUnderArbeidStatus.UNDER_ARBEID,
            opprettetDato = LocalDateTime.now(),
            sistEndretDato = sistEndretDato,
            opplastetVedleggListe = opplastetVedleggListe
        )
    }

    private fun createSoknaUnderArbeidMedFlereVedlegg(sistEndretDato: LocalDateTime): SoknadUnderArbeidDto {

        val opplastetVedleggListe =
            listOf(createOpplastetVedleggDto(), createOpplastetVedleggDto(), createOpplastetVedleggDto())

        return SoknadUnderArbeidDto(
            soknadId = 123,
            versjon = 456,
            behandlingsId = "id",
            tilknyttetBehandlingsId = null,
            eier = "fnr",
            jsonInternalSoknad = null,
            status = SoknadUnderArbeidStatus.UNDER_ARBEID,
            opprettetDato = LocalDateTime.now(),
            sistEndretDato = sistEndretDato,
            opplastetVedleggListe = opplastetVedleggListe
        )
    }
}

private fun createOpplastetVedleggDto(): OpplastetVedleggDto {

    return OpplastetVedleggDto(
        uuid = UUID.randomUUID().toString(),
        eier = "fnr",
        vedleggType = OpplastetVedleggType("annet|annet"),
        data = "hello".toByteArray(),
        soknadId = 123,
        filnavn = "filnavn",
        sha512 = "sha"
    )
}
