package uk.co.grahamcox.familytree.webapp.oauth2

import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import uk.co.grahamcox.familytree.oauth2.client.ClientCredentials
import uk.co.grahamcox.familytree.oauth2.client.ClientId
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.text.startsWith
import kotlin.text.substring
import kotlin.text.toLowerCase

/**
 * Argument Resolver to decode the Client Credentials from the Request, if possible
 */
class ClientCredentialsArgumentResolver : HandlerMethodArgumentResolver {
    /** Prefix for Basic Authorization headers */
    private val BASIC_AUTHORIZATION_PREFIX = "basic "

    /**
     * Actually resolve the argument to use for this parameter
     * @param parameter The parameter to resolve the value for
     * @param mavContainer The Model and View Container for the request. Unused
     * @param webRequest The request
     * @param binderFactory a factory for creating WebDataBinder instances. Unused
     * @return the resolved argument value, or null if we couldn't resolve one
     */
    override fun resolveArgument(parameter: MethodParameter,
                                 mavContainer: ModelAndViewContainer,
                                 webRequest: NativeWebRequest,
                                 binderFactory: WebDataBinderFactory): Any? {
        val authorization = webRequest.getHeader("Authorization") ?: ""

        return if (authorization.toLowerCase().startsWith(BASIC_AUTHORIZATION_PREFIX)) {
            parseAuthorization(authorization.substring(BASIC_AUTHORIZATION_PREFIX.length))
        } else {
            null
        }
    }

    /**
     * Check if this parameter is supported by this argument resolver
     * @param parameter the parameter to check
     * @return True if the parameter is a ClientCredentials. False if not
     */
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == ClientCredentials::class.java

    /**
     * Parse the provided Basic Authorization string to return the Client Credentials to use
     * @param authorization The authorization to parse. This is the Base64 encoded string after the "Basic " prefix
     * @return the Client Credentials that we parsed out of the header
     */
    private fun parseAuthorization(authorization: String): ClientCredentials {
        val decoded = java.lang.String(Base64.getDecoder().decode(authorization))
        return if (decoded.contains(":")) {
            val (username, password) = decoded.split(":", 2)
            ClientCredentials(ClientId(username), password)
        } else {
            throw IllegalArgumentException("Basic Authorization header was malformed")
        }
    }
}