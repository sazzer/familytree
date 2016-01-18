package uk.co.grahamcox.familytree.webapp.oauth2

import org.easymock.EasyMock
import org.easymock.EasyMockSupport
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Unit tests for the OAuth2 Access Denied Handler
 */
class OAuth2AccessDeniedHandlerTest : EasyMockSupport() {
    /**
     * Test the handler
     */
    @Test
    fun test() {
        val request = createMock(HttpServletRequest::class.java)
        val response = createMock(HttpServletResponse::class.java)
        val exception = AccessDeniedException("Oops")

        response.status = 403
        EasyMock.expectLastCall<Void>()

        replayAll()

        val handler = OAuth2AccessDeniedHandler()
        handler.handle(request, response, exception)

        verifyAll()
    }
}