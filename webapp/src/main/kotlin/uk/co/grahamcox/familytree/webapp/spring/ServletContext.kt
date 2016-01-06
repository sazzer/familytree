package uk.co.grahamcox.familytree.webapp.spring

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * The root configuration to use for the Spring Servlet context
 */
@Configuration
@Import(
        WebMvcContext::class,
        ControllersContext::class
)
open class ServletContext {

}
