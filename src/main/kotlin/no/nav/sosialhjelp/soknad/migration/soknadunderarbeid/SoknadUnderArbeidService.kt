package no.nav.sosialhjelp.soknad.migration.soknadunderarbeid

import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.dto.SoknadUnderArbeidDto
import org.springframework.stereotype.Component

@Component
class SoknadUnderArbeidService(
    private val soknadUnderArbeidRepository: SoknadUnderArbeidRepository
) {

    /**
     * Hvis id finnes fra før, oppdater raden.
     * Hvis ikke, opprett ny rad.
     */
    fun addOrUpdate(dto: SoknadUnderArbeidDto) {
        if (soknadUnderArbeidRepository.exists(dto.soknadId)) {
            soknadUnderArbeidRepository.oppdater(dto)
        } else {
            soknadUnderArbeidRepository.opprett(dto)
        }
    }
}
