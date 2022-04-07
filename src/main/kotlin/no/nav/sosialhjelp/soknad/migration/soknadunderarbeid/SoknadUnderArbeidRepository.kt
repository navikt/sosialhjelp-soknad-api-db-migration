package no.nav.sosialhjelp.soknad.migration.soknadunderarbeid

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import no.nav.sbl.soknadsosialhjelp.json.AdresseMixIn
import no.nav.sbl.soknadsosialhjelp.json.JsonSosialhjelpValidator
import no.nav.sbl.soknadsosialhjelp.soknad.JsonInternalSoknad
import no.nav.sbl.soknadsosialhjelp.soknad.adresse.JsonAdresse
import no.nav.sosialhjelp.soknad.migration.opplastetvedlegg.OpplastetVedleggRepository
import no.nav.sosialhjelp.soknad.migration.soknadunderarbeid.domain.SoknadUnderArbeid
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.stereotype.Repository
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallbackWithoutResult
import org.springframework.transaction.support.TransactionTemplate
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.util.Date

@Repository
class SoknadUnderArbeidRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val transactionTemplate: TransactionTemplate,
    private val opplastetVedleggRepository: OpplastetVedleggRepository
) {

    private val mapper: ObjectMapper = ObjectMapper().addMixIn(JsonAdresse::class.java, AdresseMixIn::class.java)
    private val writer: ObjectWriter = mapper.writerWithDefaultPrettyPrinter()

    fun opprett(soknadUnderArbeid: SoknadUnderArbeid): Long {
        val id: Long = jdbcTemplate.queryForObject("select nextval('soknad_under_arbeid_id_seq')")
        jdbcTemplate.update(
            "insert into soknad_under_arbeid (soknad_under_arbeid_id, versjon, behandlingsid, tilknyttetbehandlingsid, eier, data, status, opprettetdato, sistendretdato, old_id) values (?,?,?,?,?,?,?,?,?,?)",
            id,
            soknadUnderArbeid.versjon,
            soknadUnderArbeid.behandlingsId,
            soknadUnderArbeid.tilknyttetBehandlingsId,
            soknadUnderArbeid.eier,
            soknadUnderArbeid.jsonInternalSoknad?.let { mapJsonSoknadInternalTilFil(it) },
            soknadUnderArbeid.status.toString(),
            Date.from(soknadUnderArbeid.opprettetDato.atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(soknadUnderArbeid.sistEndretDato.atZone(ZoneId.systemDefault()).toInstant()),
            soknadUnderArbeid.oldId
        )
        return id
    }

    fun exists(oldId: Long): Boolean {
        return jdbcTemplate.queryForObject("select exists(select 1 from soknad_under_arbeid where old_id = ?)", Boolean::class.java, oldId)
    }

    fun oppdater(soknadUnderArbeid: SoknadUnderArbeid) {
        jdbcTemplate.update(
            "update soknad_under_arbeid set versjon = ?, behandlingsid = ?, tilknyttetbehandlingsid = ?, eier = ?, data = ?, status = ?, sistendretdato = ? where old_id = ?",
            soknadUnderArbeid.versjon,
            soknadUnderArbeid.behandlingsId,
            soknadUnderArbeid.tilknyttetBehandlingsId,
            soknadUnderArbeid.eier,
            soknadUnderArbeid.jsonInternalSoknad?.let { mapJsonSoknadInternalTilFil(it) },
            soknadUnderArbeid.status.toString(),
            Date.from(soknadUnderArbeid.sistEndretDato.atZone(ZoneId.systemDefault()).toInstant()),
            soknadUnderArbeid.oldId
        )
    }

    fun slett(soknadUnderArbeid: SoknadUnderArbeid) {
        transactionTemplate.execute(object : TransactionCallbackWithoutResult() {
            override fun doInTransactionWithoutResult(transactionStatus: TransactionStatus) {
                val id = soknadUnderArbeid.id
                opplastetVedleggRepository.slettAlleVedleggForSoknad(id)
                jdbcTemplate.update("delete from soknad_under_arbeid where soknad_under_arbeid_id = ?", id)
            }
        })
    }

    private fun mapJsonSoknadInternalTilFil(jsonInternalSoknad: JsonInternalSoknad): ByteArray {
        return try {
            val internalSoknad = writer.writeValueAsString(jsonInternalSoknad)
            JsonSosialhjelpValidator.ensureValidInternalSoknad(internalSoknad)
            internalSoknad.toByteArray(StandardCharsets.UTF_8)
        } catch (e: JsonProcessingException) {
            log.error("Kunne ikke konvertere søknadsobjekt til tekststreng", e)
            throw RuntimeException(e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SoknadUnderArbeidRepository::class.java)
    }
}
