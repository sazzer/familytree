package uk.co.grahamcox.familytree.webapp

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Unit tests for the Debug Controller
 */
class DebugControllerTest {
    /** The timezone to use */
    private val TIMEZONE = ZoneId.of("UTC")

    /** The time to use */
    private val TIME = ZonedDateTime.of(2016, 1, 6, 12, 33, 25, 0, TIMEZONE).toInstant()

    /** The debug controller to test */
    private lateinit var debugController : DebugController

    /**
     * Set up the controller
     */
    @Before
    fun setup() {
        val clock = Clock.fixed(TIME, TIMEZONE)

        debugController = DebugController(clock)
    }

    /**
     * Get the current time from the controller
     */
    @Test
    fun testNow() {
        val instant = debugController.now()

        Assert.assertEquals(TIME, instant)
    }
}
