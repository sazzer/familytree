package uk.co.grahamcox.familytree.webapp.oauth2

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import kotlin.collections.listOf

/**
 * Spring Security Authentication Provider for handling OAuth2 Authentication
 */
class OAuth2AuthenticationProvider : AuthenticationProvider {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(OAuth2AuthenticationProvider::class.java)

    /**
     * Attempt to authenticate the user that is identified by the provided Authentication
     * @param authentication The authentication to try and authenticate
     * @return the authenticated user details, or null if we can't authenticate then
     */
    override fun authenticate(authentication: Authentication): Authentication? {
        LOG.debug("Attempting to authenticate {}", authentication)
        val result = if (supports(authentication.javaClass)) {
            UsernamePasswordAuthenticationToken(
                    "wrongUser",
                    "wrongPassword",
                    listOf())
        } else {
            null
        }

        result?.details = authentication.details

        return result
    }

    /**
     * Check if we can support the type of authentication that we are going to be provided
     * @param authentication The class of the authentication that we are going to be provided
     * @return true if we can support it. False if not
     */
    override fun supports(authentication: Class<*>): Boolean {
        LOG.debug("Checking if we can support authentication of type {}", authentication)
        return authentication == AccessTokenAuthenticationToken::class.java
    }
}
