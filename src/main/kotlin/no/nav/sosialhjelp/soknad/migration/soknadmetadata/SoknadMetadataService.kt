package no.nav.sosialhjelp.soknad.migration.soknadmetadata

import no.nav.sosialhjelp.soknad.migration.soknadmetadata.dto.SoknadMetadataDto
import org.springframework.stereotype.Component

@Component
class SoknadMetadataService(
    private val soknadMetadataRepository: SoknadMetadataRepository
) {

    /**
     * Hvis id finnes fra før, oppdater raden.
     * Hvis ikke, opprett ny rad.
     */
    fun addOrUpdate(dto: SoknadMetadataDto) {
        if (soknadMetadataRepository.exists(dto.id)) {
            soknadMetadataRepository.oppdater(dto)
        } else {
            soknadMetadataRepository.opprett(dto)
        }
    }
}
