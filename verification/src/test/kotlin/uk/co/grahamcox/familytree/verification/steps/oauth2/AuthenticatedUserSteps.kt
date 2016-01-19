package uk.co.grahamcox.familytree.verification.steps.oauth2

import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.junit.Assert
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import uk.co.grahamcox.familytree.verification.facades.Requester
import kotlin.collections.get

/**
 * Step definitions for checking the authentication state
 */
class AuthenticatedUserSteps {
    /** the logger to use  */
    private val LOG = LoggerFactory.getLogger(AuthenticatedUserSteps::class.java)

    /** The authentication details that we last retrieved */
    private var authentication: Map<*, *>? = null

    /** The Requester to use */
    @Autowired
    private lateinit var requester: Requester

    /**
     * Look up the details of who this session is authenticated as
     */
    @When("^I look up who I'm logged in as$")
    fun lookUpAuthenticatedUser() {
        val authentication = requester.get("/api/debug/whoami")

        LOG.debug("Authentication details: {}", authentication)
        Assert.assertEquals(HttpStatus.OK, authentication.statusCode)
        Assert.assertNotNull(authentication.body)
        this.authentication = authentication.body
    }

    /**
     * Assert if the current user is authenticated or not
     */
    @Then("^I am not authenticated$")
    fun assertNotAuthenticated() {
        Assert.assertNotNull(authentication)
        Assert.assertEquals(false, authentication?.get("authenticated"))
    }
}
