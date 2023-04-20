package no.nav.sosialhjelp.soknad.migration.replication

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class ReplicationServiceTest{

    private val replicationClient: ReplicationClient = mockk()

    private val replicationService = ReplicationService(replicationClient)


    @Test
    internal fun `skal returnere null hvis det ikke er flere entries å replikere`(){
        every { replicationClient.getNext(any()) } returns null
         val replicationDto = replicationService.hentNesteDataForReplikering(LocalDateTime.now())

        assertNull(replicationDto)

    }



//    @Test
//    internal fun `skal replikere til database når det finnes flere entries å replikere`()
}