package no.nav.sosialhjelp.soknad.migration.utils

import java.sql.Timestamp
import java.time.LocalDateTime

object SQLUtils {

    fun tidTilTimestamp(tid: LocalDateTime?): Timestamp? {
        return if (tid != null) Timestamp.valueOf(tid) else null
    }
}
