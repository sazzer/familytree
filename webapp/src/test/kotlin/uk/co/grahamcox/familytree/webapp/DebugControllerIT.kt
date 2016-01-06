package uk.co.grahamcox.familytree.webapp

import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * Integration test for the Debug Controller
 */
class DebugControllerIT : SpringTestBase() {
    /**
     * Test the Now handler works as expected
     */
    @Test
    fun testNow() {
        perform(get("/api/debug/now"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value("2016-01-06T12:33:25Z"))
    }
}
