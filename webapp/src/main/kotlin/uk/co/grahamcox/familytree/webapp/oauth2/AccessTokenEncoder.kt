package uk.co.grahamcox.familytree.webapp.oauth2

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.compression.GzipCompressionCodec
import io.jsonwebtoken.impl.crypto.MacProvider
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessToken
import java.util.*

/**
 * Mechanism to encode an access token with
 */
class AccessTokenEncoder {
    /** The Key to use for signing JWTs */
    private val jwtKey = MacProvider.generateKey()

    /**
     * Encode the given Access Token into a JWT
     * @param accessToken The access token to encode
     * @return The encoded access token
     */
    fun encodeAccessToken(accessToken: AccessToken) =
            Jwts.builder()
                .setIssuer(AccessTokenEncoder::class.qualifiedName)
                .setSubject(accessToken.user.id)
                .setAudience(accessToken.client.id)
                .setExpiration(Date.from(accessToken.expires))
                .setIssuedAt(Date.from(accessToken.issued))
                .setNotBefore(Date.from(accessToken.issued))
                .setId(accessToken.accessTokenId.id)
                .claim(Scopes::class.qualifiedName, accessToken.scopes.scopes)
                .signWith(SignatureAlgorithm.HS512, jwtKey)
                .compact()
}
