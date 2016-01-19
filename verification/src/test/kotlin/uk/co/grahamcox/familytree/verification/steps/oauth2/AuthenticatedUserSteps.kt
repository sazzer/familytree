package uk.co.grahamcox.familytree.verification.steps.oauth2

import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.hamcrest.collection.IsMapContaining
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import uk.co.grahamcox.familytree.verification.facades.oauth2.AuthenticationFacade

/**
 * Step definitions for checking the authentication state
 */
class AuthenticatedUserSteps {
    /** The authentication details that we last retrieved */
    private var authentication: Map<*, *>? = null

    /** The Requester to use */
    @Autowired
    private lateinit var authenticationFacade: AuthenticationFacade

    /**
     * Look up the details of who this session is authenticated as
     */
    @When("^I look up who I'm logged in as$")
    fun lookUpAuthenticatedUser() {
        authentication = authenticationFacade.getAuthentication()
    }

    /**
     * Assert if the current user is authenticated or not
     */
    @Then("^I am not authenticated$")
    fun assertNotAuthenticated() {
        Assert.assertNotNull(authentication)
        Assert.assertThat(authentication, IsMapContaining.hasEntry<Any, Any>("authenticated", false))
    }
}
