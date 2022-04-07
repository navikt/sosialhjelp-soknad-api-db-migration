package no.nav.sosialhjelp.soknad.migration.soknadmetadata

import no.nav.sosialhjelp.soknad.migration.soknadmetadata.domain.JAXB
import no.nav.sosialhjelp.soknad.migration.soknadmetadata.dto.SoknadMetadataDto
import no.nav.sosialhjelp.soknad.migration.utils.SQLUtils.tidTilTimestamp
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository

@Repository
class SoknadMetadataRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun opprett(metadata: SoknadMetadataDto) {
        val id: Long = jdbcTemplate.queryForObject("select nextval('soknadmetadata_id_seq')")
        jdbcTemplate.update(
            "INSERT INTO soknadmetadata (id, behandlingsid, tilknyttetBehandlingsId, fnr, skjema, orgnr, navenhet, fiksforsendelseid, vedlegg, soknadtype, innsendingstatus, opprettetdato, sistendretdato, innsendtdato, old_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            id,
            metadata.behandlingsId,
            metadata.tilknyttetBehandlingsId,
            metadata.fnr,
            metadata.skjema,
            metadata.orgnr,
            metadata.navEnhet,
            metadata.fiksForsendelseId,
            metadata.vedlegg?.let { JAXB.marshal(it.toVedleggMedataListe()) },
            metadata.type?.name,
            metadata.status?.name,
            tidTilTimestamp(metadata.opprettetDato),
            tidTilTimestamp(metadata.sistEndretDato),
            tidTilTimestamp(metadata.innsendtDato),
            metadata.id
        )
    }

    fun exists(oldId: Long): Boolean {
        return jdbcTemplate.queryForObject("select exists(select 1 from soknadmetadata where old_id = ?)", Boolean::class.java, oldId)
    }

    fun oppdater(metadata: SoknadMetadataDto) {
        jdbcTemplate.update(
            "update soknadmetadata set behandlingsid = ?, tilknyttetbehandlingsid = ?, fnr = ?, skjema = ?, orgnr = ?, navenhet = ?, fiksforsendelseid = ?, vedlegg = ?, soknadtype = ?, innsendingstatus = ?, sistendretdato = ?, innsendtdato = ? where old_id = ?",
            metadata.behandlingsId,
            metadata.tilknyttetBehandlingsId,
            metadata.fnr,
            metadata.skjema,
            metadata.orgnr,
            metadata.navEnhet,
            metadata.fiksForsendelseId,
            metadata.vedlegg?.let { JAXB.marshal(it.toVedleggMedataListe()) },
            metadata.type?.name,
            metadata.status?.name,
            tidTilTimestamp(metadata.sistEndretDato),
            tidTilTimestamp(metadata.innsendtDato),
            metadata.id
        )
    }

    fun slett(id: Long) {
        jdbcTemplate.update("DELETE FROM soknadmetadata WHERE id = ?", id)
    }
}
