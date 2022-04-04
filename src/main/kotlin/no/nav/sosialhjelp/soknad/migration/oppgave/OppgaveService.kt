package no.nav.sosialhjelp.soknad.migration.oppgave

import no.nav.sosialhjelp.soknad.migration.oppgave.dto.OppgaveDto
import org.springframework.stereotype.Component

@Component
class OppgaveService(
    private val oppgaveRepository: OppgaveRepository
) {

    /**
     * Hvis dto.id finnes fra før, oppdater raden.
     * Hvis ikke, opprett ny rad.
     */
    fun addOrUpdateOppgave(dto: OppgaveDto) {
        if (oppgaveRepository.exists(dto.id)) {
            oppgaveRepository.oppdater(dto)
        } else {
            oppgaveRepository.opprett(dto)
        }
    }
}
