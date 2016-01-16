package uk.co.grahamcox.familytree.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import uk.co.grahamcox.familytree.webapp.oauth2.AccessTokenAuthenticationConfigurer
import uk.co.grahamcox.familytree.webapp.oauth2.OAuth2AuthenticationProvider

/**
 * Spring context to set up Spring Security
 */
@Configuration
@EnableWebSecurity
open class SecurityContext : WebSecurityConfigurerAdapter() {
    /**
     * Configure the Authentication Manager
     * @param auth The Authentication Manager to configure
     */
    @Autowired
    open fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(OAuth2AuthenticationProvider())
        
        auth.inMemoryAuthentication()
            .withUser("graham")
                .password("password")
                .roles("USER")
            .and().withUser("other")
                .password("password")
                .roles("OTHER")
    }

    /**
     * Bean definition for the Authentication Manager
     */
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

    /**
     * Configure the HTTP aspects of Security
     * @param http The HTTP Security to configure
     */
    override fun configure(http: HttpSecurity) {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.exceptionHandling().accessDeniedHandler(OAuth2AccessDeniedHandler())

        http.apply(AccessTokenAuthenticationConfigurer())
    }
}
