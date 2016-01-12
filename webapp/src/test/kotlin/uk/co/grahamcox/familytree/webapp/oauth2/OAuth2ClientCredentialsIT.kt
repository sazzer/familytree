package uk.co.grahamcox.familytree.webapp.oauth2

import it.ozimov.cirneco.hamcrest.java7.base.IsBetweenInclusive
import org.easymock.EasyMock
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import uk.co.grahamcox.familytree.oauth2.Password
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.client.ClientDao
import uk.co.grahamcox.familytree.oauth2.client.ClientDetails
import uk.co.grahamcox.familytree.oauth2.client.ClientId
import uk.co.grahamcox.familytree.user.UserId
import uk.co.grahamcox.familytree.webapp.SpringTestBase
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Integration Test for requesting a Client Credentials Access Token
 */
class OAuth2ClientCredentialsIT : SpringTestBase() {
    /** Some client details to use */
    private val CLIENT_DETAILS = ClientDetails(id = ClientId("graham"),
            secret = Password.hash("1234"),
            name = "Dummy Client",
            created = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
            updated = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
            owner = UserId(UUID.randomUUID().toString()),
            scopes = Scopes(arrayOf("a", "b", "c")))

    /** The Client DAO */
    @Autowired
    private lateinit var clientDao: ClientDao

    /**
     * Reset the Client DAO so that we can set expectations correctly
     */
    @Before
    fun setupClientDao() {
        EasyMock.reset(clientDao)
    }

    /**
     * Verify the Client DAO was called as expected
     */
    @After
    fun verifyClientDao() {
        EasyMock.verify(clientDao)
    }

    /**
     * Replay all of the mocks
     */
    fun replayAll() {
        EasyMock.replay(clientDao)
    }

    /**
     * Test making a request where the provided client credentials are not valid
     */
    @Test
    fun requestWithBadClientId() {
        EasyMock.expect(clientDao.loadById(ClientId("abcd")))
                .andReturn(null)
        replayAll()
        perform(MockMvcRequestBuilders.post("/api/oauth2/token")
                .param("grant_type", "client_credentials")
                .header("Authorization", "Basic YWJjZDoxMjM0")) // abcd:1234
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("invalid_client"))
    }

    /**
     * Test making a request where the provided client credentials are not valid
     */
    @Test
    fun requestWithBadPassword() {
        EasyMock.expect(clientDao.loadById(ClientId("graham")))
                .andReturn(CLIENT_DETAILS)
        replayAll()
        perform(MockMvcRequestBuilders.post("/api/oauth2/token")
                .param("grant_type", "client_credentials")
                .header("Authorization", "Basic Z3JhaGFtOnBhc3N3b3Jk")) // graham:password
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("invalid_client"))
    }

    /**
     * Test making a request where the provided client credentials are not present
     */
    @Test
    fun requestWithNoCredentials() {
        replayAll()
        perform(MockMvcRequestBuilders.post("/api/oauth2/token")
                .param("grant_type", "client_credentials"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("invalid_client"))
    }

    /**
     * Test making a request where the provided client credentials are valid
     */
    @Test
    fun requestWithGoodCredentialsNoScopes() {
        EasyMock.expect(clientDao.loadById(ClientId("graham")))
                .andReturn(CLIENT_DETAILS)
        replayAll()
        perform(MockMvcRequestBuilders.post("/api/oauth2/token")
                .param("grant_type", "client_credentials")
                .header("Authorization", "Basic Z3JhaGFtOjEyMzQ=")) // graham:1234
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("access_token").isString)
                .andExpect(MockMvcResultMatchers.jsonPath("token_type").value("Bearer"))
                .andExpect(MockMvcResultMatchers.jsonPath("scope").value("a b c"))
                .andExpect(MockMvcResultMatchers.jsonPath("expires_in").value(IsBetweenInclusive(3598, 3600)))
                .andExpect(MockMvcResultMatchers.jsonPath("refresh_token").doesNotExist())
    }

    /**
     * Test making a request where all of the requested scopes are valid
     */
    @Test
    fun requestWithGoodCredentialsAllScopesAllowed() {
        EasyMock.expect(clientDao.loadById(ClientId("graham")))
                .andReturn(CLIENT_DETAILS)
        replayAll()
        perform(MockMvcRequestBuilders.post("/api/oauth2/token")
                .param("grant_type", "client_credentials")
                .param("scope", "b a")
                .header("Authorization", "Basic Z3JhaGFtOjEyMzQ=")) // graham:1234
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("access_token").isString)
                .andExpect(MockMvcResultMatchers.jsonPath("token_type").value("Bearer"))
                .andExpect(MockMvcResultMatchers.jsonPath("scope").value("a b"))
                .andExpect(MockMvcResultMatchers.jsonPath("expires_in").value(IsBetweenInclusive(3598, 3600)))
                .andExpect(MockMvcResultMatchers.jsonPath("refresh_token").doesNotExist())
    }

    /**
     * Test making a request where only some of the requested scopes are valid
     */
    @Test
    fun requestWithGoodCredentialsSomeScopesAllowed() {
        EasyMock.expect(clientDao.loadById(ClientId("graham")))
                .andReturn(CLIENT_DETAILS)
        replayAll()
        perform(MockMvcRequestBuilders.post("/api/oauth2/token")
                .param("grant_type", "client_credentials")
                .param("scope", "a c e")
                .header("Authorization", "Basic Z3JhaGFtOjEyMzQ=")) // graham:1234
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("access_token").isString)
                .andExpect(MockMvcResultMatchers.jsonPath("token_type").value("Bearer"))
                .andExpect(MockMvcResultMatchers.jsonPath("scope").value("a c"))
                .andExpect(MockMvcResultMatchers.jsonPath("expires_in").value(IsBetweenInclusive(3598, 3600)))
                .andExpect(MockMvcResultMatchers.jsonPath("refresh_token").doesNotExist())
    }

    /**
     * Test making a request where none of the requested scopes are valid
     */
    @Test
    fun requestWithGoodCredentialsNoScopesAllowed() {
        EasyMock.expect(clientDao.loadById(ClientId("graham")))
                .andReturn(CLIENT_DETAILS)
        replayAll()
        perform(MockMvcRequestBuilders.post("/api/oauth2/token")
                .param("grant_type", "client_credentials")
                .param("scope", "d e f")
                .header("Authorization", "Basic Z3JhaGFtOjEyMzQ=")) // graham:1234
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("invalid_scope"))
    }
}