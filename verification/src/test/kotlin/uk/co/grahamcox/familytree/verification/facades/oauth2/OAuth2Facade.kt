package uk.co.grahamcox.familytree.verification.facades.oauth2

import org.apache.commons.collections.map.MultiValueMap
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.util.LinkedMultiValueMap
import uk.co.grahamcox.familytree.verification.facades.Requester
import kotlin.collections.mapOf

/**
 * Facade layer for working with the OAuth2 controller
 * @param requester The means to make a request to the server
 */
class OAuth2Facade(private val requester: Requester) {
    /**
     * Perform a Token request for the raw response
     * @param request The request details
     * @return the response from making the token request
     */
    fun tokenRequestForResponse(request: TokenRequest): ResponseEntity<Map<*, *>> {
        val params = LinkedMultiValueMap<String, String>()

        when(request) {
            is ClientCredentialsTokenRequest -> {
                params.add("grant_type", "client_credentials")
            }
            else -> throw IllegalArgumentException("Unsupported request type: ${request.javaClass.name}")
        }

        return requester.request(method = HttpMethod.POST,
                url = "/api/oauth2/token",
                body = params)
    }
}