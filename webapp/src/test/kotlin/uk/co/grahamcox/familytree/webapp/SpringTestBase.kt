package uk.co.grahamcox.familytree.webapp

import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.rules.SpringClassRule
import org.springframework.test.context.junit4.rules.SpringMethodRule
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * Base Class for all tests that depend on the Spring Context
 */
@WebAppConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = arrayOf(TestContext::class))
open class SpringTestBase {
    companion object {
        /** Class based rule for Spring Runner */
        @ClassRule
        @JvmField
        public val SCR = SpringClassRule()
    }

    /** Method based rule for Spring Runner */
    @Rule
    @JvmField
    public val springMethodRule = SpringMethodRule()


    /** The Web Application Context, used to access the webapp during tests */
    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    /** The Mock MVC Wrapper */
    private lateinit var mockMvc: MockMvc

    /**
     * Ensure that we have a Mock MVC Wrapper before the tests run
     */
    @Before
    fun setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .build()
    }

    /**
     * Actually perform the request provided
     * @param request The request to perform
     * @return the result of performing the request
     */
    fun perform(request: MockHttpServletRequestBuilder) = mockMvc.perform(request)
}
