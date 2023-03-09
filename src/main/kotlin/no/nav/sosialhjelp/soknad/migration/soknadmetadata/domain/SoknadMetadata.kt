package no.nav.sosialhjelp.soknad.migration.soknadmetadata.domain

import jakarta.xml.bind.annotation.XmlRootElement
import no.nav.sbl.soknadsosialhjelp.vedlegg.JsonVedlegg
import no.nav.sosialhjelp.soknad.migration.utils.JAXBHelper
import java.time.LocalDateTime

data class SoknadMetadata(
    var id: Long,
    var behandlingsId: String,
    var tilknyttetBehandlingsId: String? = null,
    var fnr: String,
    var skjema: String? = null,
    var orgnr: String? = null,
    var navEnhet: String? = null,
    var fiksForsendelseId: String? = null,
    var vedlegg: VedleggMetadataListe? = null,
    var type: SoknadMetadataType? = null,
    var status: SoknadMetadataInnsendingStatus? = null,
    var opprettetDato: LocalDateTime,
    var sistEndretDato: LocalDateTime,
    var innsendtDato: LocalDateTime? = null,
    var lestDittNav: Boolean = false,
    var oldId: Long
)

@XmlRootElement
data class VedleggMetadataListe(
    var vedleggListe: MutableList<VedleggMetadata> = mutableListOf()
)

@XmlRootElement
data class VedleggMetadata(
    var filUuid: String? = null,
    var filnavn: String? = null,
    var mimeType: String? = null,
    var filStorrelse: String? = null,
    var status: Vedleggstatus? = null,
    var skjema: String? = null,
    var tillegg: String? = null,
    var hendelseType: JsonVedlegg.HendelseType? = null,
    var hendelseReferanse: String? = null
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
