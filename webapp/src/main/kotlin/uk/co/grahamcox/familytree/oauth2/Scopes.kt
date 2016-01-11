package uk.co.grahamcox.familytree.oauth2

import kotlin.collections.*
import kotlin.text.isNotBlank
import kotlin.text.split
import kotlin.text.toRegex
import kotlin.text.trim

/**
 * Representation of a set of requested scopes
 * @param scopesIn the actual set of scopes
 */
class Scopes(scopesIn: Collection<String>) {
    /** The actual set of scopes */
    val scopes = scopesIn.map { it.trim() }.filter { it.isNotBlank() }.toSet()

    /**
     * Construct the set of scopes from any array
     * @param scopes the scopes to use
     */
    constructor(scopes: Array<String>) : this(scopes.toSet())

    /**
     * Construct the set of scopes by parsing a space-separated string
     * @param scopes The scopes to parse
     */
    constructor(scopes: String) : this(scopes.split("\\s".toRegex()))

    /**
     * Generate a string of the scopes. Each scope is separated by a single space
     * @return the scopes as a string
     */
    override fun toString(): String = scopes.sorted().joinToString(" ")

    /** {@inheritDoc} */
    override fun equals(other: Any?): Boolean = when(other) {
        null -> false
        is Scopes -> this.scopes == other.scopes
        else -> false
    }

    /** {@inheritDoc} */
    override fun hashCode(): Int{
        return scopes.hashCode()
    }

}