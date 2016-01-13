package uk.co.grahamcox.familytree.webapp.oauth2

import io.jsonwebtoken.*
import io.jsonwebtoken.impl.crypto.MacProvider
import org.slf4j.LoggerFactory
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessToken
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessTokenId
import uk.co.grahamcox.familytree.oauth2.client.ClientId
import uk.co.grahamcox.familytree.user.UserId
import java.util.*
import javax.crypto.SecretKey

/**
 * Exception thrown when parsing an Access Token fails
 * @param message The error message
 */
open class InvalidAccessTokenException(message: String) : Exception(message)

/**
 * Exception thrown to indicate that parsing an access token failed because it had expired
 * @param message The error message
 */
class ExpiredAccessTokenException(message: String) : InvalidAccessTokenException(message)

/**
 * Mechanism to encode an access token with
 * @property jwtKey The key to sign the JWT with
 */
class AccessTokenEncoder(private val jwtKey: SecretKey = MacProvider.generateKey()) {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(AccessTokenEncoder::class.java)

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
                .claim(Scopes::class.qualifiedName, accessToken.scopes.toString())
                .signWith(SignatureAlgorithm.HS512, jwtKey)
                .compact()

    /**
     * Decode a JWT into an Access Token
     * @param accessToken The access token to decode.
     * This must have been encoded by {@link #encodeAccessToken}
     * @return the decoded access token
     */
    fun decodeAccessToken(accessToken: String): AccessToken {
        LOG.debug("Attempting to decode access token: {}", accessToken)
        val jwt = try {
            Jwts.parser()
                    .setSigningKey(jwtKey)
                    .requireIssuer(AccessTokenEncoder::class.qualifiedName)
                    .parseClaimsJws(accessToken)
        } catch (e: MalformedJwtException) {
            LOG.warn("Failed to decode a malformed access token: {}", accessToken)
            throw InvalidAccessTokenException("The access token string is not valid")
        } catch (e: SignatureException) {
            LOG.warn("Failed to decode an access token with an invalid signature", accessToken)
            throw InvalidAccessTokenException("The access token string has an invalid signature")
        } catch (e: ExpiredJwtException) {
            LOG.warn("Decoded access token that has expired: {}", e.claims.expiration)
            throw ExpiredAccessTokenException(e.message!!)
        } catch (e: PrematureJwtException) {
            LOG.warn("Decoded access token that has not started yet: {}", e.claims.notBefore)
            throw InvalidAccessTokenException("The access token has a start date in the future")
        } catch (e: IncorrectClaimException) {
            LOG.warn("Decoded access token with incorrect mandatory claim {}: {}",
                    e.claimName, e.claims.get(e.claimName))
            throw InvalidAccessTokenException("The access token had an invalid mandatory claim: " + e.claimName)
        } catch (e: MissingClaimException) {
            LOG.warn("Missing claim {} when decoding Access Token", e.claimName)
            throw InvalidAccessTokenException("The access token had a missing claim: " + e.claimName)
        }

        val id = jwt.body.id?.let { AccessTokenId(it) }
                ?: throw InvalidAccessTokenException("No ID present")
        val clientId = jwt.body.audience?.let { ClientId(it) }
                ?: throw InvalidAccessTokenException("No Audience present")
        val userId = jwt.body.subject?.let { UserId(it) }
                ?: throw InvalidAccessTokenException("No Subject present")
        val expires = jwt.body.expiration?.let { it.toInstant() }
                ?: throw InvalidAccessTokenException("No Expiration Date present")
        val issued = jwt.body.issuedAt?.let { it.toInstant() }
                ?: throw InvalidAccessTokenException("No Issued Date present")
        val scopes = jwt.body.get(Scopes::class.qualifiedName)?.let { Scopes(it.toString()) }
                ?: throw InvalidAccessTokenException("No Scopes present")

        return AccessToken(
                accessTokenId = id,
                client = clientId,
                user = userId,
                expires = expires,
                issued = issued,
                scopes = scopes
        )
    }
}
