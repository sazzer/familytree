package uk.co.grahamcox.familytree.webapp.oauth2

import org.springframework.security.authentication.AbstractAuthenticationToken

/**
 * Authentication Token that represents authentication by an Access Token
 */
class AccessTokenAuthenticationToken : AbstractAuthenticationToken(null) {
    /**
     * Get the Credentials for this Authentication Token
     * @return the credentials
     */
    override fun getCredentials(): Any? = null

    /**
     * Get the Principal for this Authentication Token
     * @return the principal
     */
    override fun getPrincipal(): Any? = null
}