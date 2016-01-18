package uk.co.grahamcox.familytree.webapp.oauth2

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import kotlin.collections.map

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
        val result = when(authentication) {
            is AccessTokenAuthenticationToken -> {
                val accessToken = authentication.credentials
                val scopes = accessToken.scopes
                val authorities = scopes.scopes.map { it -> "ROLE_" + it }
                    .map { it -> SimpleGrantedAuthority(it)}

                val user = User(
                        accessToken.user.id,
                        accessToken.accessTokenId.id,
                        true, // enabled
                        true, // accountNonExpired
                        true, // credentialsNonExpired
                        true, // accountNonLocked
                        authorities
                )

                AccessTokenAuthenticationToken(accessToken, user)
            }
            else -> null
        }

        result?.details = authentication.details
        result?.isAuthenticated = true

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
