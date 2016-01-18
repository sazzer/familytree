package uk.co.grahamcox.familytree.webapp

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.Clock
import kotlin.collections.mapOf

@Controller
@RequestMapping("/api/debug")
open class DebugController(private val clock: Clock) {
    /**
     * Get the current time
     * @return the current time
     */
    @RequestMapping("/now")
    @ResponseBody
    open fun now() = clock.instant()

    /**
     * Get the current user details
     * @return the current user details
     */
    @RequestMapping("/whoami")
    @ResponseBody
    open fun whoami() = SecurityContextHolder.getContext().authentication ?: mapOf("authenticated" to false)
}
