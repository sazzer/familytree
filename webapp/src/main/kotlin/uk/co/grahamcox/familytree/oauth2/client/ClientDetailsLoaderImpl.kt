package uk.co.grahamcox.familytree.oauth2.client

import uk.co.grahamcox.familytree.oauth2.Password
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.user.UserId
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Implementation of the Client Details Loader
 */
class ClientDetailsLoaderImpl : ClientDetailsLoader {
    /**
     * Load the Client with the given ID, regardless of the secret
     * @param id The ID to load
     * @return the Client Details, if found. Null if no client details were found
     */
    override fun load(id: ClientId): ClientDetails? = when(id) {
        ClientId("abcd") -> ClientDetails(id = id,
                secret = Password.hash("1234"),
                name = "Dummy Client",
                created = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                updated = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                owner = UserId(UUID.randomUUID().toString()),
                scopes = Scopes(arrayOf("a", "b", "c")))
        else -> null
    }
}