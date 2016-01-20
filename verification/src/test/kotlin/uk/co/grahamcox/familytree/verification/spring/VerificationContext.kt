package uk.co.grahamcox.familytree.verification.spring

import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate
import uk.co.grahamcox.familytree.verification.facades.Requester
import kotlin.collections.listOf

/**
 * Response Error Handler that claims there's never an error
 */
class NoopResponseErrorHandler : DefaultResponseErrorHandler() {
    /**
     * Check the status code and claim that no error occurred
     */
    override fun hasError(statusCode: HttpStatus): Boolean = false
}

/**
 * Spring configuration for the verification tests
 */
@Configuration
@Import(
        AuthenticationContext::class
)
open class VerificationContext {
    /**
     * Build the Rest Template to use
     * @return the rest template
     */
    @Bean
    open fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.errorHandler = NoopResponseErrorHandler()

        restTemplate.messageConverters = listOf(
                FormHttpMessageConverter(),
                StringHttpMessageConverter(),
                MappingJackson2HttpMessageConverter()
        )
        return restTemplate
    }

    /**
     * Build the Requester to use
     * @param restTemplate The rest template to use
     * @return The requester to use
     */
    @Autowired
    @Bean
    open fun requester(restTemplate: RestTemplate) : Requester {
        val baseUrl = System.getProperty("test.baseUrl") ?: throw BeanCreationException("Required system property test.baseUrl was not set")

        return Requester(baseUrl, restTemplate)
    }
}
