package uk.co.grahamcox.familytree.webapp

import org.easymock.EasyMock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import uk.co.grahamcox.familytree.oauth2.client.ClientDao
import uk.co.grahamcox.familytree.oauth2.client.mongo.ClientMongoDao
import uk.co.grahamcox.familytree.spring.CoreContext
import uk.co.grahamcox.familytree.webapp.spring.ServletContext
import java.time.Clock
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Root configuration for the Spring Context to use for tests
 */
@Configuration
@Import(
        CoreContext::class,
        ServletContext::class
)
open class TestContext {
    /** The timezone to use */
    private val UTC_TIMEZONE = ZoneId.of("UTC")

    /** The clock to use for the tests */
    @Bean
    @Profile("test")
    open fun clock() = Clock.fixed(ZonedDateTime.of(2016, 1, 6, 12, 33, 25, 0, UTC_TIMEZONE).toInstant(),
            UTC_TIMEZONE)

    /**
     * Bean representing the OAuth2 Client DAO
     * @return the Client DAO
     */
    @Bean(name = arrayOf("clientDao"))
    @Profile("test")
    open fun clientDao() = createMock(ClientDao::class.java)

    /**
     * Create an EasyMock instance of the given class, already in replay mode
     * @param cls the class to create the mock of
     * @return the mock
     */
    private fun <T> createMock(cls: Class<T>): T {
        val result = EasyMock.createMock(cls)
        EasyMock.replay(result)
        return result
    }
}
