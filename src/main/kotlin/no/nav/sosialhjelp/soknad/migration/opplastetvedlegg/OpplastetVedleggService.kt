package no.nav.sosialhjelp.soknad.migration.opplastetvedlegg

import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.dto.OpplastetVedleggDto
import org.springframework.stereotype.Component

@Component
class OpplastetVedleggService(
    private val opplastetVedleggRepository: OpplastetVedleggRepository
) {

    /**
     * Oppretter ny rad.
     * Det er ikke mulig å oppdatere rader av OpplastetVedlegg i soknad-api, så dette utelates her og.
     */
    fun add(dto: OpplastetVedleggDto) {
        opplastetVedleggRepository.opprett(dto)
    }
}
