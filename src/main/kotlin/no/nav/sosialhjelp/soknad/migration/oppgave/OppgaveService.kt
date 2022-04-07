package no.nav.sosialhjelp.soknad.migration.oppgave

import no.nav.sosialhjelp.soknad.migration.oppgave.dto.OppgaveDto
import org.springframework.stereotype.Component

@Component
class OppgaveService(
    private val oppgaveRepository: OppgaveRepository
) {

    /**
     * Hvis id finnes fra før, oppdater oppgave.
     * Hvis ikke, opprett ny oppgave.
     */
    fun addOrUpdate(dto: OppgaveDto) {
        val oppgave = dto.toDomain()

        if (oppgaveRepository.exists(oppgave.oldId)) {
            oppgaveRepository.oppdater(oppgave)
        } else {
            oppgaveRepository.opprett(oppgave)
        }
    }
}
