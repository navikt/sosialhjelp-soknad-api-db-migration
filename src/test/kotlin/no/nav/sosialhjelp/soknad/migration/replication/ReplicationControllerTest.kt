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

    private val opplastetVedleggService = OpplastetVedleggService(opplastedVedleggRepositoryMock)
    private val soknadUnderArbeidService = SoknadUnderArbeidService(soknadUnderArbeidRepositoryMock)

    private val replicationController =
        ReplicationController(replicationServiceMock, opplastetVedleggService, soknadUnderArbeidService)


    @Test
    internal fun `skal oppdatere vedlegg tabell når replikeringsdata inneholder vedlegg som ikke er replikert fra før`() {

        val opplastetVedleggSlot = slot<OpplastetVedlegg>()
        val replikeringsrespons = lagReplikeringsresponsMedVedlegg(LocalDateTime.now())
        every { replicationServiceMock.hentNesteDataForReplikering(any()) } returns replikeringsrespons
        every { opplastedVedleggRepositoryMock.opprett(capture(opplastetVedleggSlot)) } returns UUID.randomUUID()
            .toString()
        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns false
        every { soknadUnderArbeidRepositoryMock.opprett(any()) } returns 1L
        every { opplastedVedleggRepositoryMock.exists(any()) } returns false

        replicationController.replicateAllEntries()

//        TODO vurdere å validere hele vedleggsobjektet - iom at dette er en engagsjobb med mye manuell test i forkant, er det mulig dette holder.
        assertThat(opplastetVedleggSlot.captured.eier).isEqualTo(replikeringsrespons?.soknadUnderArbeid?.opplastetVedleggListe?.first()?.eier)

    }

    @Test
    internal fun `skal prosessere alle vedlegg`() {
        val opplastetVedleggListe: MutableList<OpplastetVedlegg> = mutableListOf()
        every { replicationServiceMock.hentNesteDataForReplikering(any()) } returns lagReplikeringsresponsMedFlereVedlegg(
            LocalDateTime.now()
        )
        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns false
        every { soknadUnderArbeidRepositoryMock.opprett(any()) } returns 1L
        every { opplastedVedleggRepositoryMock.opprett(capture(opplastetVedleggListe)) } returns UUID.randomUUID()
            .toString()
        every { opplastedVedleggRepositoryMock.exists(any()) } returns false

        replicationController.replicateAllEntries()
        println(opplastetVedleggListe.toString())
        assertThat(opplastetVedleggListe).size().isEqualTo(3)


    }

    @Test
    internal fun `skal oppdatere soknad under arbeid`() {
        val soknadUnderArbeidSlot = slot<SoknadUnderArbeid>()

        val replikeringsrespons = lagReplikeringsresponsUtenVedlegg(LocalDateTime.now())
        every { replicationServiceMock.hentNesteDataForReplikering(any()) } returns replikeringsrespons
        every { soknadUnderArbeidRepositoryMock.opprett(capture(soknadUnderArbeidSlot)) } returns 1L
        every { soknadUnderArbeidRepositoryMock.exists(any()) } returns false

        replicationController.replicateAllEntries()

        assertThat(soknadUnderArbeidSlot.captured.eier).isEqualTo(replikeringsrespons?.soknadUnderArbeid?.eier)


    }

    private fun lagReplikeringsresponsUtenVedlegg(sistEndretDato: LocalDateTime): ReplicationDto? {
        return ReplicationDto("123", createSoknadMetadata(), createSoknaUnderArbeidUtenVedlegg(sistEndretDato), null)

    }


    private fun lagReplikeringsresponsMedFlereVedlegg(sistEndretDato: LocalDateTime): ReplicationDto? {

        return ReplicationDto(
            "123",
            createSoknadMetadata(),
            createSoknaUnderArbeidMedFlereVedlegg(sistEndretDato),
            null
        )
    }


    private fun lagReplikeringsresponsMedVedlegg(sistEndretDato: LocalDateTime): ReplicationDto? {

        return ReplicationDto("123", createSoknadMetadata(), createSoknaUnderArbeidMedVedlegg(sistEndretDato), null)

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

    private fun createSoknaUnderArbeidMedFlereVedlegg(sistEndretDato: LocalDateTime): SoknadUnderArbeidDto? {


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
