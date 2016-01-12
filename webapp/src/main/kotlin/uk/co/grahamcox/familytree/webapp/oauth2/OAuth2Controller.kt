package uk.co.grahamcox.familytree.webapp.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessToken
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessTokenIssuer
import uk.co.grahamcox.familytree.oauth2.client.ClientCredentials
import uk.co.grahamcox.familytree.oauth2.client.ClientDetails
import uk.co.grahamcox.familytree.oauth2.client.ClientDetailsLoader
import java.time.Clock
import java.time.Duration
import kotlin.collections.*
import kotlin.text.isEmpty

/**
 * Response indicating that an error occurred
 * @property error The error code
 * @property description The error description
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    @JsonProperty("error")
    val error: String,

    @JsonProperty("error_description")
    val description: String?
)

/**
 * Response representing an Access Token
 * @property accessToken The access token itself
 * @property type The type of access token. Defaults to 'Bearer' if not specified
 * @property expiresIn How many seconds until the token expires
 * @property refreshToken The refresh token, if there is one
 * @property scope The scopes of the token
 * @property state The state from the token request
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccessTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val type: String = "Bearer",

    @JsonProperty("expires_in")
    val expiresIn: Long,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("scope")
    val scope: String,

    @JsonProperty("state")
    val state: String? = null
)
/**
 * Base class for all OAuth2 Exceptions
 * @param errorCode The error code
 * @param message The error message
 */
open class OAuth2Exception(val errorCode: String, message: String? = null) : Exception(message)

/**
 * Exception indicating that there was no Grant Type specified on a request
 */
class NoGrantTypeException : OAuth2Exception("invalid_request", "No Grant Type was specified")

/**
 * Exception indicating that there was a required parameter that was not specified on a request
 * @param params the parameters that were missing
 */
class MissingParametersException(params: List<String>) : OAuth2Exception("invalid_request", "Missing required parameters: ${params}")

/**
 * Exception indicating that there was an unsupported Grant Type specified on a request
 * @param grantType The grant type that was specified
 */
class UnsupportedGrantTypeException(grantType: String) :
    OAuth2Exception("unsupported_grant_type", "An unsupported grant type was specified: ${grantType}")

/**
 * Exception indicating that client authentication failed for this request
 */
class InvalidClientException() : OAuth2Exception("invalid_client")

/**
 * Exception indicating that scopes requested for this request were not valid
 */
class InvalidScopeException() : OAuth2Exception("invalid_scope")

/**
 * Controller for handling OAuth2 Requests
 * @property clientDetailsLoader Loader of Client Details
 * @property accessTokenIssuer Issuer of Access Tokens
 * @property accessTokenEncoder The means to encode Access Tokens
 * @property clock The clock to use
 */
