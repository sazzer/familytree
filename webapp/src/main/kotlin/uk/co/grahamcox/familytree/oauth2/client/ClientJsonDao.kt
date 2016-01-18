package uk.co.grahamcox.familytree.oauth2.client

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import uk.co.grahamcox.familytree.oauth2.Password
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.user.UserId
import java.time.Instant
import kotlin.collections.firstOrNull
import kotlin.collections.map

/**
 * Implementation of the Client DAO that works in terms of a hard-coded JSON file of data
 * @param resource The resource to load the data from
 */
class ClientJsonDao(resource: Resource) : ClientDao {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(ClientJsonDao::class.java)
    /** the actual data */
    val data: List<ClientDetails>

    init {
        if (!resource.isReadable) {
            throw IllegalArgumentException("Unable to read from resource: " + resource.toString())
        }

        val objectMapper = ObjectMapper()
        data = objectMapper.readValue(resource.inputStream, List::class.java)
            .map { it as Map<String, Any> }
            .map {
                ClientDetails(id = ClientId(it["id"] as String),
                        secret = Password.hash(it["secret"] as String),
                        name = it["name"] as String,
                        created = Instant.parse(it["created"] as String),
                        updated = Instant.parse(it["updated"] as String),
                        owner = UserId(it["owner"] as String),
                        scopes = Scopes(it["scopes"] as List<String>))
            }

        LOG.debug("Loaded data: {}", data)
    }

    /**
     * Load the Client Details with the given Client ID
     * @param id The ID of the client to load
     * @return the client details, if found
     */
    override fun loadById(id: ClientId): ClientDetails? {
        LOG.debug("Loading client with ID {}", id)
        return data.firstOrNull { it.id == id }
    }
}
