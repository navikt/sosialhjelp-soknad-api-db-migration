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
        val sendtSoknad = dto.toDomain()

        if (sendtSoknadRepository.exists(sendtSoknad.oldId)) {
            sendtSoknadRepository.oppdater(sendtSoknad)
        } else {
            sendtSoknadRepository.opprett(sendtSoknad)
        }
    }
}
