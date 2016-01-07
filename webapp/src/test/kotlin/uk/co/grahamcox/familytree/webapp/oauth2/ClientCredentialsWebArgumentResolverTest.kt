package uk.co.grahamcox.familytree.webapp.oauth2

import org.easymock.EasyMock
import org.easymock.EasyMockRule
import org.easymock.EasyMockSupport
import org.easymock.Mock
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebArgumentResolver
import org.springframework.web.context.request.NativeWebRequest
import uk.co.grahamcox.familytree.oauth2.client.ClientCredentials

/**
 * Unit test for the Client Credentials Web Argument Resolver
 */
class ClientCredentialsWebArgumentResolverTest : EasyMockSupport() {
    /** The resolver to test */
    private val resolver = ClientCredentialsWebArgumentResolver()

    /** The method to work with */
    @Mock
    private lateinit var method: MethodParameter

    /** The web request to work with */
    @Mock
    private lateinit var request: NativeWebRequest

    /** Rule to set up mocks with */
    @Rule
    @JvmField
    val easyMockRule = EasyMockRule(this)

    /**
     * Test when the argument is not a ClientCredentials
     */
    @Test
    fun testWrongArgument() {
        EasyMock.expect(method.parameterType)
            .andReturn(String::class.java)
        replayAll()

        val resolved = resolver.resolveArgument(method, request)
        Assert.assertEquals(WebArgumentResolver.UNRESOLVED, resolved)
    }

    /**
     * Test when the argument is a ClientCredentials but none are provided
     */
    @Test
    fun testNoCredentials() {
        EasyMock.expect(method.parameterType)
                .andReturn(ClientCredentials::class.java)
        EasyMock.expect(request.getHeader("Authorization"))
                .andReturn(null)
        replayAll()

        val resolved = resolver.resolveArgument(method, request)
        Assert.assertEquals(WebArgumentResolver.UNRESOLVED, resolved)
    }

    /**
     * Test when the argument is a ClientCredentials but the ones that are provided we can't support
     */
    @Test
    fun testNotSupportedCredentials() {
        EasyMock.expect(method.parameterType)
                .andReturn(ClientCredentials::class.java)
        EasyMock.expect(request.getHeader("Authorization"))
                .andReturn("OtherScheme")
        replayAll()

        val resolved = resolver.resolveArgument(method, request)
        Assert.assertEquals(WebArgumentResolver.UNRESOLVED, resolved)
    }

    /**
     * Test when the argument is a ClientCredentials and we can provide them
     */
    @Test
    fun testValidCredentials() {
        EasyMock.expect(method.parameterType)
                .andReturn(ClientCredentials::class.java)
        EasyMock.expect(request.getHeader("Authorization"))
                .andReturn("Basic YWJjZDoxMjM0") // abcd:1234
        replayAll()

        val resolved = resolver.resolveArgument(method, request)
        Assert.assertEquals(ClientCredentials("abcd", "1234"), resolved)
    }
}