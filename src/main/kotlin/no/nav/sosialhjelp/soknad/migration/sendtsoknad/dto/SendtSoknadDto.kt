package no.nav.sosialhjelp.soknad.migration.sendtsoknad.dto

import no.nav.sosialhjelp.soknad.migration.sendtsoknad.domain.SendtSoknad
import java.time.LocalDateTime

data class SendtSoknadDto(
    val sendtSoknadId: Long,
    val behandlingsId: String,
    val tilknyttetBehandlingsId: String?,
    val eier: String,
    val fiksforsendelseId: String?,
    val orgnummer: String,
    val navEnhetsnavn: String,
    val brukerOpprettetDato: LocalDateTime,
    val brukerFerdigDato: LocalDateTime,
    val sendtDato: LocalDateTime?
) {
    fun toDomain(): SendtSoknad {
        return SendtSoknad(
            sendtSoknadId = 0L, // dummy id
            behandlingsId = behandlingsId,
            tilknyttetBehandlingsId = tilknyttetBehandlingsId,
            eier = eier,
            fiksforsendelseId = fiksforsendelseId,
            orgnummer = orgnummer,
            navEnhetsnavn = navEnhetsnavn,
            brukerOpprettetDato = brukerOpprettetDato,
            brukerFerdigDato = brukerFerdigDato,
            sendtDato = sendtDato,
            oldId = sendtSoknadId
        )
    }
}
