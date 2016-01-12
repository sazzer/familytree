package uk.co.grahamcox.familytree.oauth2.accessToken

import org.junit.Assert
import org.junit.Test
import uk.co.grahamcox.familytree.oauth2.Password
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.client.ClientDetails
import uk.co.grahamcox.familytree.oauth2.client.ClientId
import uk.co.grahamcox.familytree.user.UserId
import java.time.Clock
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Unit tests for the Access Token Issuer
 */
class AccessTokenIssuerTest {
    /** The timezone to use */
    private val TIMEZONE = ZoneId.of("UTC")

    /** The time to use */
    private val TIME = ZonedDateTime.of(2016, 1, 6, 12, 33, 25, 0, TIMEZONE).toInstant()

    /** The duration that the access tokens are valid for */
    private val ACCESS_TOKEN_DURATION = Duration.ofHours(1)

    /** The clock to use */
    private val clock = Clock.fixed(TIME, TIMEZONE)

    /** The Access Token Issuer to test */
    private val accessTokenIssuer = AccessTokenIssuer(clock, ACCESS_TOKEN_DURATION)

    /**
     * Issue an Access Token to a Client without requesting any specific scopes
     */
    @Test
    fun issueClientTokenNoScopes() {
        val client = ClientDetails(id = ClientId("graham"),
                secret = Password.hash("1234"),
                name = "Dummy Client",
                created = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                updated = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                owner = UserId(UUID.randomUUID().toString()),
                scopes = Scopes(arrayOf("a", "b", "c")))

        val accessToken = accessTokenIssuer.issueForClient(client, null)
        Assert.assertEquals(TIME, accessToken.issued)
        Assert.assertEquals(TIME.plus(ACCESS_TOKEN_DURATION), accessToken.expires)
        Assert.assertEquals(ClientId("graham"), accessToken.client)
        Assert.assertEquals(client.owner, accessToken.user)
        Assert.assertEquals(Scopes(arrayOf("a", "b", "c")), accessToken.scopes)
    }

    /**
     * Issue an Access Token to a Client whilst requesting some scopes that are supported by the client
     */
    @Test
    fun issueClientTokenScopes() {
        val client = ClientDetails(id = ClientId("graham"),
                secret = Password.hash("1234"),
                name = "Dummy Client",
                created = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                updated = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                owner = UserId(UUID.randomUUID().toString()),
                scopes = Scopes(arrayOf("a", "b", "c")))

        val accessToken = accessTokenIssuer.issueForClient(client, Scopes(arrayOf("a", "b")))
        Assert.assertEquals(TIME, accessToken.issued)
        Assert.assertEquals(TIME.plus(ACCESS_TOKEN_DURATION), accessToken.expires)
        Assert.assertEquals(ClientId("graham"), accessToken.client)
        Assert.assertEquals(client.owner, accessToken.user)
        Assert.assertEquals(Scopes(arrayOf("a", "b")), accessToken.scopes)
    }

    /**
     * Issue an Access Token to a Client whilst requesting some scopes that are supported by the client
     * and other scopes that aren't supported by it
     */
    @Test
    fun issueClientTokenScopesOverlapping() {
        val client = ClientDetails(id = ClientId("graham"),
                secret = Password.hash("1234"),
                name = "Dummy Client",
                created = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                updated = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                owner = UserId(UUID.randomUUID().toString()),
                scopes = Scopes(arrayOf("a", "b", "c")))

        val accessToken = accessTokenIssuer.issueForClient(client, Scopes(arrayOf("b", "c", "d")))
        Assert.assertEquals(TIME, accessToken.issued)
        Assert.assertEquals(TIME.plus(ACCESS_TOKEN_DURATION), accessToken.expires)
        Assert.assertEquals(ClientId("graham"), accessToken.client)
        Assert.assertEquals(client.owner, accessToken.user)
        Assert.assertEquals(Scopes(arrayOf("b", "c")), accessToken.scopes)
    }
}
