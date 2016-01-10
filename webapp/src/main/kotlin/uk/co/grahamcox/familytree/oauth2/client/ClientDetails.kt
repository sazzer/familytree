package uk.co.grahamcox.familytree.oauth2.client

import uk.co.grahamcox.familytree.oauth2.Password
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.user.UserId
import java.time.Instant

/**
 * Representation of the details of a client
 * @property id The ID of the Client
 * @property secret The secret of the Client
 * @property name The name of the client
 * @property created When the client was first created
 * @property updated When the client was last updated
 * @property owner The owner of the Client
 * @property scopes The scopes the client is able to provide
 */
data class ClientDetails(val id: ClientId,
                         val secret: Password,
                         val name: String,
                         val created: Instant,
                         val updated: Instant,
                         val owner: UserId,
                         val scopes: Scopes)