package uk.co.grahamcox.familytree.webapp.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessTokenIssuer
import uk.co.grahamcox.familytree.oauth2.client.ClientDetailsLoader
import uk.co.grahamcox.familytree.oauth2.client.ClientDetailsLoaderImpl
import uk.co.grahamcox.familytree.webapp.DebugController
import uk.co.grahamcox.familytree.webapp.oauth2.AccessTokenEncoder
import uk.co.grahamcox.familytree.webapp.oauth2.OAuth2Controller
import java.time.Clock

/**
 * The configuration to use for the Spring MVC Controllers
 */
@Configuration
open class ControllersContext {
    /**
     * Create the Debug Controller
     * @param clock The clock
     * @return the debug controller
     */
    @Autowired
    @Bean
    open fun debugController(clock: Clock) = DebugController(clock)

    /**
     * Create the OAuth2 Controller
     * @param clock The clock
     * @return the controller
     */
    @Autowired
    @Bean
    open fun oauth2Controller(clientDetailsLoader: ClientDetailsLoader,
                              accessTokenIssuer: AccessTokenIssuer,
                              clock: Clock) = OAuth2Controller(clientDetailsLoader, accessTokenIssuer, AccessTokenEncoder(), clock)
}
