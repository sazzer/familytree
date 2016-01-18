package uk.co.grahamcox.familytree.webapp.oauth2

import org.easymock.EasyMock
import org.easymock.EasyMockRule
import org.easymock.EasyMockSupport
import org.easymock.Mock
import org.junit.*
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessToken
import uk.co.grahamcox.familytree.oauth2.accessToken.AccessTokenId
import uk.co.grahamcox.familytree.oauth2.client.ClientId
import uk.co.grahamcox.familytree.user.UserId
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.*
import javax.servlet.FilterChain

/**
 * Unit tests for the Access Token Authentication Filter
 */
class AccessTokenAuthenticationFilterTest : EasyMockSupport() {
    /** The EasyMock rule */
    @Rule
    @JvmField
    val easyMockRule = EasyMockRule(this)

    /** The Mock Request */
    private val request = MockHttpServletRequest()

    /** The Mock Response */
    private val response = MockHttpServletResponse()

    /** The Mock Filter Chain */
    @Mock
    private lateinit var filterChain: FilterChain

    /** The Mock Authentication Manager */
    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    /** The Mock Access Token Encoder */
    @Mock
    private lateinit var accessTokenEncoder: AccessTokenEncoder

    /** The authentication filter to test */
    private lateinit var testSubject: AccessTokenAuthenticationFilter

    @Before
    fun setup() {
        testSubject = AccessTokenAuthenticationFilter(authenticationManager)
        testSubject.accessTokenEncoder = accessTokenEncoder

        SecurityContextHolder.clearContext()
    }

    /**
     * Verify all of the mocks
     */
    @After
    fun verify() {
        verifyAll()
    }

    /**
     * Test when there is no authentication header present
     */
    @Test
    fun testNoAuthenticationHeader() {
        filterChain.doFilter(request, response)
        EasyMock.expectLastCall<Unit>()
        replayAll()

        testSubject.doFilter(request, response, filterChain)

        Assert.assertNull(SecurityContextHolder.getContext().authentication)
    }

    /**
     * Test when there is an authentication header present but it's not a Bearer Token
     */
    @Test
    fun testWrongAuthenticationHeader() {
        request.addHeader("Authorization", "SomethingElse")
        filterChain.doFilter(request, response)
        EasyMock.expectLastCall<Unit>()
        replayAll()

        testSubject.doFilter(request, response, filterChain)

        Assert.assertNull(SecurityContextHolder.getContext().authentication)
    }

    /**
     * Test when there is an authentication header present but it's not a valid Bearer Token
     */
    @Test
    fun testInvalidAuthenticationHeader() {
        request.addHeader("Authorization", "Bearer abc")
        EasyMock.expect(accessTokenEncoder.decodeAccessToken("abc"))
                .andThrow(InvalidAccessTokenException("Invalid access token"))
        replayAll()

        testSubject.doFilter(request, response, filterChain)

        Assert.assertEquals(401, response.status)
        Assert.assertNull(SecurityContextHolder.getContext().authentication)
    }

    /**
     * Test when the authentication token is present and correct but the authentication manager
     * rejects it
     */
    @Test
    fun testAuthenticationManagerRejects() {
        val accessToken = buildAccessToken()

        request.addHeader("Authorization", "Bearer abc")
        EasyMock.expect(accessTokenEncoder.decodeAccessToken("abc"))
                .andReturn(accessToken)
        val authenticationTokenCapture = EasyMock.newCapture<Authentication>()

        EasyMock.expect(authenticationManager.authenticate(EasyMock.capture(authenticationTokenCapture)))
                .andThrow(AccountExpiredException("Oops"))
        replayAll()

        testSubject.doFilter(request, response, filterChain)

        Assert.assertEquals(401, response.status)

        Assert.assertTrue(authenticationTokenCapture.hasCaptured())
        Assert.assertTrue(authenticationTokenCapture.value is AccessTokenAuthenticationToken)
        val accessTokenAuthenticationToken = authenticationTokenCapture.value as AccessTokenAuthenticationToken
        Assert.assertEquals(accessToken, accessTokenAuthenticationToken.credentials)
        Assert.assertNull(accessTokenAuthenticationToken.principal)
        Assert.assertFalse(accessTokenAuthenticationToken.isAuthenticated)
        Assert.assertTrue(accessTokenAuthenticationToken.authorities.isEmpty())

        Assert.assertNull(SecurityContextHolder.getContext().authentication)
    }

    /**
     * Test when the authentication token is present and correct and the authentication manager
     * accepts it
     */
    @Test
    fun testAuthenticationManagerAccepts() {
        val accessToken = buildAccessToken()

        request.addHeader("Authorization", "Bearer abc")
        EasyMock.expect(accessTokenEncoder.decodeAccessToken("abc"))
                .andReturn(accessToken)
        val authenticationTokenCapture = EasyMock.newCapture<Authentication>()

        val returnedAuthentication = EasyMock.createMock(Authentication::class.java)
        EasyMock.expect(authenticationManager.authenticate(EasyMock.capture(authenticationTokenCapture)))
                .andReturn(returnedAuthentication)

        filterChain.doFilter(request, response)
        EasyMock.expectLastCall<Unit>()
        replayAll()

        testSubject.doFilter(request, response, filterChain)

        Assert.assertEquals(returnedAuthentication, SecurityContextHolder.getContext().authentication)
    }

    /**
     * Build a valid access token
     * @return the access token
     */
    private fun buildAccessToken() : AccessToken {
        val clock = Clock.systemUTC()
        return AccessToken(
                accessTokenId = AccessTokenId(UUID.randomUUID().toString()),
                client = ClientId(UUID.randomUUID().toString()),
                user = UserId(UUID.randomUUID().toString()),
                expires = clock.instant().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
                issued = clock.instant().truncatedTo(ChronoUnit.SECONDS),
                scopes = Scopes(arrayOf("a", "b", "c"))
        )

    }
}