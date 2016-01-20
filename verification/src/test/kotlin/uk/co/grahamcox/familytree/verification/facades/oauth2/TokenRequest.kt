package uk.co.grahamcox.familytree.verification.facades.oauth2

/**
 * Interface marking a Token Request
 */
interface TokenRequest

/**
 * Request for performing a Client Credentials Grant
 * @property clientId The Client ID
 * @property secret The Client Secret
 */
data class ClientCredentialsTokenRequest(val clientId: String,
                                         val secret: String) : TokenRequest