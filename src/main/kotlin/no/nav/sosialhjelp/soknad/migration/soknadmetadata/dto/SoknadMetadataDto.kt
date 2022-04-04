package no.nav.sosialhjelp.soknad.migration.soknadmetadata.dto

import no.nav.sbl.soknadsosialhjelp.vedlegg.JsonVedlegg
import no.nav.sosialhjelp.soknad.migration.utils.JAXBHelper
import java.time.LocalDateTime
import javax.xml.bind.annotation.XmlRootElement

data class SoknadMetadataDto(
    val id: Long,
    val behandlingsId: String,
    val tilknyttetBehandlingsId: String?,
    val fnr: String,
    val skjema: String?,
    val orgnr: String?,
    val navEnhet: String?,
    val fiksForsendelseId: String?,
    val vedlegg: VedleggMetadataListe?,
    val type: SoknadMetadataType?,
    val status: SoknadMetadataInnsendingStatus?,
    val opprettetDato: LocalDateTime,
    val sistEndretDato: LocalDateTime,
    val innsendtDato: LocalDateTime?,
    val lestDittNav: Boolean
)

@XmlRootElement
data class VedleggMetadataListe(
    val vedleggListe: MutableList<VedleggMetadata>
)

@XmlRootElement
data class VedleggMetadata(
    val filUuid: String?,
    val filnavn: String?,
    val mimeType: String?,
    val filStorrelse: String?,
    val status: Vedleggstatus?,
    val skjema: String?,
    val tillegg: String?,
    val hendelseType: JsonVedlegg.HendelseType?,
    val hendelseReferanse: String?
)

enum class SoknadMetadataType {
    SEND_SOKNAD_KOMMUNAL, SEND_SOKNAD_KOMMUNAL_ETTERSENDING
}

enum class SoknadMetadataInnsendingStatus {
    UNDER_ARBEID, FERDIG, AVBRUTT_AV_BRUKER, AVBRUTT_AUTOMATISK, SENDT_MED_DIGISOS_API
}

enum class Vedleggstatus {
    VedleggKreves, LastetOpp, VedleggAlleredeSendt;

    fun er(status: Vedleggstatus): Boolean {
        return this == status
    }
}

val JAXB = JAXBHelper(
    VedleggMetadata::class.java,
    VedleggMetadataListe::class.java
)
