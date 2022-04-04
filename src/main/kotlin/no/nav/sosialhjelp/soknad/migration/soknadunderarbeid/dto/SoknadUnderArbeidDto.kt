package no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.dto

import no.nav.sbl.soknadsosialhjelp.soknad.JsonInternalSoknad
import java.time.LocalDateTime

data class SoknadUnderArbeidDto(
    val soknadId: Long,
    val versjon: Long,
    val behandlingsId: String,
    val tilknyttetBehandlingsId: String?,
    val eier: String,
    val jsonInternalSoknad: JsonInternalSoknad?,
    val status: SoknadUnderArbeidStatus,
    val opprettetDato: LocalDateTime,
    val sistEndretDato: LocalDateTime
)

enum class SoknadUnderArbeidStatus {
    UNDER_ARBEID, LAAST
}
