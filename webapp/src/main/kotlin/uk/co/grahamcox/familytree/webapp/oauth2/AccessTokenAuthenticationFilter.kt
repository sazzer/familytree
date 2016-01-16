package uk.co.grahamcox.familytree.webapp.oauth2

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Spring Security Filter to do Access Token Authentication
 */
class AccessTokenAuthenticationFilter(private val authenticationManager: AuthenticationManager) :
        OncePerRequestFilter() {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(AccessTokenAuthenticationFilter::class.java)

    /**
     * Actually attempt to authenticate the request
     * @param request The request
     * @param response The response
     * @param filterChain The filter chain
     */
    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse,
                                  filterChain: FilterChain) {
        LOG.debug("Attempting Access Token Authentication")
        filterChain.doFilter(request, response)
    }
}