package no.nav.sosialhjelp.soknad.migration.oppgave.dto

import no.nav.sosialhjelp.soknad.migration.oppgave.domain.DokumentInfo
import no.nav.sosialhjelp.soknad.migration.oppgave.domain.FiksData
import no.nav.sosialhjelp.soknad.migration.oppgave.domain.FiksResultat
import no.nav.sosialhjelp.soknad.migration.oppgave.domain.Oppgave
import no.nav.sosialhjelp.soknad.migration.oppgave.domain.Status
import java.time.LocalDateTime

data class OppgaveDto(
    val id: Long,
    val behandlingsId: String,
    val type: String?,
    val status: Status,
    val steg: Int,
    val oppgaveData: FiksDataDto?,
    val oppgaveResultat: FiksResultatDto?,
    val opprettet: LocalDateTime?,
    val sistKjort: LocalDateTime?,
    val nesteForsok: LocalDateTime?,
    val retries: Int
) {
    fun toDomain(): Oppgave {
        return Oppgave(
            id = 0L, // dummy verdi
            behandlingsId = behandlingsId,
            type = type,
            status = status,
            steg = steg,
            oppgaveData = oppgaveData?.toFiksData(),
            oppgaveResultat = oppgaveResultat?.toFiksResultat(),
            opprettet = opprettet,
            sistKjort = sistKjort,
            nesteForsok = nesteForsok,
            retries = retries,
            oldId = id
        )
    }
}

data class FiksDataDto(
    val behandlingsId: String?,
    val avsenderFodselsnummer: String?,
    val mottakerOrgNr: String?,
    val mottakerNavn: String?,
    val dokumentInfoer: List<DokumentInfoDto>?,
    val innsendtDato: LocalDateTime?,
    val ettersendelsePa: String?,
) {
    fun toFiksData(): FiksData {
        return FiksData(
            behandlingsId = behandlingsId,
            avsenderFodselsnummer = avsenderFodselsnummer,
            mottakerOrgNr = mottakerOrgNr,
            mottakerNavn = mottakerNavn,
            dokumentInfoer = dokumentInfoer?.map { it.toDokumentInfo() }?.toMutableList(),
            innsendtDato = innsendtDato,
            ettersendelsePa = ettersendelsePa
        )
    }
}

data class DokumentInfoDto(
    val uuid: String?,
    val filnavn: String?,
    val mimetype: String?,
    val ekskluderesFraPrint: Boolean?
) {
    fun toDokumentInfo(): DokumentInfo {
        return DokumentInfo(
            uuid = uuid, filnavn = filnavn, mimetype = mimetype, ekskluderesFraPrint = ekskluderesFraPrint
        )
    }
}

data class FiksResultatDto(
    val fiksForsendelsesId: String?,
    val feilmelding: String?
) {
    fun toFiksResultat(): FiksResultat {
        return FiksResultat(
            fiksForsendelsesId = fiksForsendelsesId, feilmelding = feilmelding
        )
    }
}
