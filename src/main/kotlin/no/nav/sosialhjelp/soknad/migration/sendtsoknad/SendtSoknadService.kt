package no.nav.sosialhjelp.soknad.migration.sendtsoknad

import no.nav.sosialhjelp.soknad.migration.sendtsoknad.dto.SendtSoknadDto
import org.springframework.stereotype.Component

@Component
class SendtSoknadService(
    private val sendtSoknadRepository: SendtSoknadRepository
) {

    /**
     * Hvis id finnes fra før, oppdater raden.
     * Hvis ikke, opprett ny rad.
     */
    fun addOrUpdate(dto: SendtSoknadDto) {
        if (sendtSoknadRepository.exists(dto.sendtSoknadId)) {
            sendtSoknadRepository.oppdater(dto)
        } else {
            sendtSoknadRepository.opprett(dto)
        }
    }
}
