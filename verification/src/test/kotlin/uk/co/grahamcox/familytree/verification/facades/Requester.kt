package uk.co.grahamcox.familytree.verification.facades

import org.slf4j.LoggerFactory
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
    fun get(url: String, params: Map<String, Any> = mapOf()): ResponseEntity<Map<*, *>> {
        val finalUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + url)
        params.forEach { finalUrl.queryParam(it.key, it.value) }

        val uri = finalUrl.build().toUri()
        LOG.debug("Making request to {}", uri)

        val result = restTemplate.getForEntity(uri, Map::class.java)
        LOG.debug("Received result: {}", result)

        return result
    }

}