@Controller
@RequestMapping("/api/oauth2")
class OAuth2Controller(private val clientDetailsLoader: ClientDetailsLoader,
                       private val accessTokenIssuer: AccessTokenIssuer,
                       private val accessTokenEncoder: AccessTokenEncoder,
                       private val clock: Clock) {

    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(OAuth2Controller::class.java)

    /**
     * Handler for a generic OAuth2 Exception
     */
    @ExceptionHandler(OAuth2Exception::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleOAuth2Exception(e: OAuth2Exception) = ErrorResponse(e.errorCode, e.message)

    /**
     * Handler for an Invalid Client Exception
     */
    @ExceptionHandler(InvalidClientException::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleInvalidClientException(e: InvalidClientException) = ErrorResponse(e.errorCode, e.message)

    /**
     * Handler for the Token endpoint
     * @param grantType The grant type to handle
     */
    @RequestMapping(value = "/token", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun tokenHandler(@RequestParam(value = "grant_type", required = false) grantType: String?,
                     clientCredentials: ClientCredentials?,
                     @RequestParam params: Map<String, String>): AccessTokenResponse {
        val clientDetails = clientCredentials?.let { clientDetailsLoader.load(it) }
        if (clientCredentials != null && clientDetails == null) {
            throw InvalidClientException()
        }

        val accessToken = when (grantType) {
            null -> throw NoGrantTypeException()
            "authorization_code" -> authorizationCodeTokenGrant(params, clientDetails)
            "password" -> passwordTokenGrant(params, clientDetails)
            "client_credentials" -> clientCredentialsTokenGrant(params, clientDetails)
            "refresh_token" -> refreshTokenGrant(params, clientDetails)
            else -> throw UnsupportedGrantTypeException(grantType)
        }

        if (accessToken.scope.isEmpty()) {
            throw InvalidScopeException()
        }
        return accessToken
    }

    /**
     * Perform an Authorization Code Token Grant
     * @param params The parameters from the request
     * @param clientDetails The Client Details, if present
     * @return the access token
     */
    fun authorizationCodeTokenGrant(params: Map<String, String>, clientDetails: ClientDetails?): AccessTokenResponse {
        val parameters = extractParameters(mapOf(
                "code" to true,
                "redirect_uri" to true
        ), params)

        throw UnsupportedGrantTypeException("authorization_code")
    }

    /**
     * Perform an Resource Owner Password Credentials Token Grant
     * @param params The parameters from the request
     * @param clientDetails The Client Details, if present
     * @return the access token
     */
    fun passwordTokenGrant(params: Map<String, String>, clientDetails: ClientDetails?): AccessTokenResponse {
        val parameters = extractParameters(mapOf(
                "username" to true,
                "password" to true,
                "scope" to false
        ), params)
        val scopes = parameters.get("scope")?.let { Scopes(it) }

        throw UnsupportedGrantTypeException("password")
    }

    /**
     * Perform a Refresh Token Grant
     * @param params The parameters from the request
     * @param clientDetails The Client Details, if present
     * @return the access token
     */
    fun refreshTokenGrant(params: Map<String, String>, clientDetails: ClientDetails?): AccessTokenResponse {
        val parameters = extractParameters(mapOf(
                "refresh_token" to true,
                "scope" to false
        ), params)
        val scopes = parameters.get("scope")?.let { Scopes(it) }

        throw UnsupportedGrantTypeException("refresh_token")
    }

    /**
     * Perform a Client Credentials Token Grant
     * @param params The parameters from the request
     * @param clientDetails The Client Details, if present
     * @return the access token
     */
    fun clientCredentialsTokenGrant(params: Map<String, String>,
                                    clientDetails: ClientDetails?): AccessTokenResponse {
        if (clientDetails == null) {
            throw InvalidClientException()
        }

        val parameters = extractParameters(mapOf(
                "scope" to false
        ), params)
        val scopes = parameters.get("scope")?.let { Scopes(it) }

        val accessToken = accessTokenIssuer.issueForClient(clientDetails, scopes)

        return buildResponse(accessToken)
    }
    /**
     * Build the Access Token Response for the given Access Token
     * @param accessToken The access token to produce a response for
     * @return the response
     */
    private fun buildResponse(accessToken: AccessToken) = AccessTokenResponse(
        accessToken = accessTokenEncoder.encodeAccessToken(accessToken),
        scope = accessToken.scopes.toString(),
        expiresIn = Duration.between(clock.instant(), accessToken.expires).seconds,
        type = "Bearer"
    )

    /**
     * Wrapper around the received parameters and the desired ones to extract only the ones of interest
     * @param required Map of the parameters that we want to extract, with the value being True if the parameter is required and False if it is optional
     * @param params The actual parameters map from the request
     * @return the resulting parameters that we have extracted
     */
    private fun extractParameters(required: Map<String, Boolean>, params: Map<String, String>): Map<String, String> {
        val result = hashMapOf<String, String>()
        val missing = arrayListOf<String>()

        required.forEach {
            val value = params.get(it.key)
            if (value != null) {
                LOG.debug("Found value {} for parameter {}", value, it.key)
                result.put(it.key, value)
            } else if (it.value) {
                LOG.debug("Found missing parameter {}", it.key)
                missing.add(it.key)
            }
        }

        if (missing.isNotEmpty()) {
            LOG.warn("Missing required parameters: {}", missing)
            throw MissingParametersException(missing)
        }

        return result
    }
}
