package uk.co.grahamcox.familytree.oauth2

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for the Password class
 */
@RunWith(JUnitParamsRunner::class)
class PasswordTest {
    /**
     * Test the constructor
     */
    @Test
    fun testConstruct() {
        val password = Password("salt", "hash")
        Assert.assertEquals("hash", password.hash)
        Assert.assertEquals("salt", password.salt)
    }

    /**
     * Test hashing of passwords
     */
    @Test
    @Parameters(method = "paramsForHash")
    fun testHashWithSalt(plaintext: String, salt: String, hash: String) {
        val password = Password.hash(plaintext, salt)
        Assert.assertEquals(salt, password.salt)
        Assert.assertEquals(hash, password.hash)
    }

    /**
     * Test that comparing a password to the same plaintext works correctly
     */
    @Test
    @Parameters(method = "paramsForHash")
    fun testCompareWithString(plaintext: String, salt: String, hash: String) {
        val password = Password(salt, hash)
        Assert.assertTrue(password.equals(plaintext))
    }

    /**
     * Parameters for testing hashing of passwords
     */
    fun paramsForHash() = arrayOf(
        arrayOf("password", "salt", "E2Ab2k6njlWge5iGbSvmvgdE44ZvE8AMgRyrYIoo8yI="),
        arrayOf("Password1", "salt", "fnnkob15cb8KUdm7j3Ifikd/CIWhHslZFoAxv69Ct5A="), // Caps and Numbers
        arrayOf("pɹoʍssɐd", "salt", "hRD4eK4dSyVJJTyghdI01EPeH1z7SjkKbkvp8WKqTgE="), // Unicode
        arrayOf("correct horse battery staple", "salt", "v7xUpmDi8yX9eGSpWgdbjeAPz38IKI2z/9Ocj3+wgl0=") // Long string
    )
}