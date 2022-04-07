package no.nav.sosialhjelp.soknad.migration.soknadmetadata.dto

import no.nav.sbl.soknadsosialhjelp.vedlegg.JsonVedlegg
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.domain.SoknadMetadata
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.domain.SoknadMetadataInnsendingStatus
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.domain.SoknadMetadataType
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.domain.VedleggMetadata
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.domain.VedleggMetadataListe
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.domain.Vedleggstatus
import java.time.LocalDateTime

data class SoknadMetadataDto(
    val id: Long,
    val behandlingsId: String,
    val tilknyttetBehandlingsId: String?,
    val fnr: String,
    val skjema: String?,
    val orgnr: String?,
    val navEnhet: String?,
    val fiksForsendelseId: String?,
    val vedlegg: VedleggMetadataListeDto?,
    val type: SoknadMetadataType?,
    val status: SoknadMetadataInnsendingStatus?,
    val opprettetDato: LocalDateTime,
    val sistEndretDato: LocalDateTime,
    val innsendtDato: LocalDateTime?,
    val lestDittNav: Boolean
) {
    fun toDomain(): SoknadMetadata {
        return SoknadMetadata(
            id = 0L, // dummy id
            behandlingsId = behandlingsId,
            tilknyttetBehandlingsId = tilknyttetBehandlingsId,
            fnr = fnr,
            skjema = skjema,
            orgnr = orgnr,
            navEnhet = navEnhet,
            fiksForsendelseId = fiksForsendelseId,
            vedlegg = vedlegg?.toVedleggMedataListe(),
            type = type,
            status = status,
            opprettetDato = opprettetDato,
            sistEndretDato = sistEndretDato,
            innsendtDato = innsendtDato,
            lestDittNav = lestDittNav,
            oldId = id
        )
    }
}

data class VedleggMetadataListeDto(
    val vedleggListe: MutableList<VedleggMetadataDto>
) {
    fun toVedleggMedataListe(): VedleggMetadataListe {
        return VedleggMetadataListe(
            vedleggListe = vedleggListe.map { it.toVedleggMetadata() }.toMutableList()
        )
    }
}

data class VedleggMetadataDto(
    val filUuid: String?,
    val filnavn: String?,
    val mimeType: String?,
    val filStorrelse: String?,
    val status: Vedleggstatus?,
    val skjema: String?,
    val tillegg: String?,
    val hendelseType: JsonVedlegg.HendelseType?,
    val hendelseReferanse: String?
) {
    fun toVedleggMetadata(): VedleggMetadata {
        return VedleggMetadata(
            filUuid = filUuid,
            filnavn = filnavn,
            mimeType = mimeType,
            filStorrelse = filStorrelse,
            status = status,
            skjema = skjema,
            tillegg = tillegg,
            hendelseType = hendelseType,
            hendelseReferanse = hendelseReferanse
        )
    }
}
