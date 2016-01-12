package uk.co.grahamcox.familytree.oauth2.client

import org.slf4j.LoggerFactory

/**
 * Implementation of the Client Details Loader
 */
class ClientDetailsLoaderImpl(private val dao: ClientDao) : ClientDetailsLoader {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(ClientDetailsLoaderImpl::class.java)
    /**
     * Load the Client with the given ID, regardless of the secret
     * @param id The ID to load
     * @return the Client Details, if found. Null if no client details were found
     */
    override fun load(id: ClientId): ClientDetails? {
        LOG.debug("Loading client details for client {}", id)
        val client = dao.loadById(id)
        return client
    }
}
