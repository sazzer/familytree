package uk.co.grahamcox.familytree.webapp.oauth2

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessToken
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessTokenId
import uk.co.grahamcox.familytree.oauth2.client.ClientId
import uk.co.grahamcox.familytree.user.UserId
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * Unit tests for the Access Token Encoder
 */
@RunWith(JUnitParamsRunner::class)
class JwtAccessTokenEncoderTest {
    /** Logger */
    private val LOG = LoggerFactory.getLogger(JwtAccessTokenEncoderTest::class.java)

    /** The clock to use */
    private val clock = Clock.systemUTC()

    /** The JWT Key to use for the encoder */
    private val jwtKey = MacProvider.generateKey()

    /** The encoder to test */
    private val encoder = JwtAccessTokenEncoder(jwtKey)

    /**
     * Test encoding an access token and then decoding it again
     */
    @Test
    fun testEnDecode() {
        val input = AccessToken(
                accessTokenId = AccessTokenId(UUID.randomUUID().toString()),
                client = ClientId(UUID.randomUUID().toString()),
                user = UserId(UUID.randomUUID().toString()),
                expires = clock.instant().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
                issued = clock.instant().truncatedTo(ChronoUnit.SECONDS),
                scopes = Scopes(arrayOf("a", "b", "c"))
        )

        val encoded = encoder.encodeAccessToken(input)
        LOG.info("Encoded {} into {}", input, encoded)

        val decoded = encoder.decodeAccessToken(encoded)

        Assert.assertEquals(input, decoded)
    }

    /**
     * Test decoding an access token that isn't actually a valid JWT
     */
    @Test(expected = InvalidAccessTokenException::class)
    fun testInvalidTokenString() {
        encoder.decodeAccessToken("thisisjustwrong")
    }

    /**
     * Test decoding a perfectly valid JWT that wasn't signed by this encoder
     */
    @Test(expected = InvalidAccessTokenException::class)
    fun testIncorrectTokenString() {
        encoder.decodeAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9." +
                "TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ")
    }

    /**
     * Test encoding an access token and then decoding it again
     */
    @Test(expected = ExpiredAccessTokenException::class)
    fun testDecodeExpiredToken() {
        val input = AccessToken(
                accessTokenId = AccessTokenId(UUID.randomUUID().toString()),
                client = ClientId(UUID.randomUUID().toString()),
                user = UserId(UUID.randomUUID().toString()),
                expires = clock.instant().minusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
                issued = clock.instant().truncatedTo(ChronoUnit.SECONDS),
                scopes = Scopes(arrayOf("a", "b", "c"))
        )

        val encoded = encoder.encodeAccessToken(input)

        encoder.decodeAccessToken(encoded)
    }

    /**
     * Test encoding an access token and then decoding it again
     */
    @Test(expected = InvalidAccessTokenException::class)
    fun testDecodeUnstartedToken() {
        val input = AccessToken(
                accessTokenId = AccessTokenId(UUID.randomUUID().toString()),
                client = ClientId(UUID.randomUUID().toString()),
                user = UserId(UUID.randomUUID().toString()),
                expires = clock.instant().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
                issued = clock.instant().plusSeconds(30).truncatedTo(ChronoUnit.SECONDS),
                scopes = Scopes(arrayOf("a", "b", "c"))
        )

        val encoded = encoder.encodeAccessToken(input)

        encoder.decodeAccessToken(encoded)
    }

    /**
     * Test decoding an access token with the wrong issuer
     */
    @Test(expected = InvalidAccessTokenException::class)
    fun testWrongIssuer() {
        val expires = clock.instant().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        val issued = clock.instant().truncatedTo(ChronoUnit.SECONDS)

        val input = Jwts.builder()
                .setIssuer("AccessTokenEncoder::class.qualifiedName")
                .setSubject("userId")
                .setAudience("clientId")
                .setExpiration(Date.from(expires))
                .setIssuedAt(Date.from(issued))
                .setNotBefore(Date.from(issued))
                .setId("accessTokenId")
                .claim(Scopes::class.qualifiedName, "scopes")
                .signWith(SignatureAlgorithm.HS512, jwtKey)
                .compact()

        encoder.decodeAccessToken(input)
    }

    /**
     * Test decoding an access token with a claim missing
     */
    @Test(expected = InvalidAccessTokenException::class)
    @Parameters(method = "paramsForMissingClaims")
    fun testMissingClaim(claim: String) {
        val expires = clock.instant().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        val issued = clock.instant().truncatedTo(ChronoUnit.SECONDS)

        val input = Jwts.builder()
                .setIssuer(AccessTokenEncoder::class.qualifiedName)
                .setSubject("userId")
                .setAudience("clientId")
                .setExpiration(Date.from(expires))
                .setIssuedAt(Date.from(issued))
                .setNotBefore(Date.from(issued))
                .setId("accessTokenId")
                .claim(Scopes::class.qualifiedName, "scopes")
                .claim(claim, null)
                .signWith(SignatureAlgorithm.HS512, jwtKey)
                .compact()

        encoder.decodeAccessToken(input)
    }

    /**
     * Parameters for the test of missing claims
     */
    fun paramsForMissingClaims() = arrayOf(
            "sub",
            "aud",
            "exp",
            "iss",
            "jti",
            Scopes::class.qualifiedName
    )
}