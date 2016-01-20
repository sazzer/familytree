package uk.co.grahamcox.familytree.verification.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.familytree.verification.facades.Requester
import uk.co.grahamcox.familytree.verification.facades.oauth2.AuthenticationFacade
import uk.co.grahamcox.familytree.verification.facades.oauth2.OAuth2Facade

/**
 * Spring context for building everything to do with authentication
 */
@Configuration
open class AuthenticationContext {
    /**
     * Build the Authentication Facade
     * @param requester The Requester to use
     * @return the authentication facade
     */
    @Autowired
    @Bean
    open fun authenticationFacade(requester: Requester) = AuthenticationFacade(requester)

    /**
     * Build the OAuth2 Facade
     * @param requester The requester to use
     * @return the OAuth2 facade
     */
    @Autowired
    @Bean
    open fun oauth2Facade(requester: Requester) = OAuth2Facade(requester)
}
