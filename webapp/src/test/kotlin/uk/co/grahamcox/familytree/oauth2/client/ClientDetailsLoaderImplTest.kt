package uk.co.grahamcox.familytree.oauth2.client

import org.easymock.EasyMock
import org.easymock.EasyMockRule
import org.easymock.EasyMockSupport
import org.easymock.Mock
import org.junit.*
import uk.co.grahamcox.familytree.oauth2.Password
import uk.co.grahamcox.familytree.oauth2.Scopes
import uk.co.grahamcox.familytree.user.UserId
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Unit Tests for the Client Details Loader
 */
class ClientDetailsLoaderImplTest : EasyMockSupport() {
    /** The EasyMock Rule */
    @Rule
    @JvmField
    val easyMockRule = EasyMockRule(this)

    /** The Client DAO */
    @Mock
    lateinit var clientDao: ClientDao

    /** The Client Details Loader to test */
    lateinit var clientDetailsLoader: ClientDetailsLoaderImpl

    /**
     * Set up the test subject
     */
    @Before
    fun setup() {
        clientDetailsLoader = ClientDetailsLoaderImpl(clientDao)
    }

    /**
     * Verify all of the mocks
     */
    @After
    fun verify() {
        verifyAll()
    }

    /**
     * Test loading an existing client by ID
     */
    @Test
    fun testLoadById() {
        val client = ClientDetails(id = ClientId("graham"),
                secret = Password.hash("1234"),
                name = "Dummy Client",
                created = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                updated = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                owner = UserId(UUID.randomUUID().toString()),
                scopes = Scopes(arrayOf("a", "b", "c")))

        EasyMock.expect(clientDao.loadById(client.id))
                .andReturn(client)
        replayAll()

        val clientDetails = clientDetailsLoader.load(client.id)

        Assert.assertEquals(client, clientDetails)
    }

    /**
     * Test loading a non-existent client by ID
     */
    @Test
    fun testLoadByIdNotExisting() {
        EasyMock.expect(clientDao.loadById(ClientId("1234")))
                .andReturn(null)
        replayAll()

        val clientDetails = clientDetailsLoader.load(ClientId("1234"))

        Assert.assertNull(clientDetails)
    }

    /**
     * Test loading an existing client by the correct credentials
     */
    @Test
    fun testLoadByCorrectCredentials() {
        val client = ClientDetails(id = ClientId("graham"),
                secret = Password.hash("1234"),
                name = "Dummy Client",
                created = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                updated = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                owner = UserId(UUID.randomUUID().toString()),
                scopes = Scopes(arrayOf("a", "b", "c")))

        EasyMock.expect(clientDao.loadById(client.id))
                .andReturn(client)
        replayAll()

        val clientDetails = clientDetailsLoader.load(ClientCredentials(client.id, "1234"))

        Assert.assertEquals(client, clientDetails)
    }

    /**
     * Test loading an existing client by the incorrect credentials
     */
    @Test
    fun testLoadByIncorrectCredentials() {
        val client = ClientDetails(id = ClientId("graham"),
                secret = Password.hash("1234"),
                name = "Dummy Client",
                created = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                updated = ZonedDateTime.of(2016, 1, 10, 21, 29, 0, 0, ZoneId.of("UTC")).toInstant(),
                owner = UserId(UUID.randomUUID().toString()),
                scopes = Scopes(arrayOf("a", "b", "c")))

        EasyMock.expect(clientDao.loadById(client.id))
                .andReturn(client)
        replayAll()

        val clientDetails = clientDetailsLoader.load(ClientCredentials(client.id, "4321"))

        Assert.assertNull(clientDetails)
    }

    /**
     * Test loading a non-existing client by credentials
     */
    @Test
    fun testLoadByCredentialsNotExisting() {
        EasyMock.expect(clientDao.loadById(ClientId("1234")))
                .andReturn(null)
        replayAll()

        val clientDetails = clientDetailsLoader.load(ClientCredentials(ClientId("1234"), "4321"))

        Assert.assertNull(clientDetails)
    }
}