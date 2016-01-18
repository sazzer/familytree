package uk.co.grahamcox.familytree.webapp.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import uk.co.grahamcox.familytree.webapp.oauth2.ClientCredentialsArgumentResolver

/**
 * The configuration to use for the Spring MVC Configuration
 */
@Configuration
@EnableWebMvc
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = false, prePostEnabled = true)
open class WebMvcContext : WebMvcConfigurerAdapter() {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(WebMvcContext::class.java)

    /** The Object Mapper to use */
    @Autowired
    lateinit var objectMapper : ObjectMapper

    /**
     * Configure all of the message converters to use. Specifically we have a custom Jackson converter for JSON
     * @param converters The list of converters to work with
     */
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        LOG.info("Configuring the Message Converters to use")
        // First, add all of the default converters we actually care about
        converters.add(ByteArrayHttpMessageConverter())
        converters.add(StringHttpMessageConverter())
        converters.add(ResourceHttpMessageConverter())
        converters.add(FormHttpMessageConverter())

        // Then our custom ones
        converters.add(MappingJackson2HttpMessageConverter(objectMapper))
    }

    /**
     * Add any custom Argument Resolvers to the webapp
     * @param argumentResolvers The list of argument resolvers to augment
     */
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(ClientCredentialsArgumentResolver())
    }
}
