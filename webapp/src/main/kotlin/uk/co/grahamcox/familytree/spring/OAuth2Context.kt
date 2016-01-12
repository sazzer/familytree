package uk.co.grahamcox.familytree.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessTokenIssuer
import uk.co.grahamcox.familytree.oauth2.client.ClientDao
import uk.co.grahamcox.familytree.oauth2.client.ClientDetailsLoaderImpl
import uk.co.grahamcox.familytree.oauth2.client.mongo.ClientMongoDao
import java.time.Clock

@Configuration
open class OAuth2Context {
    /**
     * Bean representing the OAuth2 Client DAO
     * @return the Client DAO
     */
    @Bean(name = arrayOf("clientDao"))
    @Profile("!test")
    open fun clientDao() = ClientMongoDao()

    /**
     * Mechanism to load Client Details
     * @param clientDao The DAO to use
     * @return the Client Details Loader
     */
    @Autowired
    @Bean
    open fun clientDetailsLoader(clientDao: ClientDao) = ClientDetailsLoaderImpl(clientDao)

    /**
     * Mechanism to issue Access Tokens
     * @param clock The clock to use
     * @return the Access Token issuer
     */
    @Autowired
    @Bean
    open fun accessTokenIssuer(clock: Clock) = AccessTokenIssuer(clock)
}