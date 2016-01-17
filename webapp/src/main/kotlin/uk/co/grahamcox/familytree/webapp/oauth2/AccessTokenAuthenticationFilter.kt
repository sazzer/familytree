package uk.co.grahamcox.familytree.webapp.oauth2

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.text.startsWith
import kotlin.text.substring
import kotlin.text.toLowerCase

/**
 * Spring Security Filter to do Access Token Authentication
 */
class AccessTokenAuthenticationFilter(private val authenticationManager: AuthenticationManager) :
        OncePerRequestFilter() {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(AccessTokenAuthenticationFilter::class.java)

    /** The name of the header containing the access token */
    private val AUTHORIZATION_HEADER_NAME = "Authorization";
    /** The prefix to the header containing the access token */
    private val AUTHORIZATION_HEADER_PREFIX = "Bearer ".toLowerCase()

    /** The Authentication Entry Point to use on authentication failure */
    private val authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)

    /** The mechanism to decode access tokens */
    @Autowired
    private lateinit var accessTokenEncoder: AccessTokenEncoder

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

        val authorizationHeader = request.getHeader(AUTHORIZATION_HEADER_NAME) ?: ""
        LOG.debug("Authorization header: {}", authorizationHeader)

        if (authorizationHeader.toLowerCase().startsWith(AUTHORIZATION_HEADER_PREFIX)) {
            val encodedAccessToken = authorizationHeader.substring(AUTHORIZATION_HEADER_PREFIX.length)
            LOG.debug("Received Access Token {}", encodedAccessToken)

            try {
                val authToken = try {
                    val accessToken = accessTokenEncoder.decodeAccessToken(encodedAccessToken)
                    AccessTokenAuthenticationToken(accessToken)
                } catch (e: InvalidAccessTokenException) {
                    throw BadCredentialsException("Failed to decode Access Token", e);
                }

                authToken.details = WebAuthenticationDetails(request)
                val authResult = authenticationManager.authenticate(authToken)

                SecurityContextHolder.getContext().authentication = authResult
                filterChain.doFilter(request, response)
            } catch (e: AuthenticationException) {
                LOG.debug("Authentication failed for {}", encodedAccessToken)
                SecurityContextHolder.clearContext()
                authenticationEntryPoint.commence(request, response, e)
            }
        } else {
            filterChain.doFilter(request, response)
        }
    }
}