package uk.co.grahamcox.familytree.verification.facades.oauth2

import org.springframework.http.HttpStatus
import uk.co.grahamcox.familytree.verification.facades.Requester

/**
 * Facade layer for getting details of our authentication
 * @param requester The means to make a request to the server
 */
class AuthenticationFacade(private val requester: Requester) {

    /**
     * Get the authentication details
     * @return the authentication details
     */
    fun getAuthentication(): Map<*, *> {
        val response = requester.get("/api/debug/whoami")
        if (response.statusCode != HttpStatus.OK) {
            throw IllegalStateException("Service returned an error requesting current authentication details. Expected 200 OK but got ${response.statusCode}")
        }
        return response.body
    }
}
