package no.nav.sosialhjelp.soknad.migration.oppgave.dto

import com.migesok.jaxb.adapter.javatime.LocalDateTimeXmlAdapter
import no.nav.sosialhjelp.soknad.migration.utils.JAXBHelper
import java.time.LocalDateTime
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

// dto fra soknad-api
data class OppgaveDto(
    val id: Long,
    val behandlingsId: String,
    val type: String?,
    val status: Status,
    val steg: Int,
    val oppgaveData: FiksData?,
    val oppgaveResultat: FiksResultat?,
    val opprettet: LocalDateTime?,
    val sistKjort: LocalDateTime?,
    val nesteForsok: LocalDateTime?,
    val retries: Int
)

enum class Status {
    KLAR, UNDER_ARBEID, FERDIG, FEILET
}

@XmlRootElement
data class FiksData(
    val behandlingsId: String? = null,
    val avsenderFodselsnummer: String? = null,
    val mottakerOrgNr: String? = null,
    val mottakerNavn: String? = null,
    val dokumentInfoer: List<DokumentInfo>? = null,
    @XmlJavaTypeAdapter(LocalDateTimeXmlAdapter::class)
    val innsendtDato: LocalDateTime? = null,
    val ettersendelsePa: String? = null,
)

@XmlRootElement
@XmlType(name = "fiksDokumentInfo")
data class DokumentInfo(
    val uuid: String? = null,
    val filnavn: String? = null,
    val mimetype: String? = null,
    val ekskluderesFraPrint: Boolean? = false
)

@XmlRootElement
data class FiksResultat(
    val fiksForsendelsesId: String? = null,
    val feilmelding: String? = null
)

val JAXB = JAXBHelper(
    FiksData::class.java,
    FiksResultat::class.java
)
