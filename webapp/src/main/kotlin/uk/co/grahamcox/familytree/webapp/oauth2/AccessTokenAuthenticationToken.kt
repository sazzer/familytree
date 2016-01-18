package uk.co.grahamcox.familytree.webapp.oauth2

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessToken

/**
 * Authentication Token that represents authentication by an Access Token
 */
class AccessTokenAuthenticationToken(val credentials: AccessToken,
                                     val principal: UserDetails? = null) :
        AbstractAuthenticationToken(principal?.authorities) {

    /**
     * Get the Credentials for this Authentication Token
     * @return the credentials
     */
    override fun getCredentials(): Any? = credentials

    /**
     * Get the Principal for this Authentication Token
     * @return the principal
     */
    override fun getPrincipal(): Any? = principal


}