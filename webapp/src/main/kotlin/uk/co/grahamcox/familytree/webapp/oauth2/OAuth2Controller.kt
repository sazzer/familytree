package uk.co.grahamcox.familytree.webapp.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.client.ClientCredentials
import kotlin.collections.*

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
    val expiresIn: Int,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("scope")
    val scope: String? = null,

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
class UnknownGrantTypeException(grantType: String) :
    OAuth2Exception("unsupported_grant_type", "An unsupported grant type was specified: ${grantType}")

/**
 * Exception indicating that client authentication failed for this request
 */
class InvalidClientException() : OAuth2Exception("invalid_client")

/**
 * Controller for handling OAuth2 Requests
 */
@Controller
@RequestMapping("/api/oauth2")
class OAuth2Controller {
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
                     @RequestParam params: Map<String, String>) =
        when (grantType) {
            "authorization_code" -> {
                val parameters = extractParameters(mapOf(
                    "code" to true,
                    "redirect_uri" to true
                ), params)

                AccessTokenResponse(
                    accessToken = "abc123",
                    expiresIn = 3600)
            }
            "password" -> {
                val parameters = extractParameters(mapOf(
                    "username" to true,
                    "password" to true,
                    "scope" to false
                ), params)
                val scopes = parameters.get("scope")?.let { Scopes(it) }

                AccessTokenResponse(
                    accessToken = "abc123",
                    expiresIn = 3600,
                    refreshToken = "zxy098",
                    scope = scopes?.let { it.toString() })
            }
            "client_credentials" -> if (clientCredentials == null) {
                throw InvalidClientException()
            } else {
                val parameters = extractParameters(mapOf(
                    "scope" to false
                ), params)
                val scopes = parameters.get("scope")?.let { Scopes(it) }

                AccessTokenResponse(
                    accessToken = "abcdef",
                    refreshToken = clientCredentials.clientId + "/" + clientCredentials.clientSecret,
                    expiresIn = 3600,
                    scope = scopes?.let { it.toString() })
            }
            "refresh_token" -> {
                val parameters = extractParameters(mapOf(
                    "refresh_token" to true,
                    "scope" to false
                ), params)
                val scopes = parameters.get("scope")?.let { Scopes(it) }

                AccessTokenResponse(
                    accessToken = "zxy098",
                    expiresIn = 3600,
                    scope = scopes?.let { it.toString() })
            }
            null -> throw NoGrantTypeException()
            else -> throw UnknownGrantTypeException(grantType)
        }

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
                result.put(it.key, value)
            } else if (it.value) {
                missing.add(it.key)
            }
        }

        if (missing.isNotEmpty()) {
            throw MissingParametersException(missing)
        }

        return result
    }
}
