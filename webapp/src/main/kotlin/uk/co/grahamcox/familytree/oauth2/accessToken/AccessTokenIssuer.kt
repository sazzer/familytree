package uk.co.grahamcox.familytree.oauth2.accessToken

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
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
    /** The Key to use for signing JWTs */
    private val jwtKey = MacProvider.generateKey()

    /**
     * Issue an Access Token for a specific client
     * @param client The client to issue the access token for
     * @param scopes The scopes to issue the access token for
     * @return the access token
     */
    fun issueForClient(client: ClientDetails, scopes: Scopes?): AccessToken {
        val issuedAt = clock.instant()
        val expiresAt = issuedAt.plus(duration)

        val accessToken = Jwts.builder()
            .setIssuer(AccessTokenIssuer::class.qualifiedName)
            .setSubject(client.owner.id)
            .setAudience(client.id.id)
            .setExpiration(Date.from(expiresAt))
            .setNotBefore(Date.from(issuedAt))
            .setIssuedAt(Date.from(issuedAt))
            .setId(UUID.randomUUID().toString())
            .claim(Scopes::class.qualifiedName, client.scopes.scopes)
            .signWith(SignatureAlgorithm.HS512, jwtKey)
            .compact()

        return AccessToken(accessTokenId = AccessTokenId(accessToken),
                refreshTokenId = null,
                expires = clock.instant().plus(duration),
                scopes = client.scopes)
    }
}