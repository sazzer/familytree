package uk.co.grahamcox.familytree.webapp.oauth2

import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Handler for when an Access Denied error occurs - i.e. a valid user tried to access a resource
 * that they don't have permission to
 */
class OAuth2AccessDeniedHandler : AccessDeniedHandler {
    /**
     * Handle the access denied situation
     * @param request The request
     * @param response The response
     * @param exception The exception
     */
    override fun handle(request: HttpServletRequest,
                        response: HttpServletResponse,
                        exception: AccessDeniedException) {
        response.status = HttpStatus.FORBIDDEN.value()
    }
}