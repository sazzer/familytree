package uk.co.grahamcox.familytree.webapp

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.Clock

@Controller
@RequestMapping("/api/debug")
class DebugController(private val clock: Clock) {
    /**
     * Get the current time
     * @return the current time
     */
    @RequestMapping("/now")
    @ResponseBody
    fun now() = clock.instant()
}
