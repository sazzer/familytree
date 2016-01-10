package uk.co.grahamcox.familytree.oauth2.client

/**
 * Data Access layer for working with Client details
 */
interface ClientDao {
    /**
     * Load the Client Details with the given Client ID
     * @param id The ID of the client to load
     * @return the client details, if found
     */
    fun loadById(id: ClientId) : ClientDetails?
}