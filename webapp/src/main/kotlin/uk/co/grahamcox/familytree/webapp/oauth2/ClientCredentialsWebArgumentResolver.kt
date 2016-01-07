package uk.co.grahamcox.familytree.webapp.oauth2

import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebArgumentResolver
import org.springframework.web.context.request.NativeWebRequest
import uk.co.grahamcox.familytree.oauth2.client.ClientCredentials
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.text.startsWith
import kotlin.text.substring
import kotlin.text.toLowerCase

/**
 * Web Argument Resolver to decode the Client Credentials from the Request, if possible
 */
class ClientCredentialsWebArgumentResolver : WebArgumentResolver {
    /** Prefix for Basic Authorization headers */
    private val BASIC_AUTHORIZATION_PREFIX = "basic "
    /**
     * Resolve the Client Credentials from the request. These must be in the Authorization header, using Basic
     * authentication.
     */
    override fun resolveArgument(methodParameter: MethodParameter, request: NativeWebRequest): Any {
        return when (methodParameter.parameterType) {
            ClientCredentials::class.java -> {
                val authorization = request.getHeader("Authorization")
                when (authorization) {
                    null -> WebArgumentResolver.UNRESOLVED
                    "" -> WebArgumentResolver.UNRESOLVED
                    else -> if (authorization.toLowerCase().startsWith(BASIC_AUTHORIZATION_PREFIX)) {
                        parseAuthorization(authorization.substring(BASIC_AUTHORIZATION_PREFIX.length))
                    } else {
                        WebArgumentResolver.UNRESOLVED
                    }
                }
            }
            else -> WebArgumentResolver.UNRESOLVED
        }
    }

    /**
     * Parse the provided Basic Authorization string to return the Client Credentials to use
     * @param authorization The authorization to parse. This is the Base64 encoded string after the "Basic " prefix
     * @return the Client Credentials that we parsed out of the header
     */
    private fun parseAuthorization(authorization: String): ClientCredentials {
        val decoded = java.lang.String(Base64.getDecoder().decode(authorization))
        return if (decoded.contains(":")) {
            val (username, password) = decoded.split(":", 2)
            ClientCredentials(username, password)
        } else {
            throw IllegalArgumentException("Basic Authorization header was malformed")
        }
    }
}