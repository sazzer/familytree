package uk.co.grahamcox.familytree.oauth2.accessToken

import org.slf4j.LoggerFactory
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.client.ClientDetails
import java.time.Clock
import java.time.Duration
import java.util.*
import kotlin.collections.intersect

/**
 * Mechanism to issue access tokens
 * @param clock The clock to use
 * @param duration The duration of the issued access token
 */
class AccessTokenIssuer(private val clock: Clock,
                        private val duration: Duration = Duration.ofHours(1)) {

    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(AccessTokenIssuer::class.java)

    /**
     * Issue an Access Token for a specific client
     * @param client The client to issue the access token for
     * @param scopes The scopes to issue the access token for
     * @return the access token
     */
    fun issueForClient(client: ClientDetails, scopes: Scopes?): AccessToken {
        val accessTokenId = UUID.randomUUID().toString()
        val user = client.owner
        val issuedAt = clock.instant()
        val expiresAt = issuedAt.plus(duration)

        val clientScopes = client.scopes.scopes
        val requestedScopes = scopes?.scopes
        val actualScopes = requestedScopes?.intersect(clientScopes) ?: clientScopes

        val accessToken = AccessToken(accessTokenId = AccessTokenId(accessTokenId),
                client = client.id,
                user = user,
                issued = issuedAt,
                expires = expiresAt,
                scopes = Scopes(actualScopes))

        LOG.debug("Issuing Access Token {}", accessToken)

        return accessToken
    }
}
