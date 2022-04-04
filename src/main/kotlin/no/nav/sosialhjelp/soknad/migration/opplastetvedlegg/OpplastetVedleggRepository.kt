package no.nav.sosialhjelp.soknad.migration.opplastetvedlegg

import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.dto.OpplastetVedleggDto
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository

@Repository
class OpplastetVedleggRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun opprett(opplastetVedlegg: OpplastetVedleggDto): String {
        val id: Long = jdbcTemplate.queryForObject("select nextval('opplastet_vedlegg_id_seq')")
        jdbcTemplate.update(
            "insert into opplastet_vedlegg (id, uuid, eier, type, data, soknad_under_arbeid_id, filnavn, sha512) values (?,?,?,?,?,?,?,?)",
            id,
            opplastetVedlegg.uuid,
            opplastetVedlegg.eier,
            opplastetVedlegg.vedleggType.sammensattType,
            opplastetVedlegg.data,
            opplastetVedlegg.soknadId,
            opplastetVedlegg.filnavn,
            opplastetVedlegg.sha512
        )
        return opplastetVedlegg.uuid
    }

    fun slett(uuid: String) {
        jdbcTemplate.update("delete from opplastet_vedlegg where uuid = ?", uuid)
    }

    fun slettAlleVedleggForSoknad(soknadUnderArbeidId: Long) {
        jdbcTemplate.update("delete from opplastet_vedlegg where soknad_under_arbeid_id = ?", soknadUnderArbeidId)
    }
}
