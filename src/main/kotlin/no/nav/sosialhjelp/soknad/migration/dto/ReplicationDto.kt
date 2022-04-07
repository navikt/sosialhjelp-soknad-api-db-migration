package no.nav.sosialhjelp.soknad.migration.dto

import no.nav.sosialhjelp.soknad.migration.oppgave.dto.OppgaveDto
import no.nav.sosialhjelp.soknad.migration.sendtsoknad.dto.SendtSoknadDto
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.dto.SoknadMetadataDto
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.dto.SoknadUnderArbeidDto

data class ReplicationDto(
    val behandlingsId: String,
    val soknadMetadata: SoknadMetadataDto,
    val soknadUnderArbeid: SoknadUnderArbeidDto?,
    val sendtSoknad: SendtSoknadDto?,
    val oppgave: OppgaveDto?
)
