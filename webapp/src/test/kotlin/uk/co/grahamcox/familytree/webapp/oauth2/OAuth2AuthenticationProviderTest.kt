package uk.co.grahamcox.familytree.webapp.oauth2

import org.junit.Assert
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessToken
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessTokenId
import uk.co.grahamcox.familytree.oauth2.client.ClientId
import uk.co.grahamcox.familytree.user.UserId
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.listOf
import kotlin.collections.map

/**
 * Unit tests for the OAuth2 Authentication Provider
 */
class OAuth2AuthenticationProviderTest {
    /** The provider to test */
    private val provider = OAuth2AuthenticationProvider()

    /**
     * Test that we can support the correct access token types
     */
    @Test
    fun testSupports() {
        Assert.assertTrue(provider.supports(AccessTokenAuthenticationToken::class.java))
        Assert.assertFalse(provider.supports(UsernamePasswordAuthenticationToken::class.java))
    }

    /**
     * Test authenticating a valid token
     */
    @Test
    fun testAuthenticate() {
        val input = AccessTokenAuthenticationToken(buildAccessToken())
        val result = provider.authenticate(input)
        if (result == null) {
            Assert.fail("Result Authentication was null")
        } else {
            Assert.assertTrue(result is AccessTokenAuthenticationToken)
            Assert.assertTrue(result.isAuthenticated)
            Assert.assertEquals(listOf("ROLE_a", "ROLE_b", "ROLE_c"),
                    result.authorities.map { it -> it.authority})
        }
    }

    /**
     * Build a valid access token
     * @return the access token
     */
    private fun buildAccessToken() : AccessToken {
        val clock = Clock.systemUTC()
        return AccessToken(
                accessTokenId = AccessTokenId(UUID.randomUUID().toString()),
                client = ClientId(UUID.randomUUID().toString()),
                user = UserId(UUID.randomUUID().toString()),
                expires = clock.instant().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
                issued = clock.instant().truncatedTo(ChronoUnit.SECONDS),
                scopes = Scopes(arrayOf("a", "b", "c"))
        )

    }

}