package uk.co.grahamcox.familytree.webapp.oauth2

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import uk.co.grahamcox.familytree.webapp.SpringTestBase
import kotlin.collections.forEach
import kotlin.collections.mapOf

/**
 * Integration test for the OAuth2 Controller
 */
@RunWith(JUnitParamsRunner::class)
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

    /**
     * Test requesting an access token with a grant type of password but no username or password specified
     */
    @Test
    @Parameters(method = "parametersForMissingFields")
    fun testMissingFields(grantType: String, fields: Map<String, String>) {
        val request = MockMvcRequestBuilders.post("/api/oauth2/token")
            .param("grant_type", grantType)

        fields.forEach { request.param(it.key, it.value) }
        perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("error").value("invalid_request"))
    }

    /**
     * Test requesting an access token with a grant type of client_credentials but none specified
     */
    @Test
    fun testClientCredentialsMissing() {
        perform(MockMvcRequestBuilders.post("/api/oauth2/token")
            .param("grant_type", "client_credentials"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("error").value("invalid_client"))
    }

    /**
     * Parameters for the Missing Fields tests
     * This returns an array of arrays, where each of the inner arrays contains a grant type and a map
     * of additional parameters to use
     */
    fun parametersForMissingFields() = arrayOf(
        arrayOf("authorization_code", mapOf<String, String>()),
        arrayOf("authorization_code", mapOf("code" to "abcdef")),
        arrayOf("authorization_code", mapOf("redirect_uri" to "http://www.google.com")),
        arrayOf("password", mapOf<String, String>()),
        arrayOf("password", mapOf("username" to "abcdef")),
        arrayOf("password", mapOf("password" to "abcdef")),
        arrayOf("refresh_token", mapOf<String, String>())
    )
}