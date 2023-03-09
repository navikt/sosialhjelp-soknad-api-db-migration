package no.nav.sosialhjelp.soknad.migration.oppgave.domain

import jakarta.xml.bind.annotation.XmlRootElement
import jakarta.xml.bind.annotation.XmlType
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import no.nav.sosialhjelp.soknad.migration.utils.JAXBHelper
import no.nav.sosialhjelp.soknad.migration.utils.LocalDateTimeXmlAdapter
import java.time.LocalDateTime

data class Oppgave(
    var id: Long,
    var behandlingsId: String,
    var type: String?,
    var status: Status,
    var steg: Int,
    var oppgaveData: FiksData? = FiksData(),
    var oppgaveResultat: FiksResultat? = FiksResultat(),
    var opprettet: LocalDateTime?,
    var sistKjort: LocalDateTime?,
    var nesteForsok: LocalDateTime?,
    var retries: Int,
    var oldId: Long
)

enum class Status {
    KLAR, UNDER_ARBEID, FERDIG, FEILET
}

@XmlRootElement
data class FiksData(
    var behandlingsId: String? = null,
    var avsenderFodselsnummer: String? = null,
    var mottakerOrgNr: String? = null,
    var mottakerNavn: String? = null,
    var dokumentInfoer: List<DokumentInfo>? = null,
    @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter::class)
    var innsendtDato: LocalDateTime? = null,
    var ettersendelsePa: String? = null,
)

@XmlRootElement
@XmlType(name = "fiksDokumentInfo")
data class DokumentInfo(
    var uuid: String? = null,
    var filnavn: String? = null,
    var mimetype: String? = null,
    var ekskluderesFraPrint: Boolean? = false
)

@XmlRootElement
data class FiksResultat(
    var fiksForsendelsesId: String? = null,
    var feilmelding: String? = null
)

val JAXB = JAXBHelper(
    FiksData::class.java,
    FiksResultat::class.java
)
