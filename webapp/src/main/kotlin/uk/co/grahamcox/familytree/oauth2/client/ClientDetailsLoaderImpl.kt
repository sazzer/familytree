package uk.co.grahamcox.familytree.oauth2.client

/**
 * Implementation of the Client Details Loader
 */
class ClientDetailsLoaderImpl(private val dao: ClientDao) : ClientDetailsLoader {
    /**
     * Load the Client with the given ID, regardless of the secret
     * @param id The ID to load
     * @return the Client Details, if found. Null if no client details were found
     */
    override fun load(id: ClientId): ClientDetails? = dao.loadById(id)
}