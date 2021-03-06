package uk.co.grahamcox.familytree.oauth2.client

import org.slf4j.LoggerFactory

/** The logger to use */
private val LOG = LoggerFactory.getLogger(ClientDetailsLoader::class.java)

/**
 * Mechanism to load client details
 */
interface ClientDetailsLoader {
    /**
     * Load the Client with the given ID, regardless of the secret
     * @param id The ID to load
     * @return the Client Details, if found. Null if no client details were found
     */
    fun load(id: ClientId) : ClientDetails?

    /**
     * Load the Client with the given credentials
     * @param credentials The credentials to load
     * @return The Client Details, if found. Null if no client details were found or the secret was wrong
     */
    fun load(credentials: ClientCredentials) : ClientDetails? =
            load(credentials.clientId)?.let {
                if (it.secret.equals(credentials.clientSecret)) {
                    it
                } else {
                    LOG.warn("Loaded client details for client {} but password didn't match", credentials.clientId)
                    null
                }
            }
}
