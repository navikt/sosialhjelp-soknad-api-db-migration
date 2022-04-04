package no.nav.sosialhjelp.soknad.migration.sendtsoknad.domain

import java.time.LocalDateTime

data class SendtSoknad(
    var sendtSoknadId: Long,
    var behandlingsId: String,
    var tilknyttetBehandlingsId: String? = null,
    var eier: String,
    var fiksforsendelseId: String? = null,
    var orgnummer: String,
    var navEnhetsnavn: String,
    var brukerOpprettetDato: LocalDateTime,
    var brukerFerdigDato: LocalDateTime,
    var sendtDato: LocalDateTime? = null,
    var oldId: Long
)
