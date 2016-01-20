package uk.co.grahamcox.familytree.verification.facades

import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import kotlin.collections.forEach
import kotlin.collections.mapOf

/**
 * Mechanism to make requests to the server with
 */
class Requester(private val baseUrl: String,
                private val restTemplate: RestTemplate) {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(Requester::class.java)

    /**
     * Make a GET request to the server
     * @param url The URL to request. This will be appended on to the base URL that we have
     * @param params Any parameters to the request
     * @return the response
     */
    fun get(url: String, params: Map<String, Any> = mapOf()): ResponseEntity<Map<*, *>> =
        request(HttpMethod.GET, url, params, mapOf(), null)

    /**
     * Actually make a request to the server
     * @param method the HTTP Method to use
     * @param url The URL to request. This will be appended on to the base URL that we have
     * @param urlParams The Querystring parameters to the URL
     * @param headers Any headers to the request
     * @param body The body to the request.
     */
    fun <T> request(method: HttpMethod,
                url: String,
                urlParams: Map<String, Any> = mapOf(),
                headers: Map<String, String> = mapOf(),
                body: T? = null): ResponseEntity<Map<*, *>> {
        val finalUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + url)
        urlParams.forEach { finalUrl.queryParam(it.key, it.value) }

        val uri = finalUrl.build().toUri()
        LOG.debug("Making request to {}", uri)

        val request = RequestEntity<T>(body, method, uri)
        headers.forEach { request.headers.set(it.key, it.value) }

        val result = restTemplate.exchange(request, Map::class.java)
        LOG.debug("Received result: {}", result)

        return result
    }
}
