package no.nav.sosialhjelp.soknad.migration.soknadunderarbeid

import no.nav.sbl.soknadsosialhjelp.soknad.JsonInternalSoknad
import java.time.LocalDateTime

data class SoknadUnderArbeid(
    var soknadId: Long,
    var versjon: Long,
    var behandlingsId: String,
    var tilknyttetBehandlingsId: String? = null,
    var eier: String,
    var jsonInternalSoknad: JsonInternalSoknad?,
    var status: SoknadUnderArbeidStatus,
    var opprettetDato: LocalDateTime,
    var sistEndretDato: LocalDateTime
)

enum class SoknadUnderArbeidStatus {
    UNDER_ARBEID, LAAST
}
