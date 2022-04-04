package no.nav.sosialhjelp.soknad.migration.oppgave

import no.nav.sosialhjelp.soknad.migration.oppgave.dto.JAXB
import no.nav.sosialhjelp.soknad.migration.oppgave.dto.OppgaveDto
import no.nav.sosialhjelp.soknad.migration.utils.SQLUtils.tidTilTimestamp
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository

@Repository
class OppgaveRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun opprett(oppgave: OppgaveDto) {
        val id: Long = jdbcTemplate.queryForObject("select nextval('oppgave_id_seq')")
        jdbcTemplate.update(
            "insert into oppgave (id, behandlingsid, type, status, steg, oppgavedata, oppgaveresultat, opprettet, sistkjort, nesteforsok, retries, old_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            id,
            oppgave.behandlingsId,
            oppgave.type,
            oppgave.status.name,
            oppgave.steg,
            oppgave.oppgaveData?.let { JAXB.marshal(it) },
            oppgave.oppgaveResultat?.let { JAXB.marshal(it) },
            tidTilTimestamp(oppgave.opprettet),
            tidTilTimestamp(oppgave.sistKjort),
            tidTilTimestamp(oppgave.nesteForsok),
            oppgave.retries,
            oppgave.id
        )
    }

    fun exists(oldId: Long): Boolean {
        return jdbcTemplate.queryForObject("select exists(select 1 from oppgave where old_id = ?)", Boolean::class.java, oldId)
    }

    fun oppdater(oppgave: OppgaveDto) {
        jdbcTemplate.update(
            "update oppgave set behandlingsid = ?, type = ?, status = ?, steg = ?, oppgavedata = ?, oppgaveresultat = ?, sistkjort = ?, nesteforsok = ?, retries = ? where old_id = ?",
            oppgave.behandlingsId,
            oppgave.type,
            oppgave.status.name,
            oppgave.steg,
            oppgave.oppgaveData?.let { JAXB.marshal(it) },
            oppgave.oppgaveResultat?.let { JAXB.marshal(it) },
            tidTilTimestamp(oppgave.sistKjort),
            tidTilTimestamp(oppgave.nesteForsok),
            oppgave.retries,
            oppgave.id
        )
    }

    fun slettOppgave(id: Long) {
        jdbcTemplate.update("delete from oppgave where id = ?", id)
    }
}
