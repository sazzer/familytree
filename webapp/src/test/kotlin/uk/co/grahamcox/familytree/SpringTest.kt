package uk.co.grahamcox.familytree

import org.junit.Assert
import org.junit.Test
import org.springframework.mock.web.MockServletContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import uk.co.grahamcox.familytree.spring.CoreContext
import uk.co.grahamcox.familytree.webapp.DebugController
import uk.co.grahamcox.familytree.webapp.spring.ServletContext
import java.time.Clock

/**
 * Unit tests for the Spring Contexts
 */
class SpringTest {
    /**
     * Test the Core Context
     */
    @Test
    fun testCoreContext() {
        val servletContext = MockServletContext()
        val context = AnnotationConfigWebApplicationContext()
        context.servletContext = servletContext
        context.register(CoreContext::class.java)
        context.refresh()

        val clock = context.getBean(Clock::class.java)
        Assert.assertNotNull(clock)
    }

    /**
     * Test the Servlet Context
     */
    @Test
    fun testServletContext() {
        val servletContext = MockServletContext()
        val context = AnnotationConfigWebApplicationContext()
        context.servletContext = servletContext
        context.register(CoreContext::class.java)
        context.refresh()

        val webappContext = AnnotationConfigWebApplicationContext()
        webappContext.servletContext = servletContext
        webappContext.register(ServletContext::class.java)
        webappContext.parent = context
        webappContext.refresh()

        val debugController = webappContext.getBean(DebugController::class.java)
        Assert.assertNotNull(debugController)
    }
}
