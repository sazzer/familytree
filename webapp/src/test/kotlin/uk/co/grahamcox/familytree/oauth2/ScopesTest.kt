package uk.co.grahamcox.familytree.oauth2

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.collections.listOf
import kotlin.collections.setOf

/**
 * Unit tests for the scopes
 */
@RunWith(JUnitParamsRunner::class)
class ScopesTest {
    /**
     * Test a simple construction of the scopes
     */
    @Test
    fun testSimpleScopes() {
        val scopes = Scopes(setOf("a", "b", "c"))
        Assert.assertEquals(setOf("a", "b", "c"), scopes.scopes)
    }

    /**
     * Test a construction from a list, not a set
     */
    @Test
    fun testList() {
        val scopes = Scopes(listOf("a", "b", "c"))
        Assert.assertEquals(setOf("a", "b", "c"), scopes.scopes)
    }

    /**
     * Test a construction from an array, not a set
     */
    @Test
    @Parameters(method = "parametersForBuild")
    fun testArray(scopesArray: Array<String>) {
        val scopes = Scopes(scopesArray)
        Assert.assertEquals(setOf("a", "b", "c"), scopes.scopes)
    }

    /**
     * Test building a string from the scopes
     */
    @Test
    @Parameters(method = "parametersForBuild")
    fun testToString(scopesArray: Array<String>) {
        val scopes = Scopes(scopesArray)
        Assert.assertEquals("a b c", scopes.toString())
    }

    /**
     * Test parsing a string
     */
    @Test
    @Parameters(method = "parametersForParse")
    fun testParse(scopesString: String) {
        val scopes = Scopes(scopesString)
        Assert.assertEquals(setOf("a", "b", "c"), scopes.scopes)
    }

    /**
     * Parameters for parsing of strings. All parameters should represent the scopes "a b c"
     */
    fun parametersForParse() = arrayOf(
        "a b c",
        "c b a",
        "a\tb\tc",
        "a a b b c c",
        "a b c\tc\tb\ta"
    )

    /**
     * Parameters for building scopes from an array of individual scopes. All parameters should eventually
     * represent the scopes "a b c"
     */
    fun parametersForBuild() = arrayOf(
        arrayOf("a", "b", "c"),
        arrayOf("c", "b", "a"),
        arrayOf("a", "b", "c", "c", "b", "a"),
        arrayOf("a", "a", "b", "b", "c", "c"),
        arrayOf("  a", "b  ", "  c  "),
        arrayOf("a", "b", "c", "  a", "b  ", "  c  ")
    )
}