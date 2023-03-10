package no.nav.sosialhjelp.soknad.migration.replication

import no.nav.sosialhjelp.soknad.migration.oppgave.dto.OppgaveDto
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.dto.SoknadMetadataDto
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.dto.SoknadUnderArbeidDto

data class ReplicationDto(
    val behandlingsId: String,
    val soknadMetadata: SoknadMetadataDto,
    val soknadUnderArbeid: SoknadUnderArbeidDto?,
    val oppgave: OppgaveDto?
)
