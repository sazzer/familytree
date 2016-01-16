package uk.co.grahamcox.familytree.spring

import io.jsonwebtoken.impl.crypto.MacProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessTokenIssuer
import uk.co.grahamcox.familytree.oauth2.client.ClientDao
import uk.co.grahamcox.familytree.oauth2.client.ClientDetailsLoaderImpl
import uk.co.grahamcox.familytree.oauth2.client.mongo.ClientMongoDao
import uk.co.grahamcox.familytree.webapp.oauth2.AccessTokenEncoder
import java.time.Clock
import javax.crypto.SecretKey

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

    /**
     * The Signing Key to use for signing JWT Access Keys
     * @return the signing key
     */
    @Bean
    open fun accessTokenSigningKey() = MacProvider.generateKey()

    /**
     * The encoder to use to encode an Access Token into a string
     * @param jwtKey The signing key
     * @return the encoder
     */
    @Autowired
    @Bean
    open fun accessTokenEncoder(jwtKey: SecretKey) = AccessTokenEncoder(jwtKey)
}