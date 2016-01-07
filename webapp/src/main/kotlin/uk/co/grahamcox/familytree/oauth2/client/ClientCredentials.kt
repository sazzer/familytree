package uk.co.grahamcox.familytree.oauth2.client

/**
 * Representation of the Credentials used to identify a Client
 * @property clientId The ID of the client
 * @property clientSecret The Secret of the client
 */
data class ClientCredentials(val clientId: String, val clientSecret: String)