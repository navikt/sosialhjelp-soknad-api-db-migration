package no.nav.sosialhjelp.soknad.migration

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/internal/isAlive")
    fun isAlive(): String {
        return "Ok"
    }

    @GetMapping("/internal/isReady")
    fun isReady(): String {
        return "Ok"
    }
}
