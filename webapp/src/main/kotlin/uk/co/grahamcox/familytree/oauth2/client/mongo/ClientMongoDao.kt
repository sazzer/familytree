package uk.co.grahamcox.familytree.oauth2.client.mongo

import uk.co.grahamcox.familytree.oauth2.Password
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.client.ClientDao
import uk.co.grahamcox.familytree.oauth2.client.ClientDetails
import uk.co.grahamcox.familytree.oauth2.client.ClientId
import uk.co.grahamcox.familytree.user.UserId
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.firstOrNull
import kotlin.collections.listOf

/**
 * Implementation of the Client DAO working in terms of MongoDB
 * TODO: For now this works in terms of a local hashmap
 */
class ClientMongoDao : ClientDao {
    /**
     * The actual hardcoded data to work with
     */
    val data = listOf(
        ClientDetails(id = ClientId("graham"),
            secret = Password.hash("1234"),
            name = "Dummy Client",
            created = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
            updated = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
            owner = UserId(UUID.randomUUID().toString()),
            scopes = Scopes(arrayOf("a", "b", "c")))
    )

    /**
     * Load the Client Details with the given Client ID
     * @param id The ID of the client to load
     * @return the client details, if found
     */
    override fun loadById(id: ClientId): ClientDetails? {
        return data.firstOrNull { it.id == id }
    }
}