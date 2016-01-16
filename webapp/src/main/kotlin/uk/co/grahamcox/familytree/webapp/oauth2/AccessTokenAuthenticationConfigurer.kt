package uk.co.grahamcox.familytree.webapp.oauth2

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.HttpSecurityBuilder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

/**
 * Configurer for setting up Access Token Authentication
 */
class AccessTokenAuthenticationConfigurer<H : HttpSecurityBuilder<H>> : SecurityConfigurerAdapter<DefaultSecurityFilterChain, H>() {
    /**
     * Configure the HTTP Security Builder, setting it up for Access Token Authentication
     * @param http The HTTP Security Builder to configure
     */
    override fun configure(http: H) {
        val authenticationManager = http.getSharedObject(AuthenticationManager::class.java)
        val filter = AccessTokenAuthenticationFilter(authenticationManager)
        postProcess(filter)
        http.addFilterBefore(filter, BasicAuthenticationFilter::class.java)
    }
}