package no.nav.sosialhjelp.soknad.migration.sendtsoknad

import no.nav.sosialhjelp.soknad.migration.sendtsoknad.dto.SendtSoknadDto
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import java.time.ZoneId
import java.util.Date

@Repository
class SendtSoknadRepository(
    private val jdbcTemplate: JdbcTemplate,
) {

    fun opprett(sendtSoknad: SendtSoknadDto): Long {
        val sendtSoknadId = jdbcTemplate.queryForObject<Long>("select nextval('sendt_soknad_id_seq')")
        jdbcTemplate.update(
            "insert into sendt_soknad (sendt_soknad_id, behandlingsid, tilknyttetbehandlingsid, eier, fiksforsendelseid, orgnr, navenhetsnavn, brukeropprettetdato, brukerferdigdato, sendtdato, old_id) values (?,?,?,?,?,?,?,?,?,?,?)",
            sendtSoknadId,
            sendtSoknad.behandlingsId,
            sendtSoknad.tilknyttetBehandlingsId,
            sendtSoknad.eier,
            sendtSoknad.fiksforsendelseId,
            sendtSoknad.orgnummer,
            sendtSoknad.navEnhetsnavn,
            Date.from(sendtSoknad.brukerOpprettetDato.atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(sendtSoknad.brukerFerdigDato.atZone(ZoneId.systemDefault()).toInstant()),
            sendtSoknad.sendtDato?.let { Date.from(it.atZone(ZoneId.systemDefault()).toInstant()) },
            sendtSoknad.sendtSoknadId
        )
        return sendtSoknadId
    }

    fun exists(oldId: Long): Boolean {
        return jdbcTemplate.queryForObject("select exists(select 1 from sendt_soknad where old_id = ?)", Boolean::class.java, oldId)
    }

    fun oppdater(sendtSoknad: SendtSoknadDto) {
        jdbcTemplate.update(
            "update sendt_soknad set behandlingsid = ?, tilknyttetbehandlingsid = ?, eier = ?, fiksforsendelseid = ?, orgnummer = ?, navenhetsnavn = ?, brukerferdigdato = ?, sendtdato = ? where old_id = ?",
            sendtSoknad.behandlingsId,
            sendtSoknad.tilknyttetBehandlingsId,
            sendtSoknad.eier,
            sendtSoknad.fiksforsendelseId,
            sendtSoknad.orgnummer,
            sendtSoknad.navEnhetsnavn,
            Date.from(sendtSoknad.brukerFerdigDato.atZone(ZoneId.systemDefault()).toInstant()),
            sendtSoknad.sendtDato?.let { Date.from(it.atZone(ZoneId.systemDefault()).toInstant()) },
            sendtSoknad.sendtSoknadId
        )
    }

    fun slett(sendtSoknadId: Long) {
        jdbcTemplate.update("delete from sendt_soknad where sendt_soknad_id = ?", sendtSoknadId)
    }
}
