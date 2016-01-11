package uk.co.grahamcox.familytree.oauth2.accessToken

import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.client.ClientId
import java.time.Instant

/**
 * Representation of the actual ID of an Access Token
 * @property id The actual Access Token
 */
data class AccessTokenId(val id: String)

/**
 * Representation of the actual ID of a Refresh Token
 * @property id The actual Refresh Token
 */
data class RefreshTokenId(val id: String)

/**
 * Representation of an Access Token
 * @property accessTokenId The Access Token itself
 * @property refreshTokenId The Refresh Token, if there is one
 * @property client The client that issues the access token
 * @property issued When the Access Token was issued
 * @property expires The instant in time when the Access Token expires
 * @property scopes The scopes the Access Token is viable for
 */
data class AccessToken(val accessTokenId: AccessTokenId,
                       val refreshTokenId: RefreshTokenId?,
                       val client: ClientId,
                       val issued: Instant,
                       val expires: Instant,
                       val scopes: Scopes)