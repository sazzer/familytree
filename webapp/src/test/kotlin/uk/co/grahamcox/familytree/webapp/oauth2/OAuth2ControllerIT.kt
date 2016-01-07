package uk.co.grahamcox.familytree.webapp.oauth2

import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import uk.co.grahamcox.familytree.webapp.SpringTestBase

/**
 * Integration test for the OAuth2 Controller
 */
class OAuth2ControllerIT : SpringTestBase() {
    /**
     * Test requesting an access token with no grant type specified
     */
    @Test
    fun testTokenNoGrantType() {
        perform(MockMvcRequestBuilders.post("/api/oauth2/token"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("error").value("invalid_request"))
    }

    /**
     * Test requesting an access token with no grant type specified
     */
    @Test
    fun testTokenBadGrantType() {
        perform(MockMvcRequestBuilders.post("/api/oauth2/token")
            .param("grant_type", "unknown"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("error").value("unsupported_grant_type"))
    }
}