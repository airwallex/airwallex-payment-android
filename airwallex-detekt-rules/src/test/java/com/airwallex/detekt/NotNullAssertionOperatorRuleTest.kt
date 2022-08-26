package com.airwallex.detekt

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotNullAssertionOperatorRuleTest {
    @Test
    fun noWildcardImportsRule() {
        val findings = NotNullAssertionOperatorRule().compileAndLint("""
                fun foo(a: String?) {
                    val x: String? = a!!
                } 
        """.trimIndent())
        assertEquals(findings.size, 1)
        assertTrue(findings[0].message.isNotBlank())
    }
}