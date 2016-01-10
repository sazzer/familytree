package uk.co.grahamcox.familytree.oauth2.accessToken

import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.client.ClientDetails
import java.time.Clock
import java.time.Duration
import java.util.*

/**
 * Mechanism to issue access tokens
 * @param clock The clock to use
 * @param duration The duration of the issued access token
 */
class AccessTokenIssuer(private val clock: Clock,
                        private val duration: Duration = Duration.ofHours(1)) {
    /**
     * Issue an Access Token for a specific client
     * @param client The client to issue the access token for
     * @param scopes The scopes to issue the access token for
     * @return the access token
     */
    fun issueForClient(client: ClientDetails, scopes: Scopes?): AccessToken {
        return AccessToken(accessTokenId = AccessTokenId(UUID.randomUUID().toString()),
                refreshTokenId = null,
                expires = clock.instant().plus(duration),
                scopes = client.scopes)
    }
}