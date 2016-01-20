package uk.co.grahamcox.familytree.verification.steps.oauth2

import cucumber.api.DataTable
import cucumber.api.java.en.When
import org.springframework.beans.factory.annotation.Autowired
import uk.co.grahamcox.familytree.verification.facades.oauth2.ClientCredentialsTokenRequest
import uk.co.grahamcox.familytree.verification.facades.oauth2.OAuth2Facade

/**
 * Cucumber steps for OAuth2 Token access
 */
class OAuth2TokenSteps() {
    /** The facade layer for making OAuth2 requests */
    @Autowired
    private lateinit var oauth2Facade: OAuth2Facade

    /**
     * Perform a Client Credentials Grant
     * @param grantDetails The details of the grant
     */
    @When("^I perform a client credentials authentication for:$")
    fun clientCredentialsGrant(grantDetails: DataTable) {
        oauth2Facade.tokenRequestForResponse(ClientCredentialsTokenRequest("abc", "def"))
    }
}