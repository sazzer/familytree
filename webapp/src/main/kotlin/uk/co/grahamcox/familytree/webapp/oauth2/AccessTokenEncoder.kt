package uk.co.grahamcox.familytree.webapp.oauth2

import uk.co.grahamcox.familytree.oauth2.accessToken.AccessToken

/**
 * Exception thrown when parsing an Access Token fails
 * @param message The error message
 */
open class InvalidAccessTokenException(message: String) : RuntimeException(message)

/**
 * Exception thrown to indicate that parsing an access token failed because it had expired
 * @param message The error message
 */
class ExpiredAccessTokenException(message: String) : InvalidAccessTokenException(message)

interface AccessTokenEncoder {
    /**
     * Encode the given Access Token into a JWT
     * @param accessToken The access token to encode
     * @return The encoded access token
     */
    fun encodeAccessToken(accessToken: AccessToken): String

    /**
     * Decode a JWT into an Access Token
     * @param accessToken The access token to decode.
     * This must have been encoded by {@link #encodeAccessToken}
     * @return the decoded access token
     */
    fun decodeAccessToken(accessToken: String): AccessToken
}
