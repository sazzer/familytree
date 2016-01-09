package uk.co.grahamcox.familytree.oauth2

import java.security.MessageDigest
import java.util.*
import kotlin.text.toByteArray

data class Password(val salt: String, val hash: String) {
    companion object {
        /**
         * Hash a plaintext password to produce a Password object
         * @param password The password to hash
         * @param salt The salt to use. If none provided then a UUID is generated
         * @return the hashed password
         */
        fun hash(password: String, salt: String = UUID.randomUUID().toString()): Password {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(salt.toByteArray("UTF-8"))
            md.update(password.toByteArray("UTF-8"))
            val digest = md.digest()

            val hash = Base64.getEncoder().encodeToString(digest)
            return Password(salt, hash)
        }
    }

    /**
     * Compare to another object for equality.
     * If the provided object is a Password then the hash and salt are compared.
     * If the provided object is a String then this is assumed to be a plaintext password, hashed with the
     * same salt as this password and then compared
     *
     * @param other the other object to compare to
     * @return true if the two objects are the same password.
     */
    override fun equals(other: Any?): Boolean = when(other) {
        is String -> equals(Password.hash(other, this.salt))
        is Password -> hash.equals(other.hash) && salt.equals(other.salt)
        else -> false
    }
}